package br.com.comunicadornico.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CriarBotaoUsuarioDTO {

    @NotBlank(
            message = "A legenda do botão é obrigatória."
    )
    private String legenda;

    /*
     * Cor de fundo do botão.
     * Exemplo: #B3B4B7
     */
    @NotBlank(
            message = "A cor de fundo do botão é obrigatória."
    )
    private String corFundo;

    @Min(
            value = 1,
            message = "O tamanho do botão deve ser maior que zero."
    )
    private int tamanhoBotao = 1;

    @NotNull(
            message = "O usuário é obrigatório."
    )
    private Long idUsuario;

    @NotNull(
            message = "A categoria é obrigatória."
    )
    private Long idCategoria;

    @Min(
            value = 1,
            message = "A posição deve ser maior que zero."
    )
    private int posicao = 1;

    public CriarBotaoUsuarioDTO() {
    }

    public String getLegenda() {
        return legenda;
    }

    public void setLegenda(String legenda) {
        this.legenda = legenda;
    }

    public String getCorFundo() {
        return corFundo;
    }

    public void setCorFundo(String corFundo) {
        this.corFundo = corFundo;
    }

    public int getTamanhoBotao() {
        return tamanhoBotao;
    }

    public void setTamanhoBotao(int tamanhoBotao) {
        this.tamanhoBotao = tamanhoBotao;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(int posicao) {
        this.posicao = posicao;
    }
}