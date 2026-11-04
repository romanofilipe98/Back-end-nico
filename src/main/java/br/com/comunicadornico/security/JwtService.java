package br.com.comunicadornico.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {// essa classe é responsável por gerar os tokens

    // A chave secreta precisa de ter pelo menos 32 caracteres para o algoritmo HS256
    private static final String CHAVE_SECRETA = "MinhaChaveSecretaSuperSeguraParaOComunicadorNico2026!";
    
    // Tempo de validade do token em milissegundos ( 2 horas = 7200000 ms)
    private static final long TEMPO_EXPIRACAO = 7200000;

    private Key obterChaveAssinatura() {
        return Keys.hmacShaKeyFor(CHAVE_SECRETA.getBytes());
    }

    public String gerarToken(String email) {
        return Jwts.builder()
                .setSubject(email) // O identificador principal (email do administrador)
                .setIssuedAt(new Date()) // Data e hora de criação
                .setExpiration(new Date(System.currentTimeMillis() + TEMPO_EXPIRACAO))
                .signWith(obterChaveAssinatura(), SignatureAlgorithm.HS256) // Assinatura criptográfica
                .compact();
    }

    public String extrairEmailDoToken(String token) { // desempacota o token e extrai o e-mail
    return Jwts.parserBuilder()
            .setSigningKey(obterChaveAssinatura())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
}
}