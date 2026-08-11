package ai.cloudforge.api.auth;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.AuthDtos.AuthResponse;
import ai.cloudforge.api.auth.AuthDtos.LoginRequest;
import ai.cloudforge.api.auth.AuthDtos.RegisterRequest;
import ai.cloudforge.api.auth.AuthService.ActiveSessionResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String REFRESH_COOKIE = "cf_refresh";

    private final AuthService authService;
    private final JwtProperties jwtProperties;

    public AuthController(AuthService authService, JwtProperties jwtProperties) {
        this.authService = authService;
        this.jwtProperties = jwtProperties;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthService.AuthenticationResult result = authService.register(request);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, refreshCookie(result.refreshToken()).toString())
                .body(result.response());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return withRefreshCookie(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request) {
        return withRefreshCookie(authService.refresh(refreshToken(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token = optionalRefreshToken(request);
        if (token != null) {
            authService.logout(token);
        }
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString()).build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(@AuthenticationPrincipal AuthPrincipal principal) {
        authService.logoutAll(principal.userId());
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString()).build();
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ActiveSessionResponse>> listSessions(@AuthenticationPrincipal AuthPrincipal principal) {
        return ResponseEntity.ok(authService.getActiveSessions(principal.userId()));
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> terminateSession(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID sessionId) {
        authService.terminateSession(principal.userId(), sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public AuthResponse currentUser(@AuthenticationPrincipal AuthPrincipal principal) {
        return authService.currentUser(principal.userId());
    }

    private ResponseEntity<AuthResponse> withRefreshCookie(AuthService.AuthenticationResult result) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(result.refreshToken()).toString())
                .body(result.response());
    }

    private String refreshToken(HttpServletRequest request) {
        String token = optionalRefreshToken(request);
        if (token == null) {
            throw new InvalidRefreshTokenException();
        }
        return token;
    }

    private String optionalRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (REFRESH_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private ResponseCookie refreshCookie(String token) {
        return ResponseCookie.from(REFRESH_COOKIE, token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/auth")
                .maxAge(jwtProperties.refreshTokenTtl())
                .build();
    }

    private ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/auth")
                .maxAge(Duration.ZERO)
                .build();
    }
}
