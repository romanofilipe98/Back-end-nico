package br.com.comunicadornico.dto;

public class TokenDTO {

    private String token;
    private String tipo = "Bearer";
    private Long idAdministrador;
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
