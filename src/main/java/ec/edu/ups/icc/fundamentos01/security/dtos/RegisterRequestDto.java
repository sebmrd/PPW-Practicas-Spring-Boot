package ec.edu.ups.icc.fundamentos01.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos requeridos para registrar un nuevo usuario")
public class RegisterRequestDto {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 150)
    @Schema(description = "Nombre del usuario", example = "User Name")
    private String name;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ingresar un email válido")
    @Schema(description = "Email del usuario", example = "user@example.com")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 100)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$", message = "Debe contener al menos una mayúscula, minúscula y un número")
    @Schema(description = "Contraseña segura con mayúscula, minúscula y número", example = "Password123")
    private String password;

    // TODO: Genera Constructor vacío, Getters y Setters
    public RegisterRequestDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
