package br.com.comunicadornico.dto;

import java.time.LocalDateTime;

import br.com.comunicadornico.entity.entityRegistroDeAtividade;

public record RegistroAtividadeResponseDTO(
        Long idRegistro,
        Long idUsuario,
        Long idBotao,
        String legendaBotao,
        LocalDateTime dataHora,
        int quantidadeCliques) {

    public static RegistroAtividadeResponseDTO de(entityRegistroDeAtividade r) {
        return new RegistroAtividadeResponseDTO(
                r.getIdRegistro(),
                r.getUsuario().getIdUsuario(),
                r.getBotao() != null ? r.getBotao().getIdBotao() : null,
                r.getBotao() != null ? r.getBotao().getLegenda() : null,
                r.getDataHora(),
                r.getQuantidadeCliques());
    }
}