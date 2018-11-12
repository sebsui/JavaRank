package ch.sebastianmue.javarank.recommendation.exceptions;

/**
 * General exception, which is thrown, when there was a problem with the model training.
 * There is likely to be a problem with the provided ch.javarank.data
 */
public class ErrorInDataSourceException extends RuntimeException {
    public ErrorInDataSourceException(Exception cause) {
        super(cause);
    }
}
