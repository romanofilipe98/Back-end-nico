package br.com.comunicadornico.dto;

import br.com.comunicadornico.entity.entityCategoria;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Foto padrão atualizada da categoria"
)
public record MidiaCategoriaResponseDTO(

        Long idCategoria,
        String nome,

        @Schema(description = "URL pública da imagem")
        String foto,

        int posicao
) {

    public static MidiaCategoriaResponseDTO de(
            entityCategoria categoria) {

        return new MidiaCategoriaResponseDTO(
                categoria.getIdCategoria(),
                categoria.getNome(),
                categoria.getFotoCat(),
                categoria.getPosicaoCat()
        );
    }
}
