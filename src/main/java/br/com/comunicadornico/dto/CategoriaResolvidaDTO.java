package br.com.comunicadornico.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Categoria preparada para exibição e edição no comunicador"
)
public record CategoriaResolvidaDTO(

        Long idCategoria,

        String nome,

        @Schema(
                description = "URL final da imagem padrão ou personalizada"
        )
        String foto,

        int posicao,

        @Schema(
                description = "Indica se a categoria está oculta para o usuário"
        )
        boolean oculto,

        @Schema(
                description = "Indica se existe alguma personalização para o usuário"
        )
        boolean personalizada,

        @Schema(
                description = "Indica se a categoria base pertence exclusivamente ao usuário"
        )
        boolean exclusivaUsuario,

        @Schema(
                description = "Botões associados à categoria na ordem de exibição"
        )
        List<BotaoCategoriaOrdemDTO> botoes
) {
}