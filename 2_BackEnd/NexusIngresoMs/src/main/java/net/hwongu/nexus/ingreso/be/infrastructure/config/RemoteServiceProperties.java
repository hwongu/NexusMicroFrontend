package net.hwongu.nexus.ingreso.be.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Centraliza propiedades de servicios remotos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@ConfigurationProperties(prefix = "app.remote")
public class RemoteServiceProperties {

    private final Seguridad seguridad = new Seguridad();
    private final Catalogo catalogo = new Catalogo();

    public Seguridad getSeguridad() {
        return seguridad;
    }

    public Catalogo getCatalogo() {
        return catalogo;
    }

    public static class Seguridad {

        private String baseUrl;
        private String usuarioPorId;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getUsuarioPorId() {
            return usuarioPorId;
        }

        public void setUsuarioPorId(String usuarioPorId) {
            this.usuarioPorId = usuarioPorId;
        }
    }

    public static class Catalogo {

        private String baseUrl;
        private String productoPorId;
        private String actualizarStock;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getProductoPorId() {
            return productoPorId;
        }

        public void setProductoPorId(String productoPorId) {
            this.productoPorId = productoPorId;
        }

        public String getActualizarStock() {
            return actualizarStock;
        }

        public void setActualizarStock(String actualizarStock) {
            this.actualizarStock = actualizarStock;
        }
    }
}
