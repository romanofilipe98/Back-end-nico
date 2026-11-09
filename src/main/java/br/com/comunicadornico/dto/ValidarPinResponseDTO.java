package br.com.comunicadornico.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Resultado da validação do PIN administrativo"
)
public record ValidarPinResponseDTO(

        boolean valido,

        String mensagem
) {
}