package ec.edu.ups.icc.fundamentos01.users.dtos;

public class UpdateUserDto {

    private String name;
    private String email;

    // Constructor vacío
    public UpdateUserDto() {
    }

    // Constructor lleno
    public UpdateUserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters y setters
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
}
