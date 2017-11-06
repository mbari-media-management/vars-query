package org.mbari.m3.vars.query.util;

public class VARSException extends RuntimeException {

    public VARSException() {
    }

    public VARSException(String s) {
        super(s);
    }

    public VARSException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public VARSException(Throwable throwable) {
        super(throwable);
    }
}