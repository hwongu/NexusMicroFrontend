package net.hwongu.nexus.ingreso.be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Inicia el microservicio de ingresos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class NexusIngresoMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexusIngresoMsApplication.class, args);
    }

}
