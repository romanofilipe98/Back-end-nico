package br.com.comunicadornico.dto;

import java.time.LocalDate;

public record AdministradorPerfilDTO(
        Long idAdministrador,
        String nome,
        String email,
        int pin,
        String telefone,
        LocalDate dataNasc,
        String sexo
) {
}