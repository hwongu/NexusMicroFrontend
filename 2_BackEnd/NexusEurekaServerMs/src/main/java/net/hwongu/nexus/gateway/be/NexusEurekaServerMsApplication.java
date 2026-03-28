package net.hwongu.nexus.gateway.be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Inicia el servidor Eureka
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@EnableEurekaServer
@SpringBootApplication
public class NexusEurekaServerMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexusEurekaServerMsApplication.class, args);
    }

}
