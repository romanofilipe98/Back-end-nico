package br.com.comunicadornico.dto;

import java.util.List;

public record TiraDeSentencaResponseDTO(
        Long idTiraDeSentenca,
        Long idUsuario,
        List<Long> sequenciaBotoes,
        String textoFormatado,
        List<BotaoResumoDTO> botoes) {
}   