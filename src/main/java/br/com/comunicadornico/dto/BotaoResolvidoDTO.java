package br.com.comunicadornico.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Botão preparado para exibição e edição no comunicador"
)
public record BotaoResolvidoDTO(

        Long idBotao,

        String legenda,

        String corFundo,

        @Schema(
                description = "Cor de fundo da tira de legenda",
                example = "#B3B4B7"
        )
        String corFundoLegenda,

        @Schema(
                description = "Cor do texto da legenda",
                example = "#000000"
        )
        String corLegenda,

        @Schema(
                description = "Indica se a legenda deve ser exibida"
        )
        boolean exibirLegenda,

        @Schema(
                description = "URL final da imagem padrão ou personalizada"
        )
        String foto,

        @Schema(
                description = "URL do áudio que deve ser reproduzido"
        )
        String audioResolvido,

        int tamanhoBotao,

        boolean oculto,

        @Schema(
                description = "Indica se existe alguma personalização para o usuário"
        )
        boolean personalizado,

        @Schema(
                description = "Indica se o botão base pertence exclusivamente ao usuário"
        )
        boolean exclusivoUsuario,

        @Schema(
                description = "Identificadores das categorias associadas ao botão"
        )
        List<Long> idsCategorias
) {
}