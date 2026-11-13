package br.com.comunicadornico.dto;

public class ExcluirRegistroUsuarioResponseDTO {

    private Long id;
    private String tipoRegistro;
    private String tipoOperacao;
    private String mensagem;

    public ExcluirRegistroUsuarioResponseDTO() {
    }

    public ExcluirRegistroUsuarioResponseDTO(
            Long id,
            String tipoRegistro,
            String tipoOperacao,
            String mensagem) {

        this.id = id;
        this.tipoRegistro = tipoRegistro;
        this.tipoOperacao = tipoOperacao;
        this.mensagem = mensagem;
    }

    public Long getId() {
        return id;
    }

    public void setId(
            Long id) {

        this.id = id;
    }

    public String getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(
            String tipoRegistro) {

        this.tipoRegistro =
                tipoRegistro;
    }

    public String getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(
            String tipoOperacao) {

        this.tipoOperacao =
                tipoOperacao;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(
            String mensagem) {

        this.mensagem = mensagem;
    }
}