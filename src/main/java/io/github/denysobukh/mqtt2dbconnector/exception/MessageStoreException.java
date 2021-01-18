package io.github.denysobukh.mqtt2dbconnector.exception;

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
