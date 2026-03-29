package net.hwongu.nexus.config.be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Inicia el servidor de configuracion
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@EnableConfigServer
@SpringBootApplication
public class NexusConfigServerMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexusConfigServerMsApplication.class, args);
    }

}
