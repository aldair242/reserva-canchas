package com.reservacanchas.exception;

import com.reservacanchas.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String CONSTRAINT_SOLAPAMIENTO = "reservas_sin_solapamiento";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        ApiErrorResponse body = buildBody(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFound(
            org.springframework.web.servlet.resource.NoResourceFoundException ex, HttpServletRequest request) {
        ApiErrorResponse body = buildBody(HttpStatus.NOT_FOUND, "Recurso no encontrado: " + ex.getResourcePath(), request, null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ApiErrorResponse> handleCredencialesInvalidas(
            CredencialesInvalidasException ex, HttpServletRequest request) {
        ApiErrorResponse body = buildBody(HttpStatus.UNAUTHORIZED, ex.getMessage(), request, null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(ReservaConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleReservaConflict(
            ReservaConflictException ex, HttpServletRequest request) {
        ApiErrorResponse body = buildBody(HttpStatus.CONFLICT, ex.getMessage(), request, null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(OperacionNoPermitidaException.class)
    public ResponseEntity<ApiErrorResponse> handleOperacionNoPermitida(
            OperacionNoPermitidaException ex, HttpServletRequest request) {
        ApiErrorResponse body = buildBody(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex, HttpServletRequest request) {
        ApiErrorResponse body = buildBody(HttpStatus.FORBIDDEN, ex.getMessage(), request, null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ApiErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ApiErrorResponse.FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();
        ApiErrorResponse body = buildBody(
                HttpStatus.BAD_REQUEST, "Error de validacion", request, fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        ApiErrorResponse body = buildBody(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private static final String MENSAJE_RESERVA_OCUPADA =
            "La cancha ya está reservada en ese horario o se está reservando en este momento, intenta de nuevo";

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        if (esViolacionExclusion(ex)) {
            ApiErrorResponse body = buildBody(HttpStatus.CONFLICT, MENSAJE_RESERVA_OCUPADA, request, null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
        }

        String mensaje = "Conflicto de integridad de datos";
        ApiErrorResponse body = buildBody(HttpStatus.BAD_REQUEST, mensaje, request, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({
            org.springframework.dao.CannotAcquireLockException.class,
            org.springframework.dao.DeadlockLoserDataAccessException.class,
            org.springframework.dao.PessimisticLockingFailureException.class,
            org.springframework.orm.ObjectOptimisticLockingFailureException.class,
            org.springframework.dao.ConcurrencyFailureException.class
    })
    public ResponseEntity<ApiErrorResponse> handleLockingFailure(
            Exception ex, HttpServletRequest request) {
        ApiErrorResponse body = buildBody(HttpStatus.CONFLICT, MENSAJE_RESERVA_OCUPADA, request, null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(org.springframework.transaction.TransactionSystemException.class)
    public ResponseEntity<ApiErrorResponse> handleTransactionSystem(
            org.springframework.transaction.TransactionSystemException ex, HttpServletRequest request) {
        ApiErrorResponse body = buildBody(HttpStatus.CONFLICT, MENSAJE_RESERVA_OCUPADA, request, null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    private boolean esViolacionExclusion(Throwable ex) {
        Throwable actual = ex;
        while (actual != null) {
            if (actual instanceof java.sql.SQLException sqlEx) {
                if ("23P01".equals(sqlEx.getSQLState())) {
                    return true;
                }
            }
            if (actual.getMessage() != null && (
                    actual.getMessage().contains("23P01") ||
                    actual.getMessage().contains(CONSTRAINT_SOLAPAMIENTO) ||
                    actual.getMessage().contains("exclusion constraint") ||
                    actual.getMessage().contains("deadlock detected"))) {
                return true;
            }
            actual = actual.getCause();
        }
        return false;
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {
        log.error("Error no controlado en {}: ", request.getRequestURI(), ex);
        ApiErrorResponse body = buildBody(
                HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor: " + ex.getMessage(), request, null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private ApiErrorResponse buildBody(
            HttpStatus status, String message, HttpServletRequest request,
            List<ApiErrorResponse.FieldError> fieldErrors) {
        return new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                fieldErrors
        );
    }
}
