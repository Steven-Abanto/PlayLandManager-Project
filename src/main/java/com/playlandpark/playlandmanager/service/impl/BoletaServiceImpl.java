package com.playlandpark.playlandmanager.service.impl;

import com.playlandpark.playlandmanager.model.dto.boleta.BoletaCarritoRequest;
import com.playlandpark.playlandmanager.model.dto.boleta.BoletaRequest;
import com.playlandpark.playlandmanager.model.dto.boleta.BoletaResponse;
import com.playlandpark.playlandmanager.model.dto.boletadetalle.BoletaDetalleResponse;
import com.playlandpark.playlandmanager.model.dto.caja.CajaBoletaResponse;
import com.playlandpark.playlandmanager.model.dto.cliente.ClienteBoletaResponse;
import com.playlandpark.playlandmanager.model.dto.empleado.EmpleadoBoletaResponse;
import com.playlandpark.playlandmanager.model.dto.metpago.MetPagoResponse;
import com.playlandpark.playlandmanager.model.entity.*;
import com.playlandpark.playlandmanager.model.enums.TipoDocuVenta;
import com.playlandpark.playlandmanager.repository.*;
import com.playlandpark.playlandmanager.service.BoletaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoletaServiceImpl implements BoletaService {

    private static final String ESTADO_EMITIDA = "EMITIDA";
    private static final String CARRITO_ABIERTO = "ABIERTO";
    private static final String CARRITO_FACTURADO = "FACTURADO";

    private final BoletaRepository boletaRepository;
    private final BoletaDetalleRepository boletaDetalleRepository;
    private final CajaRepository cajaRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoDetalleRepository carritoDetalleRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ClienteRepository clienteRepository;
    private final MetPagoRepository metPagoRepository;
    private final MovVentaRepository movVentaRepository;
    private final MovInventarioRepository movInventarioRepository;
    private final ProductoRepository productoRepository;

    // Crea una boleta desde un request directo (sin carrito)
    @Override
    @Transactional
    public BoletaResponse create(BoletaRequest request) {
        validateRequired(request);

        Caja caja = cajaRepository.findById(request.idCaja())
                .orElseThrow(() -> new IllegalArgumentException("Caja no encontrada: " + request.idCaja()));

        Empleado empleado = empleadoRepository.findById(request.idEmpleado())
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado: " + request.idEmpleado()));

        Cliente cliente = null;
        if (request.idCliente() != null) {
            cliente = clienteRepository.findById(request.idCliente())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + request.idCliente()));
        }

        Boleta b = new Boleta();
        b.setCaja(caja);
        b.setEmpleado(empleado);
        b.setCliente(cliente);

        b.setTipoDocuVenta(request.tipoDocuVenta());
        b.setNumeDocuVenta(generateUniqueNumber(request.tipoDocuVenta()));

        b.setEstado(ESTADO_EMITIDA);
        b.setFechaHora(LocalDateTime.now());

        // Totales
        Totals totals = calculateTotalsSafely(request);
        b.setSubtotal(totals.subtotal);
        b.setDsctoTotal(totals.dsctoTotal);
        b.setImpuesto(totals.impuesto);
        b.setTotal(totals.total);

        Boleta saved = boletaRepository.save(b);
        return mapToResponse(saved);
    }

    public BoletaResponse createFromCarrito(BoletaCarritoRequest request) {
        if (request == null) throw new IllegalArgumentException("La solicitud no puede ser nula.");
        if (request.idCarrito() == null) throw new IllegalArgumentException("El idCarrito es obligatorio.");
        if (request.idCaja() == null) throw new IllegalArgumentException("El idCaja es obligatorio.");
        if (request.idEmpleado() == null) throw new IllegalArgumentException("El idEmpleado es obligatorio.");
        if (request.tipoDocuVenta() == null) throw new IllegalArgumentException("El tipoDocuVenta es obligatorio.");

        Carrito carrito = carritoRepository.findById(request.idCarrito())
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado: " + request.idCarrito()));

        if (!CARRITO_ABIERTO.equalsIgnoreCase(carrito.getEstado())) {
            throw new IllegalArgumentException("El carrito no está ABIERTO. Estado actual: " + carrito.getEstado());
        }

        List<CarritoDetalle> carritoDetalles = carritoDetalleRepository.findByCarrito_IdCarrito(request.idCarrito());
        if (carritoDetalles == null || carritoDetalles.isEmpty()) {
            throw new IllegalArgumentException("El carrito no tiene productos. No se puede facturar.");
        }

        Caja caja = cajaRepository.findById(request.idCaja())
                .orElseThrow(() -> new IllegalArgumentException("Caja no encontrada: " + request.idCaja()));

        Empleado empleado = empleadoRepository.findById(request.idEmpleado())
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado: " + request.idEmpleado()));

        Cliente cliente = null;
        if (request.idCliente() != null) {
            cliente = clienteRepository.findById(request.idCliente())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + request.idCliente()));
        }

        // ------------------ Calcular totales desde carrito ------------------
        BigDecimal subtotalBruto = BigDecimal.ZERO; // precio*cantidad
        BigDecimal dsctoTotal = BigDecimal.ZERO;    // descuento MONTO
        BigDecimal total = BigDecimal.ZERO;         // subtotal ya descontado

        for (CarritoDetalle cd : carritoDetalles) {
            if (cd.getCantidad() == null || cd.getCantidad() <= 0) {
                throw new IllegalArgumentException("Cantidad inválida en carritoDetalle: " + cd.getIdCarritoDetalle());
            }
            if (cd.getPrecio() == null) {
                throw new IllegalArgumentException("Precio nulo en carritoDetalle: " + cd.getIdCarritoDetalle());
            }
            if (cd.getDescuento() == null) {
                throw new IllegalArgumentException("Descuento nulo en carritoDetalle: " + cd.getIdCarritoDetalle());
            }
            if (cd.getSubtotal() == null) {
                throw new IllegalArgumentException("Subtotal nulo en carritoDetalle: " + cd.getIdCarritoDetalle());
            }

            BigDecimal base = cd.getPrecio().multiply(BigDecimal.valueOf(cd.getCantidad()));

            if (cd.getDescuento().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Descuento negativo en carritoDetalle: " + cd.getIdCarritoDetalle());
            }
            if (cd.getDescuento().compareTo(base) > 0) {
                throw new IllegalArgumentException("Descuento mayor que el importe del item en carritoDetalle: " + cd.getIdCarritoDetalle());
            }

            subtotalBruto = subtotalBruto.add(base);
            dsctoTotal = dsctoTotal.add(cd.getDescuento());
            total = total.add(cd.getSubtotal());
        }

        // ------------------ Validar pagos: totalPagado >= total ------------------
        if (request.pagos() == null || request.pagos().isEmpty()) {
            throw new IllegalArgumentException("Debe registrar al menos un método de pago.");
        }

        BigDecimal totalPagado = BigDecimal.ZERO;
        for (var p : request.pagos()) {
            if (p == null) throw new IllegalArgumentException("Pago inválido.");
            if (p.metodoPago() == null || p.metodoPago().isBlank()) {
                throw new IllegalArgumentException("El metodoPago es obligatorio.");
            }
            if (p.monto() == null) throw new IllegalArgumentException("El monto del pago es obligatorio.");
            if (p.monto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El monto del pago debe ser mayor que 0.");
            }
            totalPagado = totalPagado.add(p.monto());
        }

        if (totalPagado.compareTo(total) < 0) {
            throw new IllegalArgumentException("Pago insuficiente. Falta: " + total.subtract(totalPagado));
        }

        // Impuesto = 18% del total pagado
        BigDecimal impuesto = total.multiply(new BigDecimal("0.18"))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal vuelto = totalPagado.subtract(total);

        // ------------------ Crear boleta ------------------
        Boleta b = new Boleta();
        b.setCaja(caja);
        b.setEmpleado(empleado);
        b.setCliente(cliente);

        b.setTipoDocuVenta(request.tipoDocuVenta());
        b.setNumeDocuVenta(generateUniqueNumber(request.tipoDocuVenta()));
        b.setEstado(ESTADO_EMITIDA);
        b.setFechaHora(LocalDateTime.now());

        // ------------------ Guardar ya con totales calculados ------------------
        b.setSubtotal(subtotalBruto);
        b.setDsctoTotal(dsctoTotal);
        b.setImpuesto(impuesto);
        b.setTotal(total);
        b.setVuelto(vuelto);

        Boleta savedBoleta = boletaRepository.save(b);

        // ------------------ Guardar detalles ------------------
        List<BoletaDetalle> boletaDetalles = carritoDetalles.stream()
                .map(cd -> buildDetalle(savedBoleta, cd))
                .toList();

        boletaDetalleRepository.saveAll(boletaDetalles);

        // ------------------ Guardar pagos ------------------
        List<MetPago> pagos = request.pagos().stream().map(pr -> {
            MetPago mp = new MetPago();
            mp.setBoleta(savedBoleta);
            mp.setMetodoPago(pr.metodoPago().trim());
            mp.setMonto(pr.monto());
            return mp;
        }).toList();

        metPagoRepository.saveAll(pagos);

        MovVenta mov = new MovVenta();
        mov.setCaja(caja);
        mov.setMonto(total);
        mov.setFecha(savedBoleta.getFechaHora().toLocalDate());
        mov.setTipoMovimiento("VENTA");
        movVentaRepository.save(mov);

        // ------------------ Validar stock suficiente ------------------
        for (CarritoDetalle cd : carritoDetalles) {

            Producto producto = cd.getProducto();

            if (producto.getStockAct() == null) {
                throw new IllegalArgumentException("Producto sin stock definido: " + producto.getDescripcion());
            }

            if (producto.getStockAct() < cd.getCantidad()) {
                throw new IllegalArgumentException(
                        "Stock insuficiente para el producto: " + producto.getDescripcion() +
                                ". Stock actual: " + producto.getStockAct()
                );
            }
        }

        // ------------------ Guardar Movimientos ------------------
        List<MovInventario> movsInv = new ArrayList<>();

        for (BoletaDetalle d : boletaDetalles) {

            Producto producto = d.getProducto();

            // Restar stock
            Integer nuevoStock = producto.getStockAct() - d.getCantidad();
            producto.setStockAct(nuevoStock);

            // Guardar actualización de producto
            productoRepository.save(producto);

            // Registrar movimiento
            MovInventario mi = new MovInventario();
            mi.setBoleta(savedBoleta);
            mi.setProducto(producto);
            mi.setCantidad(d.getCantidad());
            mi.setFecha(savedBoleta.getFechaHora().toLocalDate());
            mi.setTipoMovimiento("VENTA");

            movsInv.add(mi);
        }

        movInventarioRepository.saveAll(movsInv);

        // ------------------ Cerrar Carrito ------------------

        carrito.setEstado(CARRITO_FACTURADO);
        carritoRepository.save(carrito);

        // ------------------ Recargar para response con detalles/pagos ------------------
        Boleta reloaded = boletaRepository.findById(savedBoleta.getIdBoleta())
                .orElseThrow(() -> new IllegalArgumentException("Boleta no encontrada: " + savedBoleta.getIdBoleta()));

        reloaded.setDetalles(boletaDetalleRepository.findByBoleta_IdBoleta(reloaded.getIdBoleta()));
        reloaded.setPagos(metPagoRepository.findByBoleta_IdBoleta(reloaded.getIdBoleta()));

        return mapToResponse(reloaded);
    }

    private BoletaDetalle buildDetalle(Boleta boleta, CarritoDetalle cd) {
        BoletaDetalle d = new BoletaDetalle();
        d.setBoleta(boleta);
        d.setProducto(cd.getProducto());
        d.setPrecio(cd.getPrecio());
        d.setCantidad(cd.getCantidad());
        d.setDescuento(cd.getDescuento()); // porcentual 0..1
        d.setSubtotal(cd.getSubtotal());   // ya viene: precio*cantidad - precio*cantidad*descuento

        try {
            var m = cd.getPromocion();
            d.setPromocion((m));
        } catch (Exception ignored) {
            d.setPromocion(null);
        }

        return d;
    }

    // Busca una boleta por su id
    @Override
    @Transactional(readOnly = true)
    public BoletaResponse findById(Integer id) {
        Boleta b = boletaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Boleta no encontrada: " + id));
        return mapToResponse(b);
    }

    // Busca una boleta por su tipo y número de documento de venta
    @Override
    @Transactional(readOnly = true)
    public BoletaResponse findByTypeAndNumber(TipoDocuVenta tipo, String numeDocuVenta) {
        if (tipo == null ) {
            throw new IllegalArgumentException("El tipo de documento es obligatorio.");
        }

        if (numeDocuVenta == null || numeDocuVenta.isBlank()) {
            throw new IllegalArgumentException("El número de documento es obligatorio.");
        }

        boolean existe = boletaRepository.existsByTipoDocuVentaAndNumeDocuVenta(tipo, numeDocuVenta);

        if (!existe) {
            throw new IllegalArgumentException("Boleta no encontrada: " + tipo + "-" + numeDocuVenta);
        }

        Boleta b = boletaRepository.findByTipoDocuVentaAndNumeDocuVenta(tipo, numeDocuVenta.trim())
                .orElseThrow(() -> new IllegalArgumentException("Boleta no encontrada con número: " + numeDocuVenta));
        return mapToResponse(b);
    }

    // Recupera todas las boletas
    @Override
    @Transactional(readOnly = true)
    public List<BoletaResponse> findAll() {
        return boletaRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    // Recupera las boletas filtradas por tipo de documento de venta
    @Override
    @Transactional(readOnly = true)
    public List<BoletaResponse> findByType(TipoDocuVenta tipo) {
        if (tipo == null) throw new IllegalArgumentException("El tipo de documento es obligatorio.");
        return boletaRepository.findByTipoDocuVenta(tipo).stream().map(this::mapToResponse).toList();
    }

    // Recupera las boletas asociadas a una caja
    @Override
    @Transactional(readOnly = true)
    public List<BoletaResponse> findByCaja(Integer idCaja) {
        if (idCaja == null) throw new IllegalArgumentException("El idCaja es obligatorio.");
        return boletaRepository.findByCaja_IdCaja(idCaja).stream().map(this::mapToResponse).toList();
    }

    // Recupera las boletas asociadas a un empleado
    @Override
    @Transactional(readOnly = true)
    public List<BoletaResponse> findByEmployee(Integer idEmpleado) {
        if (idEmpleado == null) throw new IllegalArgumentException("El idEmpleado es obligatorio.");
        return boletaRepository.findByEmpleado_IdEmpleado(idEmpleado).stream().map(this::mapToResponse).toList();
    }

    // Recupera las boletas asociadas a un cliente
    @Override
    @Transactional(readOnly = true)
    public List<BoletaResponse> findByClient(Integer idCliente) {
        if (idCliente == null) throw new IllegalArgumentException("El idCliente es obligatorio.");
        return boletaRepository.findByCliente_IdCliente(idCliente).stream().map(this::mapToResponse).toList();
    }

    // Recupera las boletas emitidas en un rango de fechas
    @Override
    @Transactional(readOnly = true)
    public List<BoletaResponse> findByDateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) throw new IllegalArgumentException("Las fechas son obligatorias.");
        if (end.isBefore(start)) throw new IllegalArgumentException("La fecha fin no puede ser menor que la fecha inicio.");

        return boletaRepository.findByFechaHoraBetween(start, end).stream()
                .map(this::mapToResponse).toList();
    }

    // Anula una boleta (cambia su estado a "ANULADA")
    @Override
    @Transactional
    public void cancel(Integer id) {
        Boleta b = boletaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Boleta no encontrada: " + id));

        // regla simple (luego puedes exigir que no esté pagada, etc.)
        b.setEstado("ANULADA");
        boletaRepository.save(b);
    }

    // ----------------- helpers -----------------
    // Valida los campos requeridos para crear una boleta
    private void validateRequired(BoletaRequest request) {
        if (request == null) throw new IllegalArgumentException("La solicitud no puede ser nula.");
        if (request.idCaja() == null) throw new IllegalArgumentException("El idCaja es obligatorio.");
        if (request.idEmpleado() == null) throw new IllegalArgumentException("El idEmpleado es obligatorio.");
        if (request.tipoDocuVenta() == null) throw new IllegalArgumentException("El tipoDocuVenta es obligatorio.");
        // detalles/pagos por ahora los dejamos opcionales, según tu avance
    }

    // Genera un número de documento de venta único basado en la fecha y un correlativo para el tipo de documento
    private String generateUniqueNumber(TipoDocuVenta tipo) {
        // Ejemplo: 20260222-153045
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        Optional<Boleta> bol = boletaRepository.findTopByTipoDocuVentaOrderByIdBoletaDesc(tipo);
        Integer correlativo = 1;

        if(bol.isPresent()){
            String nume = bol.get().getNumeDocuVenta().split("-")[1];
            correlativo = Integer.valueOf(nume) + 1;
        }

        String correlativoF = String.format("%08d", correlativo);

        String num = date + "-" + correlativoF;
        if (!boletaRepository.existsByTipoDocuVentaAndNumeDocuVenta(tipo, num)) {
            return num;
        } throw new IllegalArgumentException("No se pudo generar un número de documento único.");
    }

    // Calcula los totales (subtotal, descuento total, impuesto, total)
    private Totals calculateTotalsSafely(BoletaRequest request) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal dscto = BigDecimal.ZERO;

        if (request.detalles() != null) {
            for (var d : request.detalles()) {
                try {
                    var precioMethod = d.getClass().getMethod("precio");
                    var cantidadMethod = d.getClass().getMethod("cantidad");
                    var descuentoMethod = d.getClass().getMethod("descuento");

                    BigDecimal precio = (BigDecimal) precioMethod.invoke(d);
                    Integer cantidad = (Integer) cantidadMethod.invoke(d);
                    BigDecimal descuento = (BigDecimal) descuentoMethod.invoke(d);

                    BigDecimal base = precio.multiply(BigDecimal.valueOf(cantidad));
                    subtotal = subtotal.add(base);
                    dscto = dscto.add(descuento != null ? descuento : BigDecimal.ZERO);
                } catch (Exception ignored) {
                }
            }
        }

        BigDecimal impuesto = BigDecimal.ZERO;
        BigDecimal total = subtotal.subtract(dscto).add(impuesto);

        return new Totals(subtotal, dscto, impuesto, total);
    }

    // Convierte la entidad Boleta a su DTO de respuesta,
    // incluyendo datos relacionados como caja, empleado, cliente, pagos y detalles
    private BoletaResponse mapToResponse(Boleta b) {
        Caja c = b.getCaja();
        Empleado e = b.getEmpleado();
        Cliente cli = b.getCliente();

        CajaBoletaResponse caja = new CajaBoletaResponse(
                c.getCodCaja()
        );

        EmpleadoBoletaResponse empleado = new EmpleadoBoletaResponse(
                e.getIdEmpleado(),
                e.getNombre(),
                e.getApePaterno(),
                e.getApeMaterno()
        );

        ClienteBoletaResponse cliente = null;
        if (cli != null) {
            cliente = new ClienteBoletaResponse(
                    cli.getTipoDoc(),
                    cli.getNumeDoc(),
                    cli.getNombre(),
                    cli.getApePaterno(),
                    cli.getApeMaterno(),
                    cli.getCorreo(),
                    cli.getTelefono(),
                    cli.getDireccion()
            );
        }

        List<MetPagoResponse> pagos = b.getPagos() == null ? List.of() :
                b.getPagos().stream().map(p -> new MetPagoResponse(
                        p.getIdMetPago(),
                        p.getMetodoPago(),
                        p.getMonto()
                )).toList();

        List<BoletaDetalleResponse> detalles = b.getDetalles() == null ? List.of() :
                b.getDetalles().stream().map(d -> new BoletaDetalleResponse(
                        d.getIdBoletaDetalle(),
                        d.getProducto().getIdProducto(),
                        d.getProducto().getDescripcion(),
                        d.getPrecio(),
                        d.getCantidad(),
                        d.getDescuento(),
                        d.getSubtotal()
                )).toList();

        return new BoletaResponse(
                b.getIdBoleta(),
                b.getTipoDocuVenta(),
                b.getNumeDocuVenta(),
                b.getSubtotal(),
                b.getDsctoTotal(),
                b.getImpuesto(),
                b.getTotal(),
                b.getVuelto(),
                b.getEstado(),
                b.getFechaHora(),
                caja,
                empleado,
                cliente,
                pagos,
                detalles
        );
    }

    private record Totals(BigDecimal subtotal,
                          BigDecimal dsctoTotal,
                          BigDecimal impuesto,
                          BigDecimal total) {}
}
