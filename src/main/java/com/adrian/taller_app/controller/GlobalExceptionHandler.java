package com.adrian.taller_app.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.access.AccessDeniedException;

/**
 * Manejador global de excepciones para toda la aplicación.
 * Centraliza el manejo de errores y proporciona respuestas consistentes.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException ex,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 org.springframework.ui.Model model) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("title", "Recurso no encontrado");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("path", request.getRequestURI());
        return "error";
    }

    @ExceptionHandler({IllegalStateException.class, DataIntegrityViolationException.class})
    public String handleBadRequest(Exception ex,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   org.springframework.ui.Model model) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        model.addAttribute("title", "Solicitud incorrecta");
        model.addAttribute("message", resolveMessage(ex));
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("path", request.getRequestURI());
        return "error";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidation(MethodArgumentNotValidException ex,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   org.springframework.ui.Model model) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        model.addAttribute("title", "Validación fallida");
        model.addAttribute("message", "Revisa los datos del formulario.");
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("path", request.getRequestURI());
        return "error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex,
                                     HttpServletRequest request,
                                     HttpServletResponse response,
                                     org.springframework.ui.Model model) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        model.addAttribute("title", "Acceso denegado");
        model.addAttribute("message", "No tienes permisos para acceder a este recurso.");
        model.addAttribute("status", HttpStatus.FORBIDDEN.value());
        model.addAttribute("path", request.getRequestURI());
        return "error/403";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                org.springframework.ui.Model model) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("title", "Error interno");
        model.addAttribute("message", "Se produjo un error inesperado. Inténtalo de nuevo.");
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("path", request.getRequestURI());
        return "error";
    }

    private String resolveMessage(Exception ex) {
        if (ex instanceof DataIntegrityViolationException) {
            return "La operación no es válida para los datos actuales.";
        }
        return ex.getMessage() != null ? ex.getMessage() : "La operación no es válida.";
    }
}
