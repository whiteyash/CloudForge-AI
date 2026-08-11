package ai.cloudforge.api.exception;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<ValidationError> errors
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(Instant.now(), status, error, message, path, List.of());
    }

    public static ErrorResponse of(int status, String error, String message, String path, List<ValidationError> errors) {
        return new ErrorResponse(Instant.now(), status, error, message, path, errors);
    }

    public record ValidationError(String field, String message) {}
}
