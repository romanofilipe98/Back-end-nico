package br.com.comunicadornico.controller;

import br.com.comunicadornico.dto.LoginDTO;
import br.com.comunicadornico.dto.TokenDTO;
import br.com.comunicadornico.security.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class autenticacaoController {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @PostMapping
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO) {
        TokenDTO token = autenticacaoService.autenticar(loginDTO);
        return ResponseEntity.ok(token); // Retorna o status 200 OK junto com o TokenDTO
    }
}