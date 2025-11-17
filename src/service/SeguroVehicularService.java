package service;

import config.DatabaseConnection;
import dao.SeguroVehicularDao;
import entities.SeguroVehicular;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Servicio para la entidad SeguroVehicular
 * Implementa reglas de negocio, validaciones y manejo de transacciones
 */
public class SeguroVehicularService implements GenericService<SeguroVehicular> {
    
    private final SeguroVehicularDao seguroDao;
    
    public SeguroVehicularService() {
        this.seguroDao = new SeguroVehicularDao();
    }
    
    @Override
    public void insertar(SeguroVehicular entity) throws Exception {
        Connection conn = null;
        try {
            // Validaciones de negocio
            validarSeguro(entity);
            validarPolizaUnica(entity.getNroPoliza(), null);
            
            // Iniciar transacción
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Ejecutar operación
            seguroDao.crear(entity, conn);
            
            // Commit si todo salió bien
            conn.commit();
            
        } catch (Exception e) {
            // Rollback en caso de error
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("ROLLBACK ejecutado en insertar SeguroVehicular");
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            throw new Exception("Error al insertar seguro: " + e.getMessage(), e);
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
    
    @Override
    public void actualizar(SeguroVehicular entity) throws Exception {
        Connection conn = null;
        try {
            // Validaciones
            if (entity.getId() == null || entity.getId() <= 0) {
                throw new Exception("El ID del seguro es requerido para actualizar");
            }
            
            // Verificar que existe
            SeguroVehicular existente = seguroDao.leer(entity.getId());
            if (existente == null) {
                throw new Exception("No existe un seguro con ID: " + entity.getId());
            }
            
            validarSeguro(entity);
            
            // Validar unicidad de póliza si cambió
            if (!existente.getNroPoliza().equals(entity.getNroPoliza())) {
                validarPolizaUnica(entity.getNroPoliza(), entity.getId());
            }
            
            // Iniciar transacción
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Ejecutar operación
            seguroDao.actualizar(entity, conn);
            
            // Commit
            conn.commit();
            
        } catch (Exception e) {
            // Rollback en caso de error
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("ROLLBACK ejecutado en actualizar SeguroVehicular");
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            throw new Exception("Error al actualizar seguro: " + e.getMessage(), e);
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
            SeguroVehicular existente = seguroDao.leer(id);
            if (existente == null) {
                throw new Exception("No existe un seguro con ID: " + id);
            }
            
            // Iniciar transacción
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Ejecutar operación (eliminación lógica)
            seguroDao.eliminar(id, conn);
            
            // Commit
            conn.commit();
            
        } catch (Exception e) {
            // Rollback en caso de error
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("ROLLBACK ejecutado en eliminar SeguroVehicular");
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            throw new Exception("Error al eliminar seguro: " + e.getMessage(), e);
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
    public SeguroVehicular getById(long id) throws Exception {
        return seguroDao.leer(id);
    }
    
    @Override
    public List<SeguroVehicular> getAll() throws Exception {
        return seguroDao.leerTodos();
    }
    
    /**
     * Busca un seguro por número de póliza
     */
    public SeguroVehicular buscarPorPoliza(String nroPoliza) throws Exception {
        if (nroPoliza == null || nroPoliza.trim().isEmpty()) {
            throw new Exception("El número de póliza no puede estar vacío");
        }
        return seguroDao.buscarPorPoliza(nroPoliza.trim().toUpperCase());
    }
    
    /**
     * Valida los datos de un SeguroVehicular según reglas de negocio
     */
    private void validarSeguro(SeguroVehicular seguro) throws Exception {
        if (seguro == null) {
            throw new Exception("El seguro no puede ser nulo");
        }
        
        // Validar aseguradora
        if (seguro.getAseguradora() == null || seguro.getAseguradora().trim().isEmpty()) {
            throw new Exception("La aseguradora es obligatoria");
        }
        if (seguro.getAseguradora().length() > 80) {
            throw new Exception("La aseguradora no puede superar los 80 caracteres");
        }
        
        // Validar número de póliza
        if (seguro.getNroPoliza() == null || seguro.getNroPoliza().trim().isEmpty()) {
            throw new Exception("El número de póliza es obligatorio");
        }
        if (seguro.getNroPoliza().length() > 50) {
            throw new Exception("El número de póliza no puede superar los 50 caracteres");
        }
        
        // Validar cobertura
        if (seguro.getCobertura() == null) {
            throw new Exception("La cobertura es obligatoria");
        }
        
        // Validar vencimiento
        if (seguro.getVencimiento() == null) {
            throw new Exception("La fecha de vencimiento es obligatoria");
        }
        
        // Advertencia si el vencimiento es muy antiguo (más de 2 años atrás)
        LocalDate hace2Anios = LocalDate.now().minusYears(2);
        if (seguro.getVencimiento().isBefore(hace2Anios)) {
            System.out.println("⚠ ADVERTENCIA: El seguro tiene una fecha de vencimiento muy antigua");
        }
        
        // Normalizar datos
        seguro.setAseguradora(seguro.getAseguradora().trim());
        seguro.setNroPoliza(seguro.getNroPoliza().trim().toUpperCase());
    }
    
    /**
     * Valida que el número de póliza sea único
     */
    private void validarPolizaUnica(String nroPoliza, Long seguroId) throws Exception {
        SeguroVehicular existente = seguroDao.buscarPorPoliza(nroPoliza.trim().toUpperCase());
        
        if (existente != null) {
            // Si es una actualización y es el mismo seguro, está OK
            if (seguroId != null && existente.getId().equals(seguroId)) {
                return;
            }
            throw new Exception("Ya existe un seguro con el número de póliza: " + nroPoliza);
        }
    }
}