package br.com.comunicadornico.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.comunicadornico.dto.ValidarPinRequestDTO;
import br.com.comunicadornico.dto.ValidarPinResponseDTO;
import br.com.comunicadornico.entity.entityAdministrador;
import br.com.comunicadornico.repository.repositoryAdministrador;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/login")
public class ControllerLoginPin {

    private final repositoryAdministrador administradorRepository;

    public ControllerLoginPin(
            repositoryAdministrador administradorRepository) {

        this.administradorRepository
                = administradorRepository;
    }

    @PostMapping("/pin")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(
            summary = "Validar PIN do administrador",
            description = "Valida o PIN do administrador autenticado antes de liberar o acesso ao modo de edição."
    )
    public ResponseEntity<ValidarPinResponseDTO> validarPin(
            @RequestBody ValidarPinRequestDTO requisicao,
            Authentication authentication) {

        if (authentication == null
                || authentication.getName() == null
                || authentication.getName().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Administrador não autenticado."
            );
        }

        if (requisicao == null
                || requisicao.pin() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O PIN é obrigatório."
            );
        }

        if (requisicao.pin() < 0
                || requisicao.pin() > 9999) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O PIN deve possuir no máximo quatro dígitos."
            );
        }

        String emailAdministrador
                = authentication.getName();

        entityAdministrador administrador
                = administradorRepository
                        .findByEmailIgnoreCase(
                                emailAdministrador.trim()
                        )
                        .orElseThrow(()
                                -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Administrador autenticado não encontrado."
                                )
                        );

        if (administrador.getPin() != requisicao.pin()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "PIN inválido."
            );
        }

        return ResponseEntity.ok(
                new ValidarPinResponseDTO(
                        true,
                        "PIN validado com sucesso."
                )
        );
    }
}
