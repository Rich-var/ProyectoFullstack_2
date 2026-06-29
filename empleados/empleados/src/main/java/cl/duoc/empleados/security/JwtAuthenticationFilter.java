package cl.duoc.empleados.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secret; // Inyectado desde application.yml / variable de entorno JWT_SECRET, nunca hardcodeado.

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            
            try {
                DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secret))
                        .build() 
                        .verify(token);

                String username = decodedJWT.getSubject();
                
                List<SimpleGrantedAuthority> authorities;
                var rolesClaim = decodedJWT.getClaim("roles");
                
                if (!rolesClaim.isMissing() && !rolesClaim.isNull()) {
                    List<String> roles = rolesClaim.asList(String.class);
                    authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();
                } else {
                    authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }

                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}