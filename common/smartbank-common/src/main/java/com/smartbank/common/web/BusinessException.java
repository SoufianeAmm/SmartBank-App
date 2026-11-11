package com.smartbank.common.web;

import org.springframework.http.HttpStatus;

/**
 * Règle métier violée (solde insuffisant, compte bloqué, doublon...).
 * Par défaut HTTP 422 ; utiliser {@link HttpStatus#CONFLICT} pour les doublons.
 */
public class BusinessException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    public BusinessException(String code, String message) {
        this(code, message, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public BusinessException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
