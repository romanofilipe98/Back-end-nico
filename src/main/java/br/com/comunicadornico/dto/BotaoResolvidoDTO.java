package br.com.comunicadornico.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Botão preparado para exibição no comunicador"
)
public record BotaoResolvidoDTO(

        Long idBotao,
        String legenda,
        String corFundo,

        @Schema(
                description = "URL final da imagem, padrão ou personalizada"
        )
        String foto,

        @Schema(
                description = "URL do áudio que deve ser reproduzido"
        )
        String audioResolvido,

        int tamanhoBotao,
        boolean oculto,

        @Schema(
                description = "Indica se existe foto ou áudio personalizado"
        )
        boolean personalizado,

        @Schema(
                description = "Identificadores das categorias associadas ao botão"
        )
        List<Long> idsCategorias
) {
}