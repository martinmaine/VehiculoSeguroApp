package main;

import entities.Cobertura;
import entities.SeguroVehicular;
import entities.Vehiculo;
import service.SeguroVehicularService;
import service.VehiculoService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Menú de consola para la aplicación
 * Proporciona interfaz para CRUD completo de Vehículos y Seguros
 */
public class AppMenu {
    
    private final Scanner scanner;
    private final VehiculoService vehiculoService;
    private final SeguroVehicularService seguroService;
    private final DateTimeFormatter dateFormatter;
    
    public AppMenu() {
        this.scanner = new Scanner(System.in);
        this.vehiculoService = new VehiculoService();
        this.seguroService = new SeguroVehicularService();
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }
    
    /**
     * Muestra el menú principal
     */
    public void mostrarMenu() {
        boolean continuar = true;
        
        while (continuar) {
            try {
                mostrarMenuPrincipal();
                int opcion = leerEntero("Seleccione una opción: ");
                
                switch (opcion) {
                    case 1:
                        menuVehiculos();
                        break;
                    case 2:
                        menuSeguros();
                        break;
                    case 3:
                        menuOperacionesEspeciales();
                        break;
                    case 0:
                        continuar = false;
                        break;
                    default:
                        System.out.println("✗ Opción inválida. Intente nuevamente.");
                }
                
            } catch (Exception e) {
                System.err.println("✗ Error inesperado: " + e.getMessage());
                scanner.nextLine(); // Limpiar buffer
            }
        }
    }
    
    private void mostrarMenuPrincipal() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                     MENÚ PRINCIPAL                         ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║  1. Gestión de Vehículos                                   ║");
        System.out.println("║  2. Gestión de Seguros                                     ║");
        System.out.println("║  3. Operaciones Especiales (Transacciones)                 ║");
        System.out.println("║  0. Salir                                                  ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
    
    // ========================================================================
    // MENÚ DE VEHÍCULOS
    // ========================================================================
    
    private void menuVehiculos() {
        boolean volver = false;
        
        while (!volver) {
            try {
                mostrarMenuVehiculos();
                int opcion = leerEntero("Seleccione una opción: ");
                
                switch (opcion) {
                    case 1:
                        crearVehiculo();
                        break;
                    case 2:
                        listarVehiculos();
                        break;
                    case 3:
                        buscarVehiculoPorId();
                        break;
                    case 4:
                        buscarVehiculoPorDominio();
                        break;
                    case 5:
                        actualizarVehiculo();
                        break;
                    case 6:
                        eliminarVehiculo();
                        break;
                    case 7:
                        asignarSeguroAVehiculo();
                        break;
                    case 8:
                        desasignarSeguroDeVehiculo();
                        break;
                    case 0:
                        volver = true;
                        break;
                    default:
                        System.out.println("✗ Opción inválida.");
                }
                
            } catch (Exception e) {
                manejarExcepcion(e);
            }
        }
    }
    
    private void mostrarMenuVehiculos() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                  GESTIÓN DE VEHÍCULOS                      ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║  1. Crear nuevo vehículo                                   ║");
        System.out.println("║  2. Listar todos los vehículos                             ║");
        System.out.println("║  3. Buscar vehículo por ID                                 ║");
        System.out.println("║  4. Buscar vehículo por dominio                            ║");
        System.out.println("║  5. Actualizar vehículo                                    ║");
        System.out.println("║  6. Eliminar vehículo (baja lógica)                        ║");
        System.out.println("║  7. Asignar seguro a vehículo                              ║");
        System.out.println("║  8. Desasignar seguro de vehículo                          ║");
        System.out.println("║  0. Volver al menú principal                               ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
    
private void crearVehiculo() throws Exception {
        System.out.println("\n═══ CREAR NUEVO VEHÍCULO ═══");
        
        String dominio = leerTexto("Dominio (ej: ABC123 o AB123CD): ").toUpperCase();
        String marca = leerTexto("Marca: ");
        String modelo = leerTexto("Modelo: ");
        Integer anio = leerEnteroOpcional("Año (Enter para omitir): ");
        String nroChasis = leerTextoOpcional("Número de chasis (Enter para omitir): ");
        
        Vehiculo vehiculo = new Vehiculo(dominio, marca, modelo, anio, nroChasis);
        
        // Preguntar si desea asignar un seguro existente
        String asignarSeguro = leerTexto("¿Desea asignar un seguro existente? (S/N): ").toUpperCase();
        if (asignarSeguro.equals("S")) {
            listarSegurosDisponibles();
            Long seguroId = leerEnteroLong("ID del seguro a asignar: ");
            SeguroVehicular seguro = seguroService.getById(seguroId);
            if (seguro != null) {
                vehiculo.setSeguro(seguro);
            } else {
                System.out.println("⚠ Seguro no encontrado. Se creará el vehículo sin seguro.");
            }
        }
        
        vehiculoService.insertar(vehiculo);
        System.out.println("\n✓ Vehículo creado exitosamente con ID: " + vehiculo.getId());
    }
    
    private void listarVehiculos() throws Exception {
        System.out.println("\n═══ LISTADO DE VEHÍCULOS ═══");
        
        List<Vehiculo> vehiculos = vehiculoService.getAll();
        
        if (vehiculos.isEmpty()) {
            System.out.println("No hay vehículos registrados.");
            return;
        }
        
        System.out.println("\nTotal de vehículos: " + vehiculos.size());
        System.out.println();
        
        for (Vehiculo v : vehiculos) {
            System.out.println(v);
            System.out.println();
        }
    }
    
    private void buscarVehiculoPorId() throws Exception {
        System.out.println("\n═══ BUSCAR VEHÍCULO POR ID ═══");
        
        long id = leerEnteroLong("Ingrese el ID del vehículo: ");
        Vehiculo vehiculo = vehiculoService.getById(id);
        
        if (vehiculo == null) {
            System.out.println("✗ No se encontró un vehículo con ID: " + id);
        } else {
            System.out.println("\n" + vehiculo);
        }
    }
    
    private void buscarVehiculoPorDominio() throws Exception {
        System.out.println("\n═══ BUSCAR VEHÍCULO POR DOMINIO ═══");
        
        String dominio = leerTexto("Ingrese el dominio: ").toUpperCase();
        Vehiculo vehiculo = vehiculoService.buscarPorDominio(dominio);
        
        if (vehiculo == null) {
            System.out.println("✗ No se encontró un vehículo con dominio: " + dominio);
        } else {
            System.out.println("\n" + vehiculo);
        }
    }
    
    private void actualizarVehiculo() throws Exception {
        System.out.println("\n═══ ACTUALIZAR VEHÍCULO ═══");
        
        long id = leerEnteroLong("Ingrese el ID del vehículo a actualizar: ");
        Vehiculo vehiculo = vehiculoService.getById(id);
        
        if (vehiculo == null) {
            System.out.println("✗ No se encontró un vehículo con ID: " + id);
            return;
        }
        
        System.out.println("\nVehículo actual:");
        System.out.println(vehiculo);
        System.out.println("\nIngrese los nuevos datos (Enter para mantener el valor actual):");
        
        String dominio = leerTextoConDefault("Dominio [" + vehiculo.getDominio() + "]: ", vehiculo.getDominio()).toUpperCase();
        String marca = leerTextoConDefault("Marca [" + vehiculo.getMarca() + "]: ", vehiculo.getMarca());
        String modelo = leerTextoConDefault("Modelo [" + vehiculo.getModelo() + "]: ", vehiculo.getModelo());
        
        System.out.print("Año [" + (vehiculo.getAnio() != null ? vehiculo.getAnio() : "N/A") + "]: ");
        String anioStr = scanner.nextLine().trim();
        Integer anio = anioStr.isEmpty() ? vehiculo.getAnio() : Integer.parseInt(anioStr);
        
        String nroChasis = leerTextoConDefault("Nro. Chasis [" + (vehiculo.getNroChasis() != null ? vehiculo.getNroChasis() : "N/A") + "]: ", 
                                               vehiculo.getNroChasis());
        
        vehiculo.setDominio(dominio);
        vehiculo.setMarca(marca);
        vehiculo.setModelo(modelo);
        vehiculo.setAnio(anio);
        vehiculo.setNroChasis(nroChasis);
        
        vehiculoService.actualizar(vehiculo);
        System.out.println("\n✓ Vehículo actualizado exitosamente");
    }
    
    private void eliminarVehiculo() throws Exception {
        System.out.println("\n═══ ELIMINAR VEHÍCULO ═══");
        
        long id = leerEnteroLong("Ingrese el ID del vehículo a eliminar: ");
        Vehiculo vehiculo = vehiculoService.getById(id);
        
        if (vehiculo == null) {
            System.out.println("✗ No se encontró un vehículo con ID: " + id);
            return;
        }
        
        System.out.println("\nVehículo a eliminar:");
        System.out.println(vehiculo);
        
        String confirmacion = leerTexto("\n¿Está seguro de eliminar este vehículo? (S/N): ").toUpperCase();
        
        if (confirmacion.equals("S")) {
            vehiculoService.eliminar(id);
            System.out.println("\n✓ Vehículo eliminado exitosamente (baja lógica)");
        } else {
            System.out.println("✗ Operación cancelada");
        }
    }
    
    private void asignarSeguroAVehiculo() throws Exception {
        System.out.println("\n═══ ASIGNAR SEGURO A VEHÍCULO ═══");
        
        long vehiculoId = leerEnteroLong("ID del vehículo: ");
        Vehiculo vehiculo = vehiculoService.getById(vehiculoId);
        
        if (vehiculo == null) {
            System.out.println("✗ Vehículo no encontrado");
            return;
        }
        
        if (vehiculo.getSeguro() != null) {
            System.out.println("⚠ El vehículo ya tiene un seguro asignado:");
            System.out.println(vehiculo.getSeguro());
            String reemplazar = leerTexto("¿Desea reemplazarlo? (S/N): ").toUpperCase();
            if (!reemplazar.equals("S")) {
                System.out.println("✗ Operación cancelada");
                return;
            }
        }
        
        listarSegurosDisponibles();
        long seguroId = leerEnteroLong("ID del seguro a asignar: ");
        
        vehiculoService.asignarSeguro(vehiculoId, seguroId);
        System.out.println("\n✓ Seguro asignado exitosamente");
    }
    
    private void desasignarSeguroDeVehiculo() throws Exception {
        System.out.println("\n═══ DESASIGNAR SEGURO DE VEHÍCULO ═══");
        
        long vehiculoId = leerEnteroLong("ID del vehículo: ");
        
        String confirmacion = leerTexto("¿Está seguro de desasignar el seguro? (S/N): ").toUpperCase();
        
        if (confirmacion.equals("S")) {
            vehiculoService.desasignarSeguro(vehiculoId);
            System.out.println("\n✓ Seguro desasignado exitosamente");
        } else {
            System.out.println("✗ Operación cancelada");
        }
    }
    
// ========================================================================
    // MENÚ DE SEGUROS
    // ========================================================================
    
    private void menuSeguros() {
        boolean volver = false;
        
        while (!volver) {
            try {
                mostrarMenuSeguros();
                int opcion = leerEntero("Seleccione una opción: ");
                
                switch (opcion) {
                    case 1:
                        crearSeguro();
                        break;
                    case 2:
                        listarSeguros();
                        break;
                    case 3:
                        buscarSeguroPorId();
                        break;
                    case 4:
                        buscarSeguroPorPoliza();
                        break;
                    case 5:
                        actualizarSeguro();
                        break;
                    case 6:
                        eliminarSeguro();
                        break;
                    case 0:
                        volver = true;
                        break;
                    default:
                        System.out.println("✗ Opción inválida.");
                }
                
            } catch (Exception e) {
                manejarExcepcion(e);
            }
        }
    }
    
    private void mostrarMenuSeguros() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                  GESTIÓN DE SEGUROS                        ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║  1. Crear nuevo seguro                                     ║");
        System.out.println("║  2. Listar todos los seguros                               ║");
        System.out.println("║  3. Buscar seguro por ID                                   ║");
        System.out.println("║  4. Buscar seguro por número de póliza                     ║");
        System.out.println("║  5. Actualizar seguro                                      ║");
        System.out.println("║  6. Eliminar seguro (baja lógica)                          ║");
        System.out.println("║  0. Volver al menú principal                               ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
    
    private void crearSeguro() throws Exception {
        System.out.println("\n═══ CREAR NUEVO SEGURO ═══");
        
        String aseguradora = leerTexto("Aseguradora: ");
        String nroPoliza = leerTexto("Número de póliza: ").toUpperCase();
        Cobertura cobertura = leerCobertura();
        LocalDate vencimiento = leerFecha("Fecha de vencimiento (dd/MM/yyyy): ");
        
        SeguroVehicular seguro = new SeguroVehicular(aseguradora, nroPoliza, cobertura, vencimiento);
        
        seguroService.insertar(seguro);
        System.out.println("\n✓ Seguro creado exitosamente con ID: " + seguro.getId());
    }
    
    private void listarSeguros() throws Exception {
        System.out.println("\n═══ LISTADO DE SEGUROS ═══");
        
        List<SeguroVehicular> seguros = seguroService.getAll();
        
        if (seguros.isEmpty()) {
            System.out.println("No hay seguros registrados.");
            return;
        }
        
        System.out.println("\nTotal de seguros: " + seguros.size());
        System.out.println();
        
        for (SeguroVehicular s : seguros) {
            System.out.println(s);
            System.out.println();
        }
    }
    
    private void listarSegurosDisponibles() throws Exception {
        System.out.println("\n═══ SEGUROS DISPONIBLES (sin asignar) ═══");
        
        List<SeguroVehicular> todosSeguros = seguroService.getAll();
        List<Vehiculo> todosVehiculos = vehiculoService.getAll();
        
        boolean hayDisponibles = false;
        
        for (SeguroVehicular seguro : todosSeguros) {
            boolean asignado = false;
            for (Vehiculo v : todosVehiculos) {
                if (v.getSeguro() != null && v.getSeguro().getId().equals(seguro.getId())) {
                    asignado = true;
                    break;
                }
            }
            
            if (!asignado) {
                System.out.println("ID: " + seguro.getId() + " | Póliza: " + seguro.getNroPoliza() + 
                                 " | Aseguradora: " + seguro.getAseguradora());
                hayDisponibles = true;
            }
        }
        
        if (!hayDisponibles) {
            System.out.println("No hay seguros disponibles (todos están asignados)");
        }
    }
    
    private void buscarSeguroPorId() throws Exception {
        System.out.println("\n═══ BUSCAR SEGURO POR ID ═══");
        
        long id = leerEnteroLong("Ingrese el ID del seguro: ");
        SeguroVehicular seguro = seguroService.getById(id);
        
        if (seguro == null) {
            System.out.println("✗ No se encontró un seguro con ID: " + id);
        } else {
            System.out.println("\n" + seguro);
        }
    }
    
    private void buscarSeguroPorPoliza() throws Exception {
        System.out.println("\n═══ BUSCAR SEGURO POR PÓLIZA ═══");
        
        String nroPoliza = leerTexto("Ingrese el número de póliza: ").toUpperCase();
        SeguroVehicular seguro = seguroService.buscarPorPoliza(nroPoliza);
        
        if (seguro == null) {
            System.out.println("✗ No se encontró un seguro con póliza: " + nroPoliza);
        } else {
            System.out.println("\n" + seguro);
        }
    }
    
    private void actualizarSeguro() throws Exception {
        System.out.println("\n═══ ACTUALIZAR SEGURO ═══");
        
        long id = leerEnteroLong("Ingrese el ID del seguro a actualizar: ");
        SeguroVehicular seguro = seguroService.getById(id);
        
        if (seguro == null) {
            System.out.println("✗ No se encontró un seguro con ID: " + id);
            return;
        }
        
        System.out.println("\nSeguro actual:");
        System.out.println(seguro);
        System.out.println("\nIngrese los nuevos datos (Enter para mantener el valor actual):");
        
        String aseguradora = leerTextoConDefault("Aseguradora [" + seguro.getAseguradora() + "]: ", seguro.getAseguradora());
        String nroPoliza = leerTextoConDefault("Póliza [" + seguro.getNroPoliza() + "]: ", seguro.getNroPoliza()).toUpperCase();
        
        System.out.println("Cobertura actual: " + seguro.getCobertura());
        String cambiarCobertura = leerTexto("¿Cambiar cobertura? (S/N): ").toUpperCase();
        Cobertura cobertura = seguro.getCobertura();
        if (cambiarCobertura.equals("S")) {
            cobertura = leerCobertura();
        }
        
        System.out.println("Vencimiento actual: " + seguro.getVencimiento().format(dateFormatter));
        String cambiarVencimiento = leerTexto("¿Cambiar vencimiento? (S/N): ").toUpperCase();
        LocalDate vencimiento = seguro.getVencimiento();
        if (cambiarVencimiento.equals("S")) {
            vencimiento = leerFecha("Nueva fecha de vencimiento (dd/MM/yyyy): ");
        }
        
        seguro.setAseguradora(aseguradora);
        seguro.setNroPoliza(nroPoliza);
        seguro.setCobertura(cobertura);
        seguro.setVencimiento(vencimiento);
        
        seguroService.actualizar(seguro);
        System.out.println("\n✓ Seguro actualizado exitosamente");
    }
    
    private void eliminarSeguro() throws Exception {
        System.out.println("\n═══ ELIMINAR SEGURO ═══");
        
        long id = leerEnteroLong("Ingrese el ID del seguro a eliminar: ");
        SeguroVehicular seguro = seguroService.getById(id);
        
        if (seguro == null) {
            System.out.println("✗ No se encontró un seguro con ID: " + id);
            return;
        }
        
        // Verificar si está asignado a algún vehículo
        List<Vehiculo> vehiculos = vehiculoService.getAll();
        boolean asignado = false;
        for (Vehiculo v : vehiculos) {
            if (v.getSeguro() != null && v.getSeguro().getId().equals(id)) {
                System.out.println("⚠ ADVERTENCIA: Este seguro está asignado al vehículo:");
                System.out.println("  Dominio: " + v.getDominio() + " | " + v.getMarca() + " " + v.getModelo());
                asignado = true;
                break;
            }
        }
        
        System.out.println("\nSeguro a eliminar:");
        System.out.println(seguro);
        
        String confirmacion = leerTexto("\n¿Está seguro de eliminar este seguro? (S/N): ").toUpperCase();
        
        if (confirmacion.equals("S")) {
            seguroService.eliminar(id);
            System.out.println("\n✓ Seguro eliminado exitosamente (baja lógica)");
            if (asignado) {
                System.out.println("  El vehículo quedará sin seguro asignado (ON DELETE SET NULL)");
            }
        } else {
            System.out.println("✗ Operación cancelada");
        }
    }
    
// ========================================================================
    // OPERACIONES ESPECIALES (TRANSACCIONES)
    // ========================================================================
    
    private void menuOperacionesEspeciales() {
        boolean volver = false;
        
        while (!volver) {
            try {
                mostrarMenuOperacionesEspeciales();
                int opcion = leerEntero("Seleccione una opción: ");
                
                switch (opcion) {
                    case 1:
                        crearVehiculoConSeguroNuevo();
                        break;
                    case 2:
                        simularRollback();
                        break;
                    case 0:
                        volver = true;
                        break;
                    default:
                        System.out.println("✗ Opción inválida.");
                }
                
            } catch (Exception e) {
                manejarExcepcion(e);
            }
        }
    }
    
    private void mostrarMenuOperacionesEspeciales() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║            OPERACIONES ESPECIALES                          ║");
        System.out.println("║            (Demostración de Transacciones)                 ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║  1. Crear vehículo CON seguro nuevo (transacción)          ║");
        System.out.println("║  2. Simular ROLLBACK (demostración)                        ║");
        System.out.println("║  0. Volver al menú principal                               ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
    
    private void crearVehiculoConSeguroNuevo() throws Exception {
        System.out.println("\n═══ CREAR VEHÍCULO CON SEGURO NUEVO ═══");
        System.out.println("Esta operación crea el vehículo Y el seguro en una MISMA TRANSACCIÓN");
        System.out.println("Si algo falla, se hace ROLLBACK de AMBOS\n");
        
        // Datos del vehículo
        System.out.println("--- DATOS DEL VEHÍCULO ---");
        String dominio = leerTexto("Dominio: ").toUpperCase();
        String marca = leerTexto("Marca: ");
        String modelo = leerTexto("Modelo: ");
        Integer anio = leerEnteroOpcional("Año (Enter para omitir): ");
        String nroChasis = leerTextoOpcional("Número de chasis (Enter para omitir): ");
        
        Vehiculo vehiculo = new Vehiculo(dominio, marca, modelo, anio, nroChasis);
        
        // Datos del seguro
        System.out.println("\n--- DATOS DEL SEGURO ---");
        String aseguradora = leerTexto("Aseguradora: ");
        String nroPoliza = leerTexto("Número de póliza: ").toUpperCase();
        Cobertura cobertura = leerCobertura();
        LocalDate vencimiento = leerFecha("Fecha de vencimiento (dd/MM/yyyy): ");
        
        SeguroVehicular seguro = new SeguroVehicular(aseguradora, nroPoliza, cobertura, vencimiento);
        
        System.out.println("\n⚡ Ejecutando transacción...");
        vehiculoService.insertarVehiculoConSeguro(vehiculo, seguro);
        
        System.out.println("\n✓ TRANSACCIÓN EXITOSA");
        System.out.println("  Seguro ID: " + seguro.getId());
        System.out.println("  Vehículo ID: " + vehiculo.getId());
        System.out.println("  Ambos fueron creados en la misma transacción");
    }
    
    private void simularRollback() throws Exception {
        System.out.println("\n═══ SIMULACIÓN DE ROLLBACK ═══");
        System.out.println("Esta demostración intentará crear un vehículo con datos inválidos");
        System.out.println("para mostrar el ROLLBACK en acción\n");
        
        try {
            // Intentar crear vehículo con dominio inválido (muy largo)
            Vehiculo vehiculo = new Vehiculo();
            vehiculo.setDominio("DOMINIOMUYLARGO123"); // Esto fallará la validación
            vehiculo.setMarca("Test");
            vehiculo.setModelo("Test");
            
            System.out.println("⚡ Intentando insertar vehículo con dominio inválido...");
            vehiculoService.insertar(vehiculo);
            
        } catch (Exception e) {
            System.out.println("\n✓ ROLLBACK EJECUTADO CORRECTAMENTE");
            System.out.println("Motivo: " + e.getMessage());
            System.out.println("\nNingún dato fue guardado en la base de datos");
            System.out.println("La transacción se revirtió completamente");
        }
    }
    
    // ========================================================================
    // MÉTODOS AUXILIARES DE LECTURA
    // ========================================================================
    
    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        String texto = scanner.nextLine().trim();
        while (texto.isEmpty()) {
            System.out.print("⚠ Campo obligatorio. " + mensaje);
            texto = scanner.nextLine().trim();
        }
        return texto;
    }
    
    private String leerTextoOpcional(String mensaje) {
        System.out.print(mensaje);
        String texto = scanner.nextLine().trim();
        return texto.isEmpty() ? null : texto;
    }
    
    private String leerTextoConDefault(String mensaje, String valorDefault) {
        System.out.print(mensaje);
        String texto = scanner.nextLine().trim();
        return texto.isEmpty() ? valorDefault : texto;
    }
    
    private int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                int valor = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer
                return valor;
            } catch (InputMismatchException e) {
                System.out.println("✗ Debe ingresar un número entero");
                scanner.nextLine(); // Limpiar buffer
            }
        }
    }
    
    private long leerEnteroLong(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                long valor = scanner.nextLong();
                scanner.nextLine(); // Limpiar buffer
                return valor;
            } catch (InputMismatchException e) {
                System.out.println("✗ Debe ingresar un número entero");
                scanner.nextLine(); // Limpiar buffer
            }
        }
    }
    
    private Integer leerEnteroOpcional(String mensaje) {
        System.out.print(mensaje);
        String texto = scanner.nextLine().trim();
        if (texto.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            System.out.println("⚠ Valor inválido, se omitirá este campo");
            return null;
        }
    }
    
    private LocalDate leerFecha(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String texto = scanner.nextLine().trim();
                return LocalDate.parse(texto, dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("✗ Formato de fecha inválido. Use dd/MM/yyyy (ej: 31/12/2025)");
            }
        }
    }
    
    private Cobertura leerCobertura() {
        while (true) {
            System.out.println("\nTipos de cobertura:");
            System.out.println("  1. RC (Responsabilidad Civil)");
            System.out.println("  2. TERCEROS (Terceros Completo)");
            System.out.println("  3. TODO_RIESGO (Todo Riesgo)");
            
            int opcion = leerEntero("Seleccione tipo de cobertura: ");
            
            switch (opcion) {
                case 1:
                    return Cobertura.RC;
                case 2:
                    return Cobertura.TERCEROS;
                case 3:
                    return Cobertura.TODO_RIESGO;
                default:
                    System.out.println("✗ Opción inválida");
            }
        }
    }
    
    private void manejarExcepcion(Exception e) {
        System.err.println("\n✗ ERROR: " + e.getMessage());
        scanner.nextLine(); // Limpiar buffer en caso de error
    }
}