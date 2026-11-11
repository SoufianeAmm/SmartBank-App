package com.smartbank.common.web;

/** Ressource introuvable -> HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " introuvable : " + id);
    }
}
