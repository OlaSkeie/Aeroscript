package no.uio.aeroscript.error;

public class TypeError extends RuntimeException {
    public TypeError(String errorMessage) {
        super(errorMessage);
    }
}
