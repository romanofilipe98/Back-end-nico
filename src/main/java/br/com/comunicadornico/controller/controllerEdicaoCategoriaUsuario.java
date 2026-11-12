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

import br.com.comunicadornico.dto.EditarCategoriaUsuarioDTO;
import br.com.comunicadornico.dto.EditarCategoriaUsuarioResponseDTO;
import br.com.comunicadornico.service.serviceEditarCategoriaUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(
        "/api/categorias/usuario"
)
@Tag(
        name = "Edição de categorias",
        description = "Edição segura de categorias para um usuário"
)
public class controllerEdicaoCategoriaUsuario {

    private final serviceEditarCategoriaUsuario
            serviceEditarCategoriaUsuario;

    private final ObjectMapper objectMapper;

    public controllerEdicaoCategoriaUsuario(
            serviceEditarCategoriaUsuario
                    serviceEditarCategoriaUsuario,
            ObjectMapper objectMapper) {

        this.serviceEditarCategoriaUsuario =
                serviceEditarCategoriaUsuario;

        this.objectMapper =
                objectMapper;
    }

    @PutMapping(
            value = "/{idCategoria}",
            consumes =
                    MediaType.MULTIPART_FORM_DATA_VALUE,
            produces =
                    MediaType.APPLICATION_JSON_VALUE
    )
    @SecurityRequirement(
            name = "BearerAuth"
    )
    @Operation(
            summary = "Editar categoria para um usuário",
            description = """
                    Edita uma categoria exclusiva do usuário
                    ou cria uma cópia exclusiva quando a
                    categoria selecionada é uma categoria padrão.

                    A imagem é opcional. Quando nenhuma imagem
                    for enviada, a imagem atual será preservada.

                    A lista de botões substitui a lista atual
                    da categoria e deve possuir posições
                    contínuas começando em 1.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description =
                            "Categoria editada com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description =
                            "Dados, imagem ou ordem dos botões inválidos."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description =
                            "Administrador não autenticado."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description =
                            "A categoria, o usuário ou algum botão não está disponível para o administrador."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description =
                            "Categoria, usuário ou botão não encontrado."
            )
    })
    public ResponseEntity<
            EditarCategoriaUsuarioResponseDTO>
            editarCategoriaUsuario(

                    @PathVariable
                    Long idCategoria,

                    @RequestPart("dados")
                    String dadosJson,

                    @RequestPart(
                            value = "foto",
                            required = false
                    )
                    MultipartFile foto,

                    Principal principal) {

        validarAutenticacao(
                principal
        );

        EditarCategoriaUsuarioDTO dados =
                converterDados(
                        dadosJson
                );

        EditarCategoriaUsuarioResponseDTO
                resposta =
                serviceEditarCategoriaUsuario
                        .editar(
                                idCategoria,
                                dados,
                                foto,
                                principal.getName()
                        );

        return ResponseEntity.ok(
                resposta
        );
    }

    private EditarCategoriaUsuarioDTO
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
                    EditarCategoriaUsuarioDTO.class
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
                || principal.getName() == null
                || principal.getName()
                        .isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Administrador não autenticado."
            );
        }
    }
}