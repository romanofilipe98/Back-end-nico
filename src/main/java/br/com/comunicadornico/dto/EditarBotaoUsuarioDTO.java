package br.com.comunicadornico.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class EditarBotaoUsuarioDTO {

    @NotNull(
            message = "O usuário é obrigatório."
    )
    private Long idUsuario;

    @NotNull(
            message = "A categoria é obrigatória."
    )
    private Long idCategoria;

    @NotBlank(
            message = "A legenda do botão é obrigatória."
    )
    private String legenda;

    @NotBlank(
            message = "A cor de fundo do botão é obrigatória."
    )
    @Pattern(
            regexp = "^#[0-9A-Fa-f]{6}$",
            message = "A cor de fundo do botão deve estar no formato hexadecimal."
    )
    private String corFundo;

    @NotBlank(
            message = "A cor de fundo da legenda é obrigatória."
    )
    @Pattern(
            regexp = "^#[0-9A-Fa-f]{6}$",
            message = "A cor de fundo da legenda deve estar no formato hexadecimal."
    )
    private String corFundoLegenda;

    @NotBlank(
            message = "A cor do texto da legenda é obrigatória."
    )
    @Pattern(
            regexp = "^#[0-9A-Fa-f]{6}$",
            message = "A cor do texto da legenda deve estar no formato hexadecimal."
    )
    private String corLegenda;

    private boolean exibirLegenda;

    @Min(
            value = 1,
            message = "O tamanho do botão deve ser maior que zero."
    )
    private int tamanhoBotao = 1;

    private boolean oculto;

    public EditarBotaoUsuarioDTO() {
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(
            Long idUsuario) {

        this.idUsuario = idUsuario;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(
            Long idCategoria) {

        this.idCategoria = idCategoria;
    }

    public String getLegenda() {
        return legenda;
    }

    public void setLegenda(
            String legenda) {

        this.legenda = legenda;
    }

    public String getCorFundo() {
        return corFundo;
    }

    public void setCorFundo(
            String corFundo) {

        this.corFundo = corFundo;
    }

    public String getCorFundoLegenda() {
        return corFundoLegenda;
    }

    public void setCorFundoLegenda(
            String corFundoLegenda) {

        this.corFundoLegenda =
                corFundoLegenda;
    }

    public String getCorLegenda() {
        return corLegenda;
    }

    public void setCorLegenda(
            String corLegenda) {

        this.corLegenda = corLegenda;
    }

    public boolean isExibirLegenda() {
        return exibirLegenda;
    }

    public void setExibirLegenda(
            boolean exibirLegenda) {

        this.exibirLegenda =
                exibirLegenda;
    }

    public int getTamanhoBotao() {
        return tamanhoBotao;
    }

    public void setTamanhoBotao(
            int tamanhoBotao) {

        this.tamanhoBotao =
                tamanhoBotao;
    }

    public boolean isOculto() {
        return oculto;
    }

    public void setOculto(
            boolean oculto) {

        this.oculto = oculto;
    }
}