package br.com.comunicadornico.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BotaoCategoriaOrdemDTO {

    @NotNull(
            message =
                    "O ID do botão é obrigatório."
    )
    private Long idBotao;

    @Min(
            value = 1,
            message =
                    "A posição do botão deve ser maior que zero."
    )
    private int posicao;

    public BotaoCategoriaOrdemDTO() {
    }

    public Long getIdBotao() {
        return idBotao;
    }

    public void setIdBotao(
            Long idBotao) {

        this.idBotao = idBotao;
    }

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(
            int posicao) {

        this.posicao = posicao;
    }
}