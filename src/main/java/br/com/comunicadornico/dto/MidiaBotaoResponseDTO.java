package br.com.comunicadornico.dto;

import br.com.comunicadornico.entity.entityBotao;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Mídias padrão atualizadas do botão"
)
public record MidiaBotaoResponseDTO(

        Long idBotao,
        String legenda,

        @Schema(description = "URL pública da imagem")
        String foto,

        @Schema(description = "URL pública do áudio feminino")
        String audioFem,

        @Schema(description = "URL pública do áudio masculino")
        String audioMas
) {

    public static MidiaBotaoResponseDTO de(
            entityBotao botao) {

        return new MidiaBotaoResponseDTO(
                botao.getIdBotao(),
                botao.getLegenda(),
                botao.getFoto(),
                botao.getAudioFem(),
                botao.getAudioMas()
        );
    }
}