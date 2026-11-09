package br.com.comunicadornico.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Botão preparado para exibição no comunicador")
public record BotaoResolvidoDTO(

        Long idBotao,
        String legenda,
        String corFundo,

        @Schema(description = "URL final da imagem, padrão ou personalizada")
        String foto,

        @Schema(description = "URL final que deve ser reproduzida no clique")
        String audioResolvido,

        int tamanhoBotao,
        boolean oculto,

        @Schema(description = "Indica se existe foto ou áudio personalizado")
        boolean personalizado
) {
}