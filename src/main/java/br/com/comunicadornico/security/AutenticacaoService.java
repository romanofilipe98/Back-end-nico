package br.com.comunicadornico.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.comunicadornico.dto.LoginDTO;
import br.com.comunicadornico.dto.TokenDTO;
import br.com.comunicadornico.entity.entityAdministrador;
import br.com.comunicadornico.repository.repositoryAdministrador;

@Service
public class AutenticacaoService {

    @Autowired
    private repositoryAdministrador repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public TokenDTO autenticar(LoginDTO loginDTO) {
        // 1. Procurar o administrador pelo email (mesma mensagem para email ou senha errados,
        // assim ninguém descobre quais e-mails existem no sistema)
        entityAdministrador admin = repository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Email ou senha incorretos."));

        // 2. Verificar se a senha digitada está certa
        if (!passwordEncoder.matches(loginDTO.getSenha(), admin.getSenha())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email ou senha incorretos.");
        }

        // 3. Se estiver tudo certo, gera o token e devolve o TokenDTO
        String token = jwtService.gerarToken(admin.getEmail());
        return new TokenDTO(token, admin.getIdAdministrador(), admin.getNome());
    }
}