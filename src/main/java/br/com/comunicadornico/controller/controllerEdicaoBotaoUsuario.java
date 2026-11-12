package br.com.comunicadornico.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.comunicadornico.dto.EditarBotaoUsuarioDTO;
import br.com.comunicadornico.dto.EditarBotaoUsuarioResponseDTO;
import br.com.comunicadornico.service.serviceEditarBotaoUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(
        "/api/botoes/usuario"
)
@Tag(
        name = "Edição de botões",
        description = "Edição segura de botões para um usuário"
)
public class controllerEdicaoBotaoUsuario {

    private final serviceEditarBotaoUsuario
            serviceEditarBotaoUsuario;

    private final ObjectMapper objectMapper;

    public controllerEdicaoBotaoUsuario(
            serviceEditarBotaoUsuario
                    serviceEditarBotaoUsuario,
            ObjectMapper objectMapper) {

        this.serviceEditarBotaoUsuario =
                serviceEditarBotaoUsuario;

        this.objectMapper =
                objectMapper;
    }

    @PutMapping(
            value = "/{idBotao}",
            consumes =
                    MediaType.MULTIPART_FORM_DATA_VALUE,
            produces =
                    MediaType.APPLICATION_JSON_VALUE
    )
    @SecurityRequirement(
            name = "BearerAuth"
    )
    @Operation(
            summary = "Editar botão para um usuário",
            description = """
                    Edita um botão exclusivo do usuário
                    ou cria uma cópia exclusiva quando
                    o botão selecionado é um botão padrão.

                    A foto e o áudio personalizado são
                    opcionais. Quando não forem enviados,
                    as mídias atuais serão preservadas.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description =
                            "Botão editado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description =
                            "Dados ou arquivos inválidos."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description =
                            "Administrador não autenticado."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description =
                            "O botão, usuário ou categoria não está disponível para o administrador."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description =
                            "Botão, usuário ou categoria não encontrado."
            )
    })
    public ResponseEntity<
            EditarBotaoUsuarioResponseDTO>
            editarBotaoUsuario(

                    @PathVariable
                    Long idBotao,

                    @RequestPart("dados")
                    String dadosJson,

                    @RequestPart(
                            value = "foto",
                            required = false
                    )
                    MultipartFile foto,

                    @RequestPart(
                            value =
                                    "audioPersonalizado",
                            required = false
                    )
                    MultipartFile audioPersonalizado,

                    Principal principal) {

        validarAutenticacao(
                principal
        );

        EditarBotaoUsuarioDTO dados =
                converterDados(
                        dadosJson
                );

        EditarBotaoUsuarioResponseDTO
                resposta =
                serviceEditarBotaoUsuario
                        .editar(
                                idBotao,
                                dados,
                                foto,
                                audioPersonalizado,
                                principal.getName()
                        );

        return ResponseEntity.ok(
                resposta
        );
    }

    private EditarBotaoUsuarioDTO
            converterDados(
                    String dadosJson) {

        if (dadosJson == null
                || dadosJson.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O campo dados é obrigatório."
            );
        }

        try {
            return objectMapper.readValue(
                    dadosJson,
                    EditarBotaoUsuarioDTO.class
            );
        } catch (
                JsonProcessingException
                        erroJson
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O campo dados contém um JSON inválido.",
                    erroJson
            );
        }
    }

    private void validarAutenticacao(
            Principal principal) {

        if (principal == null
                || principal.getName()
                        == null
                || principal.getName()
                        .isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Administrador não autenticado."
            );
        }
    }
}