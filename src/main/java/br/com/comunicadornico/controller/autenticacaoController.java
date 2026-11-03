package br.com.comunicadornico.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.comunicadornico.dto.LoginDTO;
import br.com.comunicadornico.dto.TokenDTO;
import br.com.comunicadornico.security.AutenticacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/login")
@Tag(name = "Autenticação", description = "Endpoints para login e geração de tokens JWT")
public class autenticacaoController {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @PostMapping
    @Operation(summary = "Realizar login do administrador", description = "Recebe as credenciais (email e senha), valida na base de dados e devolve o token JWT de acesso.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso. Token gerado."),
        @ApiResponse(responseCode = "403", description = "Credenciais inválidas (email ou senha incorretos)")
    })
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO) {
        TokenDTO token = autenticacaoService.autenticar(loginDTO);
        return ResponseEntity.ok(token); // Retorna o status 200 OK junto com o TokenDTO
    }
}