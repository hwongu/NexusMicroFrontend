package net.hwongu.nexus.ingreso.be.infrastructure.adapter.out.remote.catalogo;

import net.hwongu.nexus.ingreso.be.domain.port.out.ProductoRemotoPort;
import net.hwongu.nexus.ingreso.be.dto.ProductoRemotoDTO;
import net.hwongu.nexus.ingreso.be.exception.BadRequestException;
import net.hwongu.nexus.ingreso.be.exception.IntegracionRemotaException;
import net.hwongu.nexus.ingreso.be.infrastructure.config.RemoteServiceProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Consulta productos en el microservicio de catalogo
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Component
public class ProductoCatalogoAdapter implements ProductoRemotoPort {

    private static final String CIRCUITO_CATALOGO = "catalogoService";
    private static final String MENSAJE_CATALOGO_NO_DISPONIBLE =
            "No se pudo consultar NexusCatalogoMs. El servicio de catalogo esta caido o no disponible.";

    private final RestClient.Builder loadBalancedRestClientBuilder;
    private final RemoteServiceProperties remoteServiceProperties;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public ProductoCatalogoAdapter(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder loadBalancedRestClientBuilder,
                                   RemoteServiceProperties remoteServiceProperties,
                                   CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.loadBalancedRestClientBuilder = loadBalancedRestClientBuilder;
        this.remoteServiceProperties = remoteServiceProperties;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Override
    public ProductoRemotoDTO validarProductoExistente(Integer idProducto) {
        ProductoRemotoDTO producto;

        try {
            producto = circuitBreakerFactory.create(CIRCUITO_CATALOGO).run(
                    () -> obtenerClienteCatalogo()
                            .get()
                            .uri(remoteServiceProperties.getCatalogo().getProductoPorId(), idProducto)
                            .retrieve()
                            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                                throw new BadRequestException("El producto indicado no existe.");
                            })
                            .body(ProductoRemotoDTO.class),
                    throwable -> {
                        throw propagarExcepcionRemota(throwable);
                    }
            );
        } catch (BadRequestException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new IntegracionRemotaException(MENSAJE_CATALOGO_NO_DISPONIBLE);
        }

        if (producto == null) {
            throw new BadRequestException("El producto indicado no existe.");
        }

        return producto;
    }

    @Override
    public ProductoRemotoDTO buscarProducto(Integer idProducto) {
        ProductoRemotoDTO producto;

        try {
            producto = circuitBreakerFactory.create(CIRCUITO_CATALOGO).run(
                    () -> obtenerClienteCatalogo()
                            .get()
                            .uri(remoteServiceProperties.getCatalogo().getProductoPorId(), idProducto)
                            .retrieve()
                            .body(ProductoRemotoDTO.class),
                    throwable -> {
                        throw propagarExcepcionRemota(throwable);
                    }
            );
        } catch (RestClientException exception) {
            throw new IntegracionRemotaException(MENSAJE_CATALOGO_NO_DISPONIBLE);
        }

        if (producto == null || producto.getNombre() == null) {
            throw new IntegracionRemotaException(MENSAJE_CATALOGO_NO_DISPONIBLE);
        }

        return producto;
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
