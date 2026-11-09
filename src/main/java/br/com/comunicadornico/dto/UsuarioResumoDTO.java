package br.com.comunicadornico.dto;

import java.time.LocalDate;

public record UsuarioResumoDTO(
        Long idUsuario,
        String nome,
        String sexo,
        String nivelAutismo,
        LocalDate dataNascAut,
        boolean voz
) {
}