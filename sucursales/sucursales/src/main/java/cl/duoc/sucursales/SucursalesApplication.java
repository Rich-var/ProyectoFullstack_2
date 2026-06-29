package cl.duoc.sucursales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SucursalesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SucursalesApplication.class, args);
    }

}