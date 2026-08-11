package ai.cloudforge.api.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
class EmailAlreadyRegisteredException extends RuntimeException {
    EmailAlreadyRegisteredException() {
        super("An account with this email already exists");
    }
}

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class InvalidCredentialsException extends RuntimeException {
    InvalidCredentialsException() {
        super("Invalid email or password");
    }
}

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class InvalidRefreshTokenException extends RuntimeException {
    InvalidRefreshTokenException() {
        super("Refresh token is invalid or expired");
    }
}
