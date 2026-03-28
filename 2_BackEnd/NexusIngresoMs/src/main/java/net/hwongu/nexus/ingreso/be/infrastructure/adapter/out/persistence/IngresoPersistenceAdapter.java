package net.hwongu.nexus.ingreso.be.infrastructure.adapter.out.persistence;

import net.hwongu.nexus.ingreso.be.domain.model.DetalleIngreso;
import net.hwongu.nexus.ingreso.be.domain.model.Ingreso;
import net.hwongu.nexus.ingreso.be.domain.port.out.IngresoPersistencePort;
import net.hwongu.nexus.ingreso.be.infrastructure.adapter.out.persistence.entity.DetalleIngresoJpaEntity;
import net.hwongu.nexus.ingreso.be.infrastructure.adapter.out.persistence.entity.IngresoJpaEntity;
import net.hwongu.nexus.ingreso.be.infrastructure.adapter.out.persistence.repository.DetalleIngresoJpaRepository;
import net.hwongu.nexus.ingreso.be.infrastructure.adapter.out.persistence.repository.IngresoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Accede a datos de ingresos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Component
public class IngresoPersistenceAdapter implements IngresoPersistencePort {

    private final IngresoJpaRepository ingresoJpaRepository;
    private final DetalleIngresoJpaRepository detalleIngresoJpaRepository;

    public IngresoPersistenceAdapter(IngresoJpaRepository ingresoJpaRepository,
                                     DetalleIngresoJpaRepository detalleIngresoJpaRepository) {
        this.ingresoJpaRepository = ingresoJpaRepository;
        this.detalleIngresoJpaRepository = detalleIngresoJpaRepository;
    }

    @Override
    public List<Ingreso> findAllIngresosOrderByIdDesc() {
        return ingresoJpaRepository.findAllByOrderByIdIngresoDesc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsIngresoById(Integer idIngreso) {
        return ingresoJpaRepository.existsById(idIngreso);
    }

    @Override
    public Optional<Ingreso> findIngresoById(Integer idIngreso) {
        return ingresoJpaRepository.findById(idIngreso).map(this::toDomain);
    }

    @Override
    public List<DetalleIngreso> findDetallesByIngresoId(Integer idIngreso) {
        return detalleIngresoJpaRepository.findAllByIngresoIdIngresoOrderByIdDetalleAsc(idIngreso)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Ingreso saveIngreso(Ingreso ingreso) {
        return toDomain(ingresoJpaRepository.save(toJpa(ingreso)));
    }

    @Override
    public List<DetalleIngreso> saveDetalles(List<DetalleIngreso> detalles) {
        List<DetalleIngresoJpaEntity> entities = detalles.stream()
                .map(this::toJpa)
                .toList();

        return detalleIngresoJpaRepository.saveAll(entities)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private Ingreso toDomain(IngresoJpaEntity entity) {
        return Ingreso.builder()
                .idIngreso(entity.getIdIngreso())
                .idUsuario(entity.getIdUsuario())
                .fechaIngreso(entity.getFechaIngreso())
                .estado(entity.getEstado())
                .build();
    }

    private DetalleIngreso toDomain(DetalleIngresoJpaEntity entity) {
        return DetalleIngreso.builder()
                .idDetalle(entity.getIdDetalle())
                .idIngreso(entity.getIngreso().getIdIngreso())
                .idProducto(entity.getIdProducto())
                .cantidad(entity.getCantidad())
                .precioCompra(entity.getPrecioCompra())
                .build();
    }

    private IngresoJpaEntity toJpa(Ingreso ingreso) {
        return IngresoJpaEntity.builder()
                .idIngreso(ingreso.getIdIngreso())
                .idUsuario(ingreso.getIdUsuario())
                .fechaIngreso(ingreso.getFechaIngreso())
                .estado(ingreso.getEstado())
                .build();
    }

    private DetalleIngresoJpaEntity toJpa(DetalleIngreso detalle) {
        return DetalleIngresoJpaEntity.builder()
                .idDetalle(detalle.getIdDetalle())
                .ingreso(ingresoJpaRepository.getReferenceById(detalle.getIdIngreso()))
                .idProducto(detalle.getIdProducto())
                .cantidad(detalle.getCantidad())
                .precioCompra(detalle.getPrecioCompra())
                .build();
    }
}
