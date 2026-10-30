package br.com.comunicadornico.dto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Objeto que contém as credenciais para o login")
public class LoginDTO {
    @Schema(description = "Endereço de e-mail registrado do administrador", example = "admin@teste.com")
    private String email;

    @Schema(description = "Senha em texto plano", example = "1234")
    private String senha;

    // Getters e Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
         this.senha = senha;
    }
}
