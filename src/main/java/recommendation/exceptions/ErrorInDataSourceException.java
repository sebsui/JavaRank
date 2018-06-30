package recommendation.exceptions;

public class ErrorInDataSourceException extends RuntimeException {
    public ErrorInDataSourceException(Exception cause) {
        super(cause);
    }
}
