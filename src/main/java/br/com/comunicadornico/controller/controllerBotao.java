package br.com.comunicadornico.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import br.com.comunicadornico.dto.CriarBotaoUsuarioDTO;
import br.com.comunicadornico.dto.CriarBotaoUsuarioResponseDTO;
import br.com.comunicadornico.dto.MidiaBotaoResponseDTO;
import br.com.comunicadornico.entity.entityBotao;
import br.com.comunicadornico.service.ServiceMidiaBotao;
import br.com.comunicadornico.service.serviceBotao;
import br.com.comunicadornico.service.serviceCriarBotaoUsuario;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@RequestMapping("/api/botoes")
@Tag(name = "Botões", description = "Endpoints para gestão, cadastro e consulta de botões")
public class controllerBotao {

    @Autowired
    private serviceBotao meuService;

    @Autowired
    private ServiceMidiaBotao serviceMidiaBotao;

    @Autowired
    private serviceCriarBotaoUsuario serviceCriarBotaoUsuario;

    @Autowired
    private ObjectMapper objectMapper;

    // 1- Criar Botao - Rota Protegida
    @PostMapping
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Criar novo Botão", description = "Registra um novo botão no banco de dados (Requer Token).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Botão criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro na validação dos dados enviados")
    })
    public ResponseEntity<entityBotao> criarBotao(@RequestBody entityBotao novoBotao) {

        entityBotao botaoCriado = meuService.criarBotao(novoBotao);

        return ResponseEntity.status(HttpStatus.CREATED).body(botaoCriado);
    }

    // 2. Read Consultar todas os botoes - Rota Protegida
    @GetMapping
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Listar Todos os botões", description = "Retorna uma lista de todos os botões cadastrados (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<entityBotao>> listarTodos() {

        List<entityBotao> lista = meuService.listarBotoes();

        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }

    // READ - Consultar por ID - Rota Protegida
    @GetMapping("/{idBotao}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Buscar por ID", description = "Retorna os dados de um botão específico pelo seu ID (Requer Token).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Botão encontrado"),
        @ApiResponse(responseCode = "404", description = "Botão não encontrado")
    })
    public ResponseEntity<entityBotao> buscarBotaoPorId(@PathVariable Long idBotao) {
        entityBotao botaoEncontrado = meuService.pesquisarBotaoPorId(idBotao);
        return ResponseEntity.ok(botaoEncontrado);
    }

    // READ - Consultar Botao pelo nome - Rota Protegida
    @GetMapping("/buscar")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Buscar por nome", description = "Pesquisa botões que contenham o nome informado (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    public ResponseEntity<List<entityBotao>> buscarPorNome(@RequestParam String legenda) {
        List<entityBotao> botoes = meuService.pesquisarBotao(legenda);

        return ResponseEntity.status(HttpStatus.OK).body(botoes);
    }

    //4. UPTADE - Atualizar um registro existente (PUT)
    @PutMapping("/{idBotao}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Atualizar botão", description = "Atualiza os dados de um botão existente pelo ID (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Botão atualizado com sucesso")
    public ResponseEntity<entityBotao> atualizarBotao(@PathVariable Long idBotao, @RequestBody entityBotao botaoAtualizado) {
        entityBotao botao = meuService.atualizarBotao(idBotao, botaoAtualizado);

        return ResponseEntity.status(HttpStatus.OK).body(botao);
    }

    // 5. DELETE - Excluir um registro (DELETE)
    @DeleteMapping("/{idBotao}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Excluir botão", description = "Remove um botão do banco de dados pelo ID (Requer Token).")
    @ApiResponse(responseCode = "204", description = "Botão excluído com sucesso")
    public ResponseEntity<Void> excluirBotao(@PathVariable Long idBotao) {
        meuService.excluirBotao(idBotao);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(
            value = "/{idBotao}/midia",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @SecurityRequirement(name = "BearerAuth")
    @Operation(
            summary = "Atualizar mídias padrão do botão",
            description = "Envia a foto e os áudios feminino e masculino para o Cloudinary. Os arquivos não enviados permanecem inalterados."
    )
    public ResponseEntity<MidiaBotaoResponseDTO>
            atualizarMidiaPadrao(
                    @PathVariable Long idBotao,
                    @RequestPart(
                            value = "foto",
                            required = false
                    ) MultipartFile foto,
                    @RequestPart(
                            value = "audioFem",
                            required = false
                    ) MultipartFile audioFem,
                    @RequestPart(
                            value = "audioMas",
                            required = false
                    ) MultipartFile audioMas) {

        entityBotao botaoAtualizado
                = serviceMidiaBotao.atualizarMidiaPadrao(
                        idBotao,
                        foto,
                        audioFem,
                        audioMas
                );

        return ResponseEntity.ok(
                MidiaBotaoResponseDTO.de(
                        botaoAtualizado
                )
        );
    }

    @PostMapping(
            value = "/usuario",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @SecurityRequirement(name = "BearerAuth")
    @Operation(
            summary = "Criar botão exclusivo para um usuário",
            description = """
                Cria um botão vinculado ao usuário informado.
                O administrador é identificado pelo token JWT.
                O botão recebe uma foto, um áudio personalizado
                e é associado à categoria selecionada.
                """
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "201",
                description = "Botão criado com sucesso."
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Dados ou arquivos inválidos."
        ),
        @ApiResponse(
                responseCode = "401",
                description = "Administrador não autenticado."
        ),
        @ApiResponse(
                responseCode = "403",
                description = "O usuário ou a categoria não pertence ao administrador."
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Usuário ou categoria não encontrado."
        ),
        @ApiResponse(
                responseCode = "415",
                description = "Formato da requisição não suportado."
        )
    })
    public ResponseEntity<CriarBotaoUsuarioResponseDTO>
            criarBotaoUsuario(
                    @RequestPart("dados") String dadosJson,
                    @RequestPart("foto") MultipartFile foto,
                    @RequestPart("audioPersonalizado") MultipartFile audioPersonalizado,
                    Principal principal) {

        if (principal == null
                || principal.getName() == null
                || principal.getName().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        CriarBotaoUsuarioDTO dados;

        try {
            dados = objectMapper.readValue(
                    dadosJson,
                    CriarBotaoUsuarioDTO.class
            );
        } catch (JsonProcessingException erroJson) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O campo dados contém um JSON inválido.",
                    erroJson
            );
        }

        CriarBotaoUsuarioResponseDTO resposta
                = serviceCriarBotaoUsuario.criar(
                        dados,
                        foto,
                        audioPersonalizado,
                        principal.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resposta);
    }
}
