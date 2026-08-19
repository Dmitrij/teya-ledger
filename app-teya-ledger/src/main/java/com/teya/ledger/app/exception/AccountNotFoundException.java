package com.teya.ledger.app.exception;

public final class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String message) {
        super(message);
    }
    
}
