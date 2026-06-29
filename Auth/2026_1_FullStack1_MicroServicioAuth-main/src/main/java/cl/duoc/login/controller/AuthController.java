package cl.duoc.login.controller;

import cl.duoc.login.dto.request.DtoAuthRequest;
import cl.duoc.login.dto.response.DtoAuthResponse;
import cl.duoc.login.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<DtoAuthResponse> login(@Valid @RequestBody DtoAuthRequest request) {
        DtoAuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}