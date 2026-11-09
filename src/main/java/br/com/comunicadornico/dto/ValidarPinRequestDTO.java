package br.com.comunicadornico.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "PIN do administrador para liberar o modo administrativo"
)
public record ValidarPinRequestDTO(
        Integer pin
) {
}