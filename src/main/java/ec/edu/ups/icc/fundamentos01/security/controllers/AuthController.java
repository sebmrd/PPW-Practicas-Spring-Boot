package ec.edu.ups.icc.fundamentos01.security.controllers;

import ec.edu.ups.icc.fundamentos01.security.dtos.AuthResponseDto;
import ec.edu.ups.icc.fundamentos01.security.dtos.LoginRequestDto;
import ec.edu.ups.icc.fundamentos01.security.dtos.RegisterRequestDto;
import ec.edu.ups.icc.fundamentos01.security.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Autenticación", description = "Endpoints públicos para registro e inicio de sesión")
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Iniciar sesión", description = "Valida credenciales y devuelve un JWT para consumir endpoints protegidos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login correcto"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        AuthResponseDto response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario, asigna ROLE_USER y devuelve un JWT.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "El email ya está registrado")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        AuthResponseDto response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@Valid @RequestBody ec.edu.ups.icc.fundamentos01.security.dtos.RefreshTokenRequestDto request) {
        AuthResponseDto response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody ec.edu.ups.icc.fundamentos01.security.dtos.RefreshTokenRequestDto request) {
        authService.logout(request);
    }
}
