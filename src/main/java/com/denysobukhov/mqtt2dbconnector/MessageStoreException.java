package com.denysobukhov.mqtt2dbconnector;

public class MessageStoreException extends Exception {
    public MessageStoreException() {
        super();
    }

    public MessageStoreException(String m) {
        super(m);
    }

    MessageStoreException(Exception e) {
        super(e);
    }
}
