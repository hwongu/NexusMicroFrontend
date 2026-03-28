package net.hwongu.nexus.ingreso.be.infrastructure.adapter.out.remote.seguridad;

import net.hwongu.nexus.ingreso.be.domain.port.out.UsuarioRemotoPort;
import net.hwongu.nexus.ingreso.be.dto.UsuarioRemotoDTO;
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
 * Consulta usuarios en el microservicio de seguridad
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Component
public class UsuarioSeguridadAdapter implements UsuarioRemotoPort {

    private static final String CIRCUITO_SEGURIDAD = "seguridadService";
    private static final String MENSAJE_SEGURIDAD_NO_DISPONIBLE =
            "No se pudo consultar NexusSeguridadMs. El servicio de seguridad esta caido o no disponible.";

    private final RestClient.Builder loadBalancedRestClientBuilder;
    private final RemoteServiceProperties remoteServiceProperties;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public UsuarioSeguridadAdapter(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder loadBalancedRestClientBuilder,
                                   RemoteServiceProperties remoteServiceProperties,
                                   CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.loadBalancedRestClientBuilder = loadBalancedRestClientBuilder;
        this.remoteServiceProperties = remoteServiceProperties;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Override
    public UsuarioRemotoDTO validarUsuarioActivo(Integer idUsuario) {
        UsuarioRemotoDTO usuario;

        try {
            usuario = circuitBreakerFactory.create(CIRCUITO_SEGURIDAD).run(
                    () -> obtenerClienteSeguridad()
                            .get()
                            .uri(remoteServiceProperties.getSeguridad().getUsuarioPorId(), idUsuario)
                            .retrieve()
                            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                                throw new BadRequestException("El usuario indicado no existe.");
                            })
                            .body(UsuarioRemotoDTO.class),
                    throwable -> {
                        throw propagarExcepcionRemota(throwable);
                    }
            );
        } catch (BadRequestException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new IntegracionRemotaException(MENSAJE_SEGURIDAD_NO_DISPONIBLE);
        }

        if (usuario == null || usuario.getEstado() == null || !usuario.getEstado()) {
            throw new BadRequestException("El usuario indicado no existe o esta inactivo.");
        }

        return usuario;
    }

    @Override
    public UsuarioRemotoDTO buscarUsuario(Integer idUsuario) {
        UsuarioRemotoDTO usuario;

        try {
            usuario = circuitBreakerFactory.create(CIRCUITO_SEGURIDAD).run(
                    () -> obtenerClienteSeguridad()
                            .get()
                            .uri(remoteServiceProperties.getSeguridad().getUsuarioPorId(), idUsuario)
                            .retrieve()
                            .body(UsuarioRemotoDTO.class),
                    throwable -> {
                        throw propagarExcepcionRemota(throwable);
                    }
            );
        } catch (RestClientException exception) {
            throw new IntegracionRemotaException(MENSAJE_SEGURIDAD_NO_DISPONIBLE);
        }

        if (usuario == null || usuario.getUsername() == null) {
            throw new IntegracionRemotaException(MENSAJE_SEGURIDAD_NO_DISPONIBLE);
        }

        return usuario;
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

    private RestClient obtenerClienteSeguridad() {
        return loadBalancedRestClientBuilder.baseUrl(remoteServiceProperties.getSeguridad().getBaseUrl()).build();
    }
}
