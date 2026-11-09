package br.com.comunicadornico.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.comunicadornico.entity.entityAdministrador;
import br.com.comunicadornico.entity.entityUsuario;
import br.com.comunicadornico.repository.repositoryAdministrador;
import br.com.comunicadornico.repository.repositoryUsuario;
import br.com.comunicadornico.service.serviceUsuario;
import br.com.comunicadornico.dto.BotaoResolvidoDTO;
import br.com.comunicadornico.dto.CategoriaResolvidaDTO;
import br.com.comunicadornico.dto.UsuarioResumoDTO;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import br.com.comunicadornico.service.ServiceMidiaBotao;
import br.com.comunicadornico.service.ServiceMidiaCategoria;
import br.com.comunicadornico.service.ServiceBotaoResolvido;
import br.com.comunicadornico.service.ServiceCategoriaResolvida;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController //avisa o spring que a classe vai responder a requisitos da web
@RequestMapping("/api/usuarios") //define o endereço base Url
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Tag(name = "Usuários", description = "Endpoints para gestão, cadastro e consulta de Usuarios")
public class controllerUsuario {

    @Autowired
    private serviceUsuario meuService;
    @Autowired
    private repositoryAdministrador administradorRepository;

    @Autowired
    private repositoryUsuario usuarioRepository;

    @Autowired
    private ServiceBotaoResolvido botaoResolvidoService;

    @Autowired
    private ServiceCategoriaResolvida categoriaResolvidaService;

    @Autowired
    private ServiceMidiaBotao serviceMidiaBotao;

    @Autowired
    private ServiceMidiaCategoria serviceMidiaCategoria;

    // 1 - Metodo para criar um novo usuario (POST) - Rota Protegida
    @PostMapping //informa que o post dessa classe deve passar por aqui, é uma etiqueta de anotação
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Criar novo usuario", description = "Registra um novo Usuário no banco de dados (Requer Token).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro na validação dos dados enviados")
    })
    public ResponseEntity<entityUsuario> criarUsuario(
            @RequestBody entityUsuario novoUsuario,
            Authentication authentication // O Spring injeta a identidade do JWT aqui
    ) {
        // Extrai o e-mail (ou username) de quem fez a requisição
        String emailAdminLogado = authentication.getName();

        // Passa o objeto e o e-mail para a Service
        entityUsuario usuarioCriado = meuService.criarUsuario(novoUsuario, emailAdminLogado);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
    }

    // 2. Read Consultar todos os registros - Rota Protegida
    @GetMapping
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista de todos os usuários cadastrados (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Usuários retornados com sucesso")
    public ResponseEntity<List<entityUsuario>> listarTodos() {
        List<entityUsuario> lista = meuService.listarUsuarios();

        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }

    // READ - Consultar registros pelo nome - Rota Protegida
    @GetMapping("/buscar")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Buscar usuário por nome", description = "Pesquisa um usuário que contenham o nome informado (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    public ResponseEntity<List<entityUsuario>> buscarPorNome(@RequestParam String nome) {
        List<entityUsuario> usuarios = meuService.pesquisarUsuario(nome);
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }

    // READ - Consultar por ID
    @GetMapping("/{idUsuario}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os dados de um usuário específico pelo seu ID (Requer Token).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<entityUsuario> buscarUsuarioPorId(@PathVariable Long idUsuario) {

        entityUsuario usuarioEncontrado = meuService.pesquisarUsuarioPorId(idUsuario);

        return ResponseEntity.ok(usuarioEncontrado);

    }

    //4. UPDATE - Atualizar um registro existente (PUT)
    @PutMapping("/{idUsuario}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente pelo ID (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso")
    public ResponseEntity<entityUsuario> atualizarUsuario(@PathVariable Long idUsuario, @RequestBody entityUsuario usuarioAtualizado) {
        entityUsuario usuario = meuService.atualizarUsuario(idUsuario, usuarioAtualizado);

        return ResponseEntity.status(HttpStatus.OK).body(usuario);
    }

    // 5. DELETE - Excluir um registro (DELETE)
    @DeleteMapping("/{idUsuario}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Excluir usuário", description = "Remove um usuário do banco de dados pelo ID (Requer Token).")
    @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso")
    public ResponseEntity<Void> excluirUsuario(@PathVariable Long idUsuario) {

        meuService.excluirUsuario(idUsuario);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/meus-usuarios")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(
            summary = "Listar meus usuários",
            description = "Retorna os usuários vinculados ao administrador autenticado."
    )
    public ResponseEntity<List<UsuarioResumoDTO>>
            listarMeusUsuarios(
                    Authentication authentication) {

        String emailAdminLogado
                = authentication.getName();

        entityAdministrador admin
                = administradorRepository
                        .findByEmail(emailAdminLogado)
                        .orElseThrow(()
                                -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Administrador não encontrado para o e-mail do token: ["
                                        + emailAdminLogado
                                        + "]"
                                )
                        );

        List<UsuarioResumoDTO> meusUsuarios
                = usuarioRepository
                        .findByAdministrador(admin)
                        .stream()
                        .map(usuario
                                -> new UsuarioResumoDTO(
                                        usuario.getIdUsuario(),
                                        usuario.getNome(),
                                        usuario.getSexo(),
                                        usuario.getNivelAutismo(),
                                        usuario.getDataNascAut(),
                                        usuario.isVoz()
                                )
                        )
                        .toList();

        return ResponseEntity.ok(meusUsuarios);
    }

    @GetMapping("/{idUsuario}/botoes-resolvidos")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(
            summary = "Listar botões do comunicador",
            description = "Retorna os botões com foto e áudio resolvidos conforme as personalizações e a preferência de voz do usuário."
    )
    public ResponseEntity<List<BotaoResolvidoDTO>>
            listarBotoesResolvidos(
                    @PathVariable Long idUsuario,
                    Authentication authentication) {

        return ResponseEntity.ok(
                botaoResolvidoService
                        .listarBotoesResolvidos(
                                idUsuario,
                                authentication.getName()
                        )
        );
    }

    @GetMapping("/{idUsuario}/categorias-resolvidas")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(
            summary = "Listar categorias do comunicador",
            description = "Retorna as categorias com a foto padrão ou personalizada para o usuário."
    )
    public ResponseEntity<List<CategoriaResolvidaDTO>>
            listarCategoriasResolvidas(
                    @PathVariable Long idUsuario,
                    Authentication authentication) {

        return ResponseEntity.ok(
                categoriaResolvidaService
                        .listarCategoriasResolvidas(
                                idUsuario,
                                authentication.getName()
                        )
        );
    }

    @PostMapping(
            value = "/{idUsuario}/botoes/{idBotao}/personalizacao",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @SecurityRequirement(name = "BearerAuth")
    @Operation(
            summary = "Personalizar botão do comunicador",
            description = "Permite ao administrador configurar uma foto e um áudio personalizado para o botão do comunicador do usuário."
    )
    public ResponseEntity<BotaoResolvidoDTO>
            personalizarBotao(
                    @PathVariable Long idUsuario,
                    @PathVariable Long idBotao,
                    @RequestPart(
                            value = "foto",
                            required = false
                    ) MultipartFile foto,
                    @RequestPart(
                            value = "audioPersonalizado",
                            required = false
                    ) MultipartFile audioPersonalizado,
                    Authentication authentication) {

        BotaoResolvidoDTO resultado
                = serviceMidiaBotao.personalizarBotao(
                        idUsuario,
                        idBotao,
                        foto,
                        audioPersonalizado,
                        authentication.getName()
                );

        return ResponseEntity.ok(resultado);
    }

    @PostMapping(
            value = "/{idUsuario}/categorias/{idCategoria}/personalizacao",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @SecurityRequirement(name = "BearerAuth")
    @Operation(
            summary = "Personalizar categoria do comunicador",
            description = "Permite ao administrador configurar uma foto personalizada para a categoria do comunicador do usuário."
    )
    public ResponseEntity<CategoriaResolvidaDTO>
            personalizarCategoria(
                    @PathVariable Long idUsuario,
                    @PathVariable Long idCategoria,
                    @RequestPart("foto") MultipartFile foto,
                    Authentication authentication) {

        CategoriaResolvidaDTO resultado
                = serviceMidiaCategoria.personalizarCategoria(
                        idUsuario,
                        idCategoria,
                        foto,
                        authentication.getName()
                );

        return ResponseEntity.ok(resultado);
    }

    @DeleteMapping(
            "/{idUsuario}/botoes/{idBotao}/personalizacao"
    )
    @SecurityRequirement(name = "BearerAuth")
    @Operation(
            summary = "Remover personalização do botão",
            description = "Exclui a foto e o áudio personalizados do Cloudinary e faz o botão voltar a usar as mídias padrão."
    )
    public ResponseEntity<BotaoResolvidoDTO>
            removerPersonalizacaoBotao(
                    @PathVariable Long idUsuario,
                    @PathVariable Long idBotao,
                    Authentication authentication) {

        BotaoResolvidoDTO resultado
                = serviceMidiaBotao
                        .removerPersonalizacaoBotao(
                                idUsuario,
                                idBotao,
                                authentication.getName()
                        );

        return ResponseEntity.ok(resultado);
    }

    @DeleteMapping(
            "/{idUsuario}/categorias/{idCategoria}/personalizacao"
    )
    @SecurityRequirement(name = "BearerAuth")
    @Operation(
            summary = "Remover personalização da categoria",
            description = "Exclui a foto personalizada do Cloudinary e faz a categoria voltar a usar sua foto padrão."
    )
    public ResponseEntity<CategoriaResolvidaDTO>
            removerPersonalizacaoCategoria(
                    @PathVariable Long idUsuario,
                    @PathVariable Long idCategoria,
                    Authentication authentication) {

        CategoriaResolvidaDTO resultado
                = serviceMidiaCategoria
                        .removerPersonalizacaoCategoria(
                                idUsuario,
                                idCategoria,
                                authentication.getName()
                        );

        return ResponseEntity.ok(resultado);
    }
}
