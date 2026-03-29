package net.hwongu.nexus.gateway.be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Inicia el gateway de microservicios
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@SpringBootApplication
public class NexusGatewayMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexusGatewayMsApplication.class, args);
    }

}
