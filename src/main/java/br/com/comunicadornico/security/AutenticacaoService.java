package br.com.comunicadornico.security;

import br.com.comunicadornico.dto.LoginDTO;
import br.com.comunicadornico.dto.TokenDTO;
import br.com.comunicadornico.entity.entityAdministrador;
import br.com.comunicadornico.repository.repositoryAdministrador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AutenticacaoService {

    @Autowired
    private repositoryAdministrador repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public TokenDTO autenticar(LoginDTO loginDTO) {
        // 1. Procurar o administrador pelo email
        Optional<entityAdministrador> adminOptional = repository.findByEmail(loginDTO.getEmail());

        if (adminOptional.isEmpty()) {
            throw new RuntimeException("Email ou senha incorretos.");
        }

        entityAdministrador admin = adminOptional.get();

        // 2. Verificar se a senha digitada ta certa
        if (!passwordEncoder.matches(loginDTO.getSenha(), admin.getSenha())) {
            throw new RuntimeException("Email ou senha incorretos.");
        }
        
        // 3. Se estiver tudo certo, gera o token e devolve o TokenDTO
        String token = jwtService.gerarToken(admin.getEmail());
        return new TokenDTO(token, admin.getIdAdministrador(), admin.getNome());
    }
}