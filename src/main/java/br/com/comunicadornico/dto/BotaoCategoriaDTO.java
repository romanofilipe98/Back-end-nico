package br.com.comunicadornico.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Dados para associar um botão a uma categoria"
)
public class BotaoCategoriaDTO {

    @Schema(
            description = "ID do botão",
            example = "1"
    )
    private Long idBotao;

    @Schema(
            description = "ID da categoria",
            example = "1"
    )
    private Long idCategoria;

    @Schema(
            description = "Posição do botão dentro da categoria",
            example = "1"
    )
    private int posicao;

    public BotaoCategoriaDTO() {
    }

    public Long getIdBotao() {
        return idBotao;
    }

    public void setIdBotao(
            Long idBotao) {

        this.idBotao = idBotao;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(
            Long idCategoria) {

        this.idCategoria =
                idCategoria;
    }

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(
            int posicao) {

        this.posicao = posicao;
    }
}