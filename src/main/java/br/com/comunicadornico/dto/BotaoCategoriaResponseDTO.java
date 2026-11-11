package br.com.comunicadornico.dto;

import br.com.comunicadornico.entity.entityBotaoCategoria;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Resposta da associação entre botão e categoria"
)
public class BotaoCategoriaResponseDTO {

    private Long idBotaoCategoria;
    private Long idBotao;
    private String legendaBotao;
    private Long idCategoria;
    private String nomeCategoria;
    private int posicao;

    public BotaoCategoriaResponseDTO() {
    }

    public BotaoCategoriaResponseDTO(
            Long idBotaoCategoria,
            Long idBotao,
            String legendaBotao,
            Long idCategoria,
            String nomeCategoria,
            int posicao) {

        this.idBotaoCategoria =
                idBotaoCategoria;

        this.idBotao =
                idBotao;

        this.legendaBotao =
                legendaBotao;

        this.idCategoria =
                idCategoria;

        this.nomeCategoria =
                nomeCategoria;

        this.posicao =
                posicao;
    }

    public static BotaoCategoriaResponseDTO de(
            entityBotaoCategoria relacao) {

        return new BotaoCategoriaResponseDTO(
                relacao.getIdBotaoCategoria(),
                relacao.getBotao().getIdBotao(),
                relacao.getBotao().getLegenda(),
                relacao.getCategoria().getIdCategoria(),
                relacao.getCategoria().getNome(),
                relacao.getPosicao()
        );
    }

    public Long getIdBotaoCategoria() {
        return idBotaoCategoria;
    }

    public void setIdBotaoCategoria(
            Long idBotaoCategoria) {

        this.idBotaoCategoria =
                idBotaoCategoria;
    }

    public Long getIdBotao() {
        return idBotao;
    }

    public void setIdBotao(
            Long idBotao) {

        this.idBotao = idBotao;
    }

    public String getLegendaBotao() {
        return legendaBotao;
    }

    public void setLegendaBotao(
            String legendaBotao) {

        this.legendaBotao =
                legendaBotao;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(
            Long idCategoria) {

        this.idCategoria =
                idCategoria;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public void setNomeCategoria(
            String nomeCategoria) {

        this.nomeCategoria =
                nomeCategoria;
    }

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(
            int posicao) {

        this.posicao = posicao;
    }
}