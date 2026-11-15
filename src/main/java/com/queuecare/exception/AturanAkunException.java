package com.queuecare.exception;

/** Aturan keselamatan pengelolaan akun yang dilanggar, dengan pesan untuk pengguna. */
public class AturanAkunException extends RuntimeException {

    public AturanAkunException(String message) {
        super(message);
    }
}
