package service;

import config.DatabaseConnection;
import dao.SeguroVehicularDao;
import dao.VehiculoDao;
import entities.SeguroVehicular;
import entities.Vehiculo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para la entidad Vehiculo
 * Implementa reglas de negocio, validaciones y manejo de transacciones
 * Maneja la relación 1→1 con SeguroVehicular
 */
public class VehiculoService implements GenericService<Vehiculo> {
    
    private final VehiculoDao vehiculoDao;
    private final SeguroVehicularDao seguroDao;
    
    public VehiculoService() {
        this.vehiculoDao = new VehiculoDao();
        this.seguroDao = new SeguroVehicularDao();
    }
    
    @Override
    public void insertar(Vehiculo entity) throws Exception {
        Connection conn = null;
        try {
            // Validaciones de negocio
            validarVehiculo(entity);
            validarDominioUnico(entity.getDominio(), null);
            
            if (entity.getNroChasis() != null && !entity.getNroChasis().trim().isEmpty()) {
                validarChasisUnico(entity.getNroChasis(), null);
            }
            
            // Iniciar transacción
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Si tiene seguro asociado, validar que existe y no esté ya asignado
            if (entity.getSeguro() != null && entity.getSeguro().getId() != null) {
                validarSeguroDisponible(entity.getSeguro().getId(), null, conn);
            }
            
            // Ejecutar operación
            vehiculoDao.crear(entity, conn);
            
            // Commit si todo salió bien
            conn.commit();
            System.out.println("✓ Vehículo insertado correctamente con ID: " + entity.getId());
            
        } catch (Exception e) {
            // Rollback en caso de error
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("✗ ROLLBACK ejecutado en insertar Vehiculo");
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            throw new Exception("Error al insertar vehículo: " + e.getMessage(), e);
        } finally {
            // Restablecer autoCommit y cerrar conexión
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Crea un vehículo CON su seguro en una misma transacción
     * Este método demuestra una operación transaccional compleja
     */
    public void insertarVehiculoConSeguro(Vehiculo vehiculo, SeguroVehicular seguro) throws Exception {
        Connection conn = null;
        try {
            // Validaciones
            validarVehiculo(vehiculo);
            validarDominioUnico(vehiculo.getDominio(), null);
            
            if (vehiculo.getNroChasis() != null && !vehiculo.getNroChasis().trim().isEmpty()) {
                validarChasisUnico(vehiculo.getNroChasis(), null);
            }
            
            if (seguro == null) {
                throw new Exception("El seguro no puede ser nulo en esta operación");
            }
            
            // Iniciar transacción
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Crear el seguro primero
            seguroDao.crear(seguro, conn);
            System.out.println("  → Seguro creado con ID: " + seguro.getId());
            
            // 2. Asignar el seguro al vehículo
            vehiculo.setSeguro(seguro);
            
            // 3. Crear el vehículo
            vehiculoDao.crear(vehiculo, conn);
            System.out.println("  → Vehículo creado con ID: " + vehiculo.getId());
            
            // Commit de toda la operación
            conn.commit();
            System.out.println("✓ Vehículo con seguro insertado correctamente en una transacción");
            
        } catch (Exception e) {
            // Rollback en caso de error
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("✗ ROLLBACK ejecutado en insertarVehiculoConSeguro");
                    System.err.println("  Ni el vehículo ni el seguro fueron creados");
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            throw new Exception("Error al insertar vehículo con seguro: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }
    
    @Override
    public void actualizar(Vehiculo entity) throws Exception {
        Connection conn = null;
        try {
            // Validaciones
            if (entity.getId() == null || entity.getId() <= 0) {
                throw new Exception("El ID del vehículo es requerido para actualizar");
            }
            
            // Verificar que existe
            Vehiculo existente = vehiculoDao.leer(entity.getId());
            if (existente == null) {
                throw new Exception("No existe un vehículo con ID: " + entity.getId());
            }
            
            validarVehiculo(entity);
            
            // Validar unicidad de dominio si cambió
            if (!existente.getDominio().equalsIgnoreCase(entity.getDominio())) {
                validarDominioUnico(entity.getDominio(), entity.getId());
            }
            
            // Validar unicidad de chasis si cambió y no es nulo
            if (entity.getNroChasis() != null && !entity.getNroChasis().trim().isEmpty()) {
                if (existente.getNroChasis() == null || 
                    !existente.getNroChasis().equalsIgnoreCase(entity.getNroChasis())) {
                    validarChasisUnico(entity.getNroChasis(), entity.getId());
                }
            }
            
            // Iniciar transacción
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Validar cambio de seguro
            Long nuevoSeguroId = entity.getSeguro() != null ? entity.getSeguro().getId() : null;
            Long seguroActualId = existente.getSeguro() != null ? existente.getSeguro().getId() : null;
            
            // Si se está asignando o cambiando el seguro
            if (nuevoSeguroId != null && !nuevoSeguroId.equals(seguroActualId)) {
                validarSeguroDisponible(nuevoSeguroId, entity.getId(), conn);
            }
            
            // Ejecutar operación
            vehiculoDao.actualizar(entity, conn);
            
            // Commit
            conn.commit();
            System.out.println("✓ Vehículo actualizado correctamente");
            
        } catch (Exception e) {
            // Rollback en caso de error
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("✗ ROLLBACK ejecutado en actualizar Vehiculo");
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            throw new Exception("Error al actualizar vehículo: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }
    
    @Override
    public void eliminar(long id) throws Exception {
        Connection conn = null;
        try {
            // Verificar que existe
            Vehiculo existente = vehiculoDao.leer(id);
            if (existente == null) {
                throw new Exception("No existe un vehículo con ID: " + id);
            }
            
            // Iniciar transacción
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Ejecutar operación (eliminación lógica)
            vehiculoDao.eliminar(id, conn);
            
            // Commit
            conn.commit();
            System.out.println("✓ Vehículo eliminado correctamente (eliminación lógica)");
            
        } catch (Exception e) {
            // Rollback en caso de error
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("✗ ROLLBACK ejecutado en eliminar Vehiculo");
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            throw new Exception("Error al eliminar vehículo: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }
    
    @Override
    public Vehiculo getById(long id) throws Exception {
        return vehiculoDao.leer(id);
    }
    
    @Override
    public List<Vehiculo> getAll() throws Exception {
        return vehiculoDao.leerTodos();
    }
    
    /**
     * Busca un vehículo por dominio
     */
    public Vehiculo buscarPorDominio(String dominio) throws Exception {
        if (dominio == null || dominio.trim().isEmpty()) {
            throw new Exception("El dominio no puede estar vacío");
        }
        return vehiculoDao.buscarPorDominio(dominio.trim().toUpperCase());
    }
    
    /**
     * Busca un vehículo por número de chasis
     */
    public Vehiculo buscarPorChasis(String nroChasis) throws Exception {
        if (nroChasis == null || nroChasis.trim().isEmpty()) {
            throw new Exception("El número de chasis no puede estar vacío");
        }
        return vehiculoDao.buscarPorChasis(nroChasis.trim().toUpperCase());
    }
    
    /**
     * Asigna un seguro existente a un vehículo
     */
    public void asignarSeguro(long vehiculoId, long seguroId) throws Exception {
        Connection conn = null;
        try {
            // Iniciar transacción
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Verificar que el vehículo existe
            Vehiculo vehiculo = vehiculoDao.leer(vehiculoId, conn);
            if (vehiculo == null) {
                throw new Exception("No existe un vehículo con ID: " + vehiculoId);
            }
            
            // Verificar que el seguro existe
            SeguroVehicular seguro = seguroDao.leer(seguroId, conn);
            if (seguro == null) {
                throw new Exception("No existe un seguro con ID: " + seguroId);
            }
            
            // Validar que el seguro no esté ya asignado
            validarSeguroDisponible(seguroId, vehiculoId, conn);
            
            // Asignar el seguro
            vehiculo.setSeguro(seguro);
            vehiculoDao.actualizar(vehiculo, conn);
            
            // Commit
            conn.commit();
            System.out.println("✓ Seguro asignado correctamente al vehículo");
            
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("✗ ROLLBACK ejecutado en asignarSeguro");
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            throw new Exception("Error al asignar seguro: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Desasigna el seguro de un vehículo
     */
    public void desasignarSeguro(long vehiculoId) throws Exception {
        Connection conn = null;
        try {
            // Iniciar transacción
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Verificar que el vehículo existe
            Vehiculo vehiculo = vehiculoDao.leer(vehiculoId, conn);
            if (vehiculo == null) {
                throw new Exception("No existe un vehículo con ID: " + vehiculoId);
            }
            
            if (vehiculo.getSeguro() == null) {
                throw new Exception("El vehículo no tiene un seguro asignado");
            }
            
            // Desasignar el seguro
            vehiculo.setSeguro(null);
            vehiculoDao.actualizar(vehiculo, conn);
            
            // Commit
            conn.commit();
            System.out.println("✓ Seguro desasignado correctamente del vehículo");
            
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("✗ ROLLBACK ejecutado en desasignarSeguro");
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            throw new Exception("Error al desasignar seguro: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Valida los datos de un Vehiculo según reglas de negocio
     */
    private void validarVehiculo(Vehiculo vehiculo) throws Exception {
        if (vehiculo == null) {
            throw new Exception("El vehículo no puede ser nulo");
        }
        
        // Validar dominio
        if (vehiculo.getDominio() == null || vehiculo.getDominio().trim().isEmpty()) {
            throw new Exception("El dominio es obligatorio");
        }
        if (vehiculo.getDominio().length() > 10) {
            throw new Exception("El dominio no puede superar los 10 caracteres");
        }
        // Validar formato básico de dominio argentino (ABC123 o AB123CD)
        String dominioPattern = "^[A-Z]{2,3}[0-9]{3}([A-Z]{2})?$";
        String dominioUpper = vehiculo.getDominio().trim().toUpperCase();
        if (!dominioUpper.matches(dominioPattern)) {
            throw new Exception("El dominio no tiene un formato válido (ej: ABC123 o AB123CD)");
        }
        
        // Validar marca
        if (vehiculo.getMarca() == null || vehiculo.getMarca().trim().isEmpty()) {
            throw new Exception("La marca es obligatoria");
        }
        if (vehiculo.getMarca().length() > 50) {
            throw new Exception("La marca no puede superar los 50 caracteres");
        }
        
        // Validar modelo
        if (vehiculo.getModelo() == null || vehiculo.getModelo().trim().isEmpty()) {
            throw new Exception("El modelo es obligatorio");
        }
        if (vehiculo.getModelo().length() > 50) {
            throw new Exception("El modelo no puede superar los 50 caracteres");
        }
        
        // Validar año
        if (vehiculo.getAnio() != null) {
            int anioActual = java.time.Year.now().getValue();
            if (vehiculo.getAnio() < 1900 || vehiculo.getAnio() > anioActual + 1) {
                throw new Exception("El año debe estar entre 1900 y " + (anioActual + 1));
            }
        }
        
        // Validar número de chasis
        if (vehiculo.getNroChasis() != null && !vehiculo.getNroChasis().trim().isEmpty()) {
            if (vehiculo.getNroChasis().length() > 50) {
                throw new Exception("El número de chasis no puede superar los 50 caracteres");
            }
        }
        
        // Normalizar datos
        vehiculo.setDominio(vehiculo.getDominio().trim().toUpperCase());
        vehiculo.setMarca(vehiculo.getMarca().trim());
        vehiculo.setModelo(vehiculo.getModelo().trim());
        if (vehiculo.getNroChasis() != null) {
            vehiculo.setNroChasis(vehiculo.getNroChasis().trim().toUpperCase());
        }
    }
    
    /**
     * Valida que el dominio sea único
     */
    private void validarDominioUnico(String dominio, Long vehiculoId) throws Exception {
        Vehiculo existente = vehiculoDao.buscarPorDominio(dominio.trim().toUpperCase());
        
        if (existente != null) {
            // Si es una actualización y es el mismo vehículo, está OK
            if (vehiculoId != null && existente.getId().equals(vehiculoId)) {
                return;
            }
            throw new Exception("Ya existe un vehículo con el dominio: " + dominio);
        }
    }
    
    /**
     * Valida que el número de chasis sea único
     */
    private void validarChasisUnico(String nroChasis, Long vehiculoId) throws Exception {
        Vehiculo existente = vehiculoDao.buscarPorChasis(nroChasis.trim().toUpperCase());
        
        if (existente != null) {
            // Si es una actualización y es el mismo vehículo, está OK
            if (vehiculoId != null && existente.getId().equals(vehiculoId)) {
                return;
            }
            throw new Exception("Ya existe un vehículo con el número de chasis: " + nroChasis);
        }
    }
    
    /**
     * Valida que el seguro no esté ya asignado a otro vehículo (relación 1→1)
     */
    private void validarSeguroDisponible(long seguroId, Long vehiculoId, Connection conn) throws Exception {
        // Buscar si hay algún vehículo con este seguro
        List<Vehiculo> todosVehiculos = vehiculoDao.leerTodos(conn);
        
        for (Vehiculo v : todosVehiculos) {
            if (v.getSeguro() != null && v.getSeguro().getId().equals(seguroId)) {
                // Si es el mismo vehículo que estamos actualizando, está OK
                if (vehiculoId != null && v.getId().equals(vehiculoId)) {
                    continue;
                }
                throw new Exception("El seguro ya está asignado a otro vehículo (Dominio: " + v.getDominio() + ")");
            }
        }
    }
}