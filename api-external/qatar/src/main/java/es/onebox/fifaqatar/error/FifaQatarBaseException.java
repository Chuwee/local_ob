package es.onebox.fifaqatar.error;

public abstract class FifaQatarBaseException extends RuntimeException {

    protected FifaQatarBaseException() {
        super();
    }

    protected FifaQatarBaseException(String message) {
        super(message);
    }
}
