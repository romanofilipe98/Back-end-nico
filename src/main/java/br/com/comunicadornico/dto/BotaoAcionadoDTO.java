package br.com.comunicadornico.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado do acionamento de um botão")
public record BotaoAcionadoDTO(
        Long idBotao,
        String legenda,
        String foto,
        @Schema(description = "Áudio a tocar (personalizado, ou voz masculina/feminina conforme o usuário). Nulo = usar síntese de voz no front")
        String audio,
        Long idRegistro,
        int quantidadeCliques) {
}