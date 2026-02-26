package com.playlandpark.playlandmanager.service.impl;

import com.playlandpark.playlandmanager.model.dto.reservaservicio.ReservaServicioRequest;
import com.playlandpark.playlandmanager.model.dto.reservaservicio.ReservaServicioResponse;
import com.playlandpark.playlandmanager.model.entity.Cliente;
import com.playlandpark.playlandmanager.model.entity.Producto;
import com.playlandpark.playlandmanager.model.entity.ReservaServicio;
import com.playlandpark.playlandmanager.model.enums.EstadoReserva;
import com.playlandpark.playlandmanager.repository.ClienteRepository;
import com.playlandpark.playlandmanager.repository.ProductoRepository;
import com.playlandpark.playlandmanager.repository.ReservaServicioRepository;
import com.playlandpark.playlandmanager.service.ReservaServicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

// Operaciones CRUD, validaciones de solapamiento, cancelaciones y mapeo entre entidades y DTOs.
@Service
@RequiredArgsConstructor
public class ReservaServicioServiceImpl implements ReservaServicioService {

    private static final EstadoReserva ESTADO_INICIAL = EstadoReserva.PENDIENTE;
    private static final EstadoReserva ESTADO_CANCELADA = EstadoReserva.CANCELADA;

    private final ReservaServicioRepository reservaServicioRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;


    // Crea una nueva reserva de servicio.
    // Valida la request, verifica existencia de producto y cliente, comprueba solapamientos y persiste la reserva.
    @Override
    @Transactional
    public ReservaServicioResponse create(ReservaServicioRequest request) {
        validateRequest(request);

        Producto producto = productoRepository.findById(request.idProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + request.idProducto()));

        if(!producto.getEsServicio()) throw new IllegalArgumentException("El producto a reservar debe ser un servicio.");

        Cliente cliente = clienteRepository.findById(request.idCliente())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + request.idCliente()));

        // Solapamiento de reservas
        var overlaps = reservaServicioRepository.findOverlaps(
                request.idProducto(),
                request.fechaReserva(),
                request.horaInicio(),
                request.horaFin(),
                ESTADO_CANCELADA
        );

        if (!overlaps.isEmpty()) {
            throw new IllegalArgumentException("El horario ya está reservado para este producto.");
        }

        // Formamos la entidad a partir de la request y las entidades relacionadas
        ReservaServicio r = new ReservaServicio();
        r.setProducto(producto);
        r.setCliente(cliente);
        r.setFechaReserva(request.fechaReserva());
        r.setHoraInicio(request.horaInicio());
        r.setHoraFin(request.horaFin());
        r.setCntPersonas(request.cntPersonas());
        r.setEstado(ESTADO_INICIAL);
        r.setObservaciones(cleanNullable(request.observaciones()));

        // Finalmente, mapeamos la entidad guardada a DTO de respuesta
        ReservaServicio saved = reservaServicioRepository.save(r);
        return mapToResponse(saved);
    }

    // Busca una reserva por su Id.
    @Override
    @Transactional(readOnly = true)
    public ReservaServicioResponse findById(Integer idReserva) {
        ReservaServicio r = reservaServicioRepository.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada: " + idReserva));
        return mapToResponse(r);
    }

    // Recupera todas las reservas.
    @Override
    @Transactional(readOnly = true)
    public List<ReservaServicioResponse> findAll() {
        return reservaServicioRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    // Recupera reservas asociadas a un cliente.
    @Override
    @Transactional(readOnly = true)
    public List<ReservaServicioResponse> findByCliente(Integer idCliente) {
        if (idCliente == null) throw new IllegalArgumentException("El idCliente es obligatorio.");
        return reservaServicioRepository.findByCliente_IdCliente(idCliente).stream().map(this::mapToResponse).toList();
    }

    // Recupera reservas asociadas a un producto (servicio).
    @Override
    @Transactional(readOnly = true)
    public List<ReservaServicioResponse> findByProducto(Integer idProducto) {
        if (idProducto == null) throw new IllegalArgumentException("El idProducto es obligatorio.");
        return reservaServicioRepository.findByProducto_IdProducto(idProducto).stream().map(this::mapToResponse).toList();
    }

    // Recupera reservas para una fecha concreta.
    @Override
    @Transactional(readOnly = true)
    public List<ReservaServicioResponse> findByFecha(LocalDate fecha) {
        if (fecha == null) throw new IllegalArgumentException("La fecha es obligatoria.");
        return reservaServicioRepository.findByFechaReserva(fecha).stream().map(this::mapToResponse).toList();
    }

    // Recupera reservas dentro de un rango de fechas (inclusive).
    @Override
    @Transactional(readOnly = true)
    public List<ReservaServicioResponse> findByRangoFechas(LocalDate start, LocalDate end) {
        if (start == null || end == null) throw new IllegalArgumentException("Las fechas son obligatorias.");
        if (end.isBefore(start)) throw new IllegalArgumentException("La fecha fin no puede ser menor que la fecha inicio.");

        return reservaServicioRepository.findByFechaReservaBetween(start, end).stream()
                .map(this::mapToResponse).toList();
    }

    // Recupera reservas por estado (PENDIENTE, CONFIRMADA, CANCELADA, ...).
    @Override
    @Transactional(readOnly = true)
    public List<ReservaServicioResponse> findByEstado(EstadoReserva estado) {
        if (estado == null) throw new IllegalArgumentException("El estado es obligatorio.");
        return reservaServicioRepository.findByEstado(estado).stream().map(this::mapToResponse).toList();
    }

    // Actualiza una reserva existente.
    @Override
    @Transactional
    public ReservaServicioResponse update(Integer idReserva, ReservaServicioRequest request) {
        if (idReserva == null) throw new IllegalArgumentException("El idReserva es obligatorio.");
        validateRequest(request);

        ReservaServicio r = reservaServicioRepository.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada: " + idReserva));

        if (ESTADO_CANCELADA.equals(r.getEstado())) {
            throw new IllegalArgumentException("No se puede actualizar una reserva CANCELADA.");
        }

        Producto producto = productoRepository.findById(request.idProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + request.idProducto()));

        Cliente cliente = clienteRepository.findById(request.idCliente())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + request.idCliente()));

        // Solapamiento de reservas
        var overlaps = reservaServicioRepository.findOverlaps(
                request.idProducto(),
                request.fechaReserva(),
                request.horaInicio(),
                request.horaFin(),
                ESTADO_CANCELADA
        );

        boolean conflict = overlaps.stream().anyMatch(x -> !x.getIdReserva().equals(idReserva));
        if (conflict) {
            throw new IllegalArgumentException("El horario ya está reservado para este producto.");
        }

        r.setProducto(producto);
        r.setCliente(cliente);
        r.setFechaReserva(request.fechaReserva());
        r.setHoraInicio(request.horaInicio());
        r.setHoraFin(request.horaFin());
        r.setCntPersonas(request.cntPersonas());
        r.setObservaciones(cleanNullable(request.observaciones()));

        ReservaServicio saved = reservaServicioRepository.save(r);
        return mapToResponse(saved);
    }

    // Cancela una reserva, marca la reserva como CANCELADA y añade el motivo a las observaciones.
    @Override
    @Transactional
    public void cancel(Integer idReserva, String motivo) {
        if (idReserva == null) throw new IllegalArgumentException("El idReserva es obligatorio.");

        ReservaServicio r = reservaServicioRepository.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada: " + idReserva));

        if (ESTADO_CANCELADA.equals(r.getEstado())) return; // idempotente

        r.setEstado(ESTADO_CANCELADA);

        String obsActual = r.getObservaciones();
        String motivoLimpio = (motivo == null) ? "" : motivo.trim();
        String nuevaObs = buildCancelObs(obsActual, motivoLimpio);
        r.setObservaciones(nuevaObs);

        reservaServicioRepository.save(r);
    }

    // Elimina una reserva por id.
    @Override
    @Transactional
    public void delete(Integer idReserva) {
        if (idReserva == null) throw new IllegalArgumentException("El idReserva es obligatorio.");
        if (!reservaServicioRepository.existsById(idReserva)) {
            throw new IllegalArgumentException("Reserva no encontrada: " + idReserva);
        }
        reservaServicioRepository.deleteById(idReserva);
    }

    // ---------------- helpers ----------------

    // Valida los campos obligatorios de la petición de reserva y reglas básicas de negocio.
    // Reglas: request no nula, ids y fechas obligatorias, fecha no pasada, horaFin > horaInicio, cntPersonas > 0 si aplica.
    private void validateRequest(ReservaServicioRequest request) {
        if (request == null) throw new IllegalArgumentException("La solicitud no puede ser nula.");
        if (request.idProducto() == null) throw new IllegalArgumentException("El idProducto es obligatorio.");
        if (request.idCliente() == null) throw new IllegalArgumentException("El idCliente es obligatorio.");
        if (request.fechaReserva() == null) throw new IllegalArgumentException("La fechaReserva es obligatoria.");
        if (request.horaInicio() == null) throw new IllegalArgumentException("La horaInicio es obligatoria.");
        if (request.horaFin() == null) throw new IllegalArgumentException("La horaFin es obligatoria.");

        if (request.fechaReserva().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se puede reservar en una fecha pasada.");
        }

        if (!request.horaFin().isAfter(request.horaInicio())) {
            throw new IllegalArgumentException("La horaFin debe ser mayor que la horaInicio.");
        }

        if (request.cntPersonas() != null && request.cntPersonas() <= 0) {
            throw new IllegalArgumentException("La cantidad de personas debe ser mayor que 0.");
        }
    }

    // Normaliza una cadena que puede ser nula: trim y conviértela a null si queda vacía.
    private String cleanNullable(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    // Construye el texto final de observaciones al cancelar una reserva.
    // Si ya había observaciones, las conserva y añade la marca de CANCELADA. Si se pasó un motivo, lo concatena.
    private String buildCancelObs(String obsActual, String motivo) {
        String base = (obsActual == null || obsActual.isBlank()) ? "" : obsActual.trim();
        if (motivo == null || motivo.isBlank()) {
            return base.isEmpty() ? "CANCELADA" : base + " | CANCELADA";
        }
        return base.isEmpty()
                ? "CANCELADA: " + motivo
                : base + " | CANCELADA: " + motivo;
    }

    // Mapea la entidad ReservaServicio a su correspondiente DTO de respuesta.
   private ReservaServicioResponse mapToResponse(ReservaServicio r) {
        return new ReservaServicioResponse(
                r.getIdReserva(),
                r.getProducto().getIdProducto(),
                r.getProducto().getDescripcion(),
                r.getCliente().getIdCliente(),
                r.getCliente().getNombre(),
                r.getFechaReserva(),
                r.getHoraInicio(),
                r.getHoraFin(),
                r.getCntPersonas(),
                r.getEstado(),
                r.getObservaciones()
        );
    }
}
