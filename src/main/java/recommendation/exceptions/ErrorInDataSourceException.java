package recommendation.exceptions;

/**
 * General exception, which is thrown, when there was a problem with the modeltraing.
 * There is likely to be a problem with the provided data
 */
public class ErrorInDataSourceException extends RuntimeException {
    public ErrorInDataSourceException(Exception cause) {
        super(cause);
    }
}
