package br.com.comunicadornico.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Objeto retornado após o login bem-sucedido, contendo o token JWT e dados básicos do usuário")
public class TokenDTO {
    
    @Schema(description = "Token JWT gerado para autenticação nas rotas protegidas", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkB0...")
    private String token;

    @Schema(description = "Tipo do token gerado", example = "Bearer")
    private String tipo = "Bearer";

    @Schema(description = "ID do administrador no banco de dados", example = "6")
    private Long idAdministrador;
    
    @Schema(description = "Nome do administrador", example = "Administrador Teste")
    private String nome;

    public TokenDTO(String token, Long idAdministrador, String nome) {
        this.token = token;
        this.idAdministrador = idAdministrador;
        this.nome = nome;
    }

    // set e get
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(Long idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
