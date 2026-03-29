package net.hwongu.nexus.ingreso.be.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa la persistencia de detalle de ingreso
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Entity
@Table(name = "detalle_ingreso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleIngresoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ingreso", nullable = false)
    private IngresoJpaEntity ingreso;

    @Column(name = "id_producto", nullable = false)
    private Integer idProducto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_compra", nullable = false)
    private Double precioCompra;
}
