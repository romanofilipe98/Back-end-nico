package br.com.comunicadornico.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.comunicadornico.entity.entityBotao;
import br.com.comunicadornico.service.serviceBotao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/botoes")
@Tag(name="Botões", description="Endpoints para gestão, cadastro e consulta de botões")
public class controllerBotao {

    @Autowired
    private serviceBotao meuService;

    // 1- Criar Botao - Rota Protegida
    @PostMapping
    @SecurityRequirement(name="BearerAuth")
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
    @SecurityRequirement(name="BearerAuth")
    @Operation(summary = "Listar Todos os botões", description = "Retorna uma lista de todos os botões cadastrados (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<entityBotao>> listarTodos() {

        List<entityBotao> lista = meuService.listarBotoes();

        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }

    // READ - Consultar por ID - Rota Protegida
    @GetMapping("/{idBotao}")
    @SecurityRequirement(name="BearerAuth")
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
}