package net.hwongu.nexus.ingreso.be.infrastructure.adapter.out.remote.catalogo;

import net.hwongu.nexus.ingreso.be.domain.model.DetalleIngreso;
import net.hwongu.nexus.ingreso.be.domain.port.out.StockCatalogoPort;
import net.hwongu.nexus.ingreso.be.dto.ActualizarStockRequestDTO;
import net.hwongu.nexus.ingreso.be.exception.BadRequestException;
import net.hwongu.nexus.ingreso.be.infrastructure.config.RemoteServiceProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * Actualiza stock en el microservicio de catalogo
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Component
public class StockCatalogoAdapter implements StockCatalogoPort {

    private static final String CIRCUITO_CATALOGO = "catalogoService";

    private final RestClient.Builder loadBalancedRestClientBuilder;
    private final RemoteServiceProperties remoteServiceProperties;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public StockCatalogoAdapter(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder loadBalancedRestClientBuilder,
                                RemoteServiceProperties remoteServiceProperties,
                                CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.loadBalancedRestClientBuilder = loadBalancedRestClientBuilder;
        this.remoteServiceProperties = remoteServiceProperties;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Override
    public void actualizarStock(List<DetalleIngreso> detalles, String operacion) {
        RestClient catalogoClient = obtenerClienteCatalogo();

        for (DetalleIngreso detalle : detalles) {
            ActualizarStockRequestDTO requestDTO = ActualizarStockRequestDTO.builder()
                    .cantidad(detalle.getCantidad())
                    .operacion(operacion)
                    .build();

            circuitBreakerFactory.create(CIRCUITO_CATALOGO).run(
                    () -> {
                        catalogoClient.put()
                                .uri(remoteServiceProperties.getCatalogo().getActualizarStock(), detalle.getIdProducto())
                                .body(requestDTO)
                                .retrieve()
                                .toBodilessEntity();
                        return null;
                    },
                    throwable -> {
                        throw propagarExcepcionRemota(throwable);
                    }
            );
        }
    }

    private RestClientException propagarExcepcionRemota(Throwable throwable) {
        if (throwable instanceof BadRequestException badRequestException) {
            throw badRequestException;
        }

        if (throwable instanceof RestClientException restClientException) {
            return restClientException;
        }

        return new RestClientException("La llamada remota fallo o el circuito esta abierto.", throwable) {
        };
    }

    private RestClient obtenerClienteCatalogo() {
        return loadBalancedRestClientBuilder.baseUrl(remoteServiceProperties.getCatalogo().getBaseUrl()).build();
    }
}
