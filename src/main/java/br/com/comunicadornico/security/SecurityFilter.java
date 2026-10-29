package br.com.comunicadornico.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import br.com.comunicadornico.repository.repositoryAdministrador;


import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private repositoryAdministrador repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = recuperarToken(request);

        if (token != null) {
            String email = jwtService.extrairEmailDoToken(token);
            
            // Busca o administrador na base de dados
            var administrador = repository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Administrador não encontrado"));

            // Cria a autenticação 
            var authentication = new UsernamePasswordAuthenticationToken(administrador, null, null);
            
            // Regista o administrador como logado nesta requisição
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continua o fluxo da requisição
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