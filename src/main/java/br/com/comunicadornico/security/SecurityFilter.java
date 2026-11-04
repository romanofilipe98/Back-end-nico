package br.com.comunicadornico.security;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.comunicadornico.repository.repositoryAdministrador;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private repositoryAdministrador repository;

   @Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
        String token = recuperarToken(request);
        System.out.println("🔍 Token intercetado no cabeçalho: " + token);

        if (token != null) {
            String email = jwtService.extrairEmailDoToken(token);
            System.out.println("✅ E-mail extraído do token: " + email);
            
            var administrador = repository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Administrador não encontrado"));

            //
            var authentication = new UsernamePasswordAuthenticationToken(administrador, null, new java.util.ArrayList<>());
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("🔓 Autenticação injetada no contexto com sucesso!");
        }
    } catch (Exception e) {
        System.out.println("❌ ERRO no Filtro JWT: " + e.getMessage());
        e.printStackTrace();
    }

    filterChain.doFilter(request, response);
}

    private String recuperarToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}