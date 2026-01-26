package com.adrian.taller_app.service;

import com.adrian.taller_app.domain.Cliente;
import com.adrian.taller_app.domain.OrdenTrabajo;
import com.adrian.taller_app.domain.Vehiculo;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Servicio para la generación de facturas en formato PDF.
 * Crea facturas profesionales con logo, datos del cliente y detalles de la orden de trabajo.
 */
@Service
public class FacturaPdfService {

    private static final PDFont FONT_REGULAR = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDFont FONT_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    public byte[] generarFactura(OrdenTrabajo ot) {
        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float margin = 50f;
                float pageWidth = page.getMediaBox().getWidth();
                float y = page.getMediaBox().getHeight() - margin;

                Optional<PDImageXObject> logo = loadLogo(doc);
                if (logo.isPresent()) {
                    float logoHeight = 64f;
                    float logoWidth = logo.get().getWidth() * (logoHeight / logo.get().getHeight());
                    float logoY = y - logoHeight;
                    cs.drawImage(logo.get(), margin, logoY, logoWidth, logoHeight);
                    float brandSize = 22f;
                    float textY = y - (logoHeight / 2) + (brandSize / 2) - 8f;
                    drawText(cs, FONT_BOLD, brandSize, "GarageGest", margin + logoWidth + 14f, textY);
                    y = logoY - 18f;
                } else {
                    drawText(cs, FONT_BOLD, 24f, "GarageGest", margin, y - 10f);
                    y -= 48f;
                }
                drawText(cs, FONT_BOLD, 14f, "Factura de Orden de Trabajo", margin, y);
                y -= 18f;
                drawText(cs, FONT_REGULAR, 12f, "OT #" + ot.getIdOt(), margin, y);

                y -= 20f;
                drawLine(cs, margin, y, pageWidth - margin, y);
                y -= 18f;

                Cliente cliente = ot.getVehiculo() != null ? ot.getVehiculo().getCliente() : null;
                Vehiculo vehiculo = ot.getVehiculo();

                drawText(cs, FONT_BOLD, 11f, "Cliente", margin, y);
                drawText(cs, FONT_BOLD, 11f, "Vehículo", pageWidth / 2, y);
                y -= 14f;

                y = drawWrappedText(cs, FONT_REGULAR, 11f,
                        cliente != null ? cliente.getNombre() : "-", margin, y, 240f, 14f);
                y = drawWrappedText(cs, FONT_REGULAR, 11f,
                        vehiculoLabel(vehiculo), pageWidth / 2, y + 14f, 240f, 14f);

                y -= 8f;
                drawText(cs, FONT_REGULAR, 11f, "Email: " + safe(cliente != null ? cliente.getEmail() : null), margin, y);
                drawText(cs, FONT_REGULAR, 11f, "Matrícula: " + safe(vehiculo != null ? vehiculo.getMatricula() : null), pageWidth / 2, y);
                y -= 14f;
                drawText(cs, FONT_REGULAR, 11f, "Teléfono: " + safe(cliente != null ? cliente.getTelefono() : null), margin, y);
                drawText(cs, FONT_REGULAR, 11f, "Km entrada: " + safe(ot.getKmEntrada()), pageWidth / 2, y);
                y -= 14f;
                drawText(cs, FONT_REGULAR, 11f, "NIF: " + safe(cliente != null ? cliente.getNif() : null), margin, y);
                drawText(cs, FONT_REGULAR, 11f, "Fecha: " + formatDate(ot.getFechaCreacion()), pageWidth / 2, y);

                y -= 22f;
                drawText(cs, FONT_BOLD, 12f, "Descripción", margin, y);
                y -= 14f;
                y = drawWrappedText(cs, FONT_REGULAR, 11f, safe(ot.getDescripcion(), "Sin descripción"),
                        margin, y, pageWidth - margin * 2, 14f);

                y -= 18f;
                drawLine(cs, margin, y, pageWidth - margin, y);
                y -= 16f;

                float totalBoxX = pageWidth - margin - 160f;
                drawText(cs, FONT_REGULAR, 11f, "Subtotal", totalBoxX, y);
                drawText(cs, FONT_REGULAR, 11f, formatCurrency(ot.getSubtotal()), totalBoxX + 90f, y);
                y -= 14f;
                drawText(cs, FONT_REGULAR, 11f, "IVA (21%)", totalBoxX, y);
                drawText(cs, FONT_REGULAR, 11f, formatCurrency(ot.getIvaTotal()), totalBoxX + 90f, y);
                y -= 16f;
                drawText(cs, FONT_BOLD, 12f, "Total", totalBoxX, y);
                drawText(cs, FONT_BOLD, 12f, formatCurrency(ot.getTotal()), totalBoxX + 90f, y);

                y -= 30f;
                drawText(cs, FONT_REGULAR, 10f, "Gracias por confiar en GarageGest.", margin, y);
            }

            doc.save(output);
            return output.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo generar la factura PDF.", ex);
        }
    }

    private Optional<PDImageXObject> loadLogo(PDDocument doc) {
        String[] resources = {
                "/static/images/logo-garagegest.png",
                "/static/images/logo-garagegest.jpg",
                "/static/images/logo-garagegest.jpeg"
        };
        for (String resource : resources) {
            try (InputStream stream = getClass().getResourceAsStream(resource)) {
                if (stream == null) {
                    continue;
                }
                BufferedImage image = ImageIO.read(stream);
                if (image != null) {
                    return Optional.of(LosslessFactory.createFromImage(doc, image));
                }
            } catch (IOException | IllegalArgumentException ex) {
                // ignore and try next
            }
        }
        return Optional.empty();
    }

    private void drawText(PDPageContentStream cs, PDFont font, float size, String text, float x, float y) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    private void drawLine(PDPageContentStream cs, float x1, float y1, float x2, float y2) throws IOException {
        cs.moveTo(x1, y1);
        cs.lineTo(x2, y2);
        cs.stroke();
    }

    private float drawWrappedText(PDPageContentStream cs,
                                  PDFont font,
                                  float size,
                                  String text,
                                  float x,
                                  float y,
                                  float width,
                                  float leading) throws IOException {
        List<String> lines = wrapText(font, size, text, width);
        float currentY = y;
        for (String line : lines) {
            drawText(cs, font, size, line, x, currentY);
            currentY -= leading;
        }
        return currentY;
    }

    private List<String> wrapText(PDFont font, float size, String text, float width) throws IOException {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isBlank()) {
            lines.add("-");
            return lines;
        }
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            float textWidth = font.getStringWidth(testLine) / 1000 * size;
            if (textWidth > width && line.length() > 0) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }
        if (line.length() > 0) {
            lines.add(line.toString());
        }
        return lines;
    }

    private String formatCurrency(BigDecimal value) {
        BigDecimal safe = value != null ? value : BigDecimal.ZERO;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "ES"));
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        return df.format(safe) + " €";
    }

    private String formatDate(LocalDateTime value) {
        if (value == null) {
            return "-";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return value.format(formatter);
    }

    private String vehiculoLabel(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return "-";
        }
        String marca = safe(vehiculo.getMarca());
        String modelo = safe(vehiculo.getModelo());
        if (!marca.equals("-") && !modelo.equals("-")) {
            return marca + " " + modelo;
        }
        return marca.equals("-") ? modelo : marca;
    }

    private String safe(Object value) {
        return value == null ? "-" : String.valueOf(value);
    }

    private String safe(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }
}
