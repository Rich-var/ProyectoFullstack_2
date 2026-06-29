package cl.duoc.pedidos.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration // Configuracion global para todos los Feign Client de este microservicio.
public class FeignClientConfig {

    /**
     * Sin este interceptor, las llamadas de Pedidos hacia Usuarios y Productos
     * (via Feign) salen SIN el header Authorization. Como esos microservicios
     * exigen un JWT valido (anyRequest().authenticated()), cada llamada remota
     * terminaria en 401 y "crearPedido" nunca funcionaria de extremo a extremo.
     *
     * Este bean toma el header Authorization que llego en la peticion original
     * (Gateway -> Pedidos) y lo copia tal cual en la peticion saliente
     * (Pedidos -> Usuarios / Pedidos -> Productos), preservando el flujo de JWT
     * descrito en la arquitectura: "se pide el token una vez y se reenvia en
     * cada peticion protegida".
     */
    @Bean
    public RequestInterceptor authorizationHeaderForwardingInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authorizationHeader = request.getHeader("Authorization");

                if (authorizationHeader != null) {
                    requestTemplate.header("Authorization", authorizationHeader);
                }
            }
        };
    }
}
