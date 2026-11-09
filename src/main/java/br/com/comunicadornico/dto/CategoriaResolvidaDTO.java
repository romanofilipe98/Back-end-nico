package br.com.comunicadornico.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Categoria preparada para exibição no comunicador")
public record CategoriaResolvidaDTO(

        Long idCategoria,
        String nome,

        @Schema(description = "URL final da imagem, padrão ou personalizada")
        String foto,

        int posicao,

        @Schema(description = "Indica se existe imagem personalizada")
        boolean personalizada
) {
}