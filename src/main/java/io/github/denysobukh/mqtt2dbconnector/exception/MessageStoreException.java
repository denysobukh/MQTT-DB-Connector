package io.github.denysobukh.mqtt2dbconnector.exception;

/**
 * Checked exception used for message persistence failures.
 */
public class MessageStoreException extends Exception {
    /**
     * Creates an exception without a detail message.
     */
    public MessageStoreException() {
        super();
    }

    /**
     * Creates an exception with a detail message.
     *
     * @param m detail message
     */
    public MessageStoreException(String m) {
        super(m);
    }

    /**
     * Creates an exception wrapping another exception.
     *
     * @param e original exception
     */
    MessageStoreException(Exception e) {
        super(e);
    }
}
