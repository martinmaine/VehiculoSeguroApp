package dao;

import config.DatabaseConnection;
import entities.Cobertura;
import entities.SeguroVehicular;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad SeguroVehicular
 * Implementa todas las operaciones CRUD con PreparedStatement
 */
public class SeguroVehicularDao implements GenericDao<SeguroVehicular> {
    
    // Consultas SQL
    private static final String INSERT = 
        "INSERT INTO seguro_vehicular (aseguradora, nro_poliza, cobertura, vencimiento, eliminado) " +
        "VALUES (?, ?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID = 
        "SELECT id, aseguradora, nro_poliza, cobertura, vencimiento, eliminado " +
        "FROM seguro_vehicular WHERE id = ?";
    
    private static final String SELECT_ALL = 
        "SELECT id, aseguradora, nro_poliza, cobertura, vencimiento, eliminado " +
        "FROM seguro_vehicular WHERE eliminado = FALSE ORDER BY id";
    
    private static final String UPDATE = 
        "UPDATE seguro_vehicular SET aseguradora = ?, nro_poliza = ?, cobertura = ?, " +
        "vencimiento = ?, eliminado = ? WHERE id = ?";
    
    private static final String DELETE_LOGICAL = 
        "UPDATE seguro_vehicular SET eliminado = TRUE WHERE id = ?";
    
    private static final String SELECT_BY_POLIZA = 
        "SELECT id, aseguradora, nro_poliza, cobertura, vencimiento, eliminado " +
        "FROM seguro_vehicular WHERE nro_poliza = ? AND eliminado = FALSE";
    
    @Override
    public void crear(SeguroVehicular entity) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            crear(entity, conn);
        }
    }
    
    @Override
    public void crear(SeguroVehicular entity, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getAseguradora());
            stmt.setString(2, entity.getNroPoliza());
            stmt.setString(3, entity.getCobertura().name());
            stmt.setDate(4, Date.valueOf(entity.getVencimiento()));
            stmt.setBoolean(5, entity.getEliminado() != null ? entity.getEliminado() : false);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Error al crear el seguro, ninguna fila afectada");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
        }
    }
    
    @Override
    public SeguroVehicular leer(long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return leer(id, conn);
        }
    }
    
    @Override
    public SeguroVehicular leer(long id, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
                return null;
            }
        }
    }
    
    @Override
    public List<SeguroVehicular> leerTodos() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return leerTodos(conn);
        }
    }
    
    @Override
    public List<SeguroVehicular> leerTodos(Connection conn) throws Exception {
        List<SeguroVehicular> seguros = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                seguros.add(mapResultSetToEntity(rs));
            }
        }
        
        return seguros;
    }
    
    @Override
    public void actualizar(SeguroVehicular entity) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            actualizar(entity, conn);
        }
    }
    
    @Override
    public void actualizar(SeguroVehicular entity, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            stmt.setString(1, entity.getAseguradora());
            stmt.setString(2, entity.getNroPoliza());
            stmt.setString(3, entity.getCobertura().name());
            stmt.setDate(4, Date.valueOf(entity.getVencimiento()));
            stmt.setBoolean(5, entity.getEliminado());
            stmt.setLong(6, entity.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Error al actualizar el seguro, ninguna fila afectada. ID: " + entity.getId());
            }
        }
    }
    
    @Override
    public void eliminar(long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            eliminar(id, conn);
        }
    }
    
    @Override
    public void eliminar(long id, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_LOGICAL)) {
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Error al eliminar el seguro, ninguna fila afectada. ID: " + id);
            }
        }
    }
    
    /**
     * Busca un seguro por número de póliza
     */
    public SeguroVehicular buscarPorPoliza(String nroPoliza) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return buscarPorPoliza(nroPoliza, conn);
        }
    }
    
    /**
     * Busca un seguro por número de póliza usando una conexión externa
     */
    public SeguroVehicular buscarPorPoliza(String nroPoliza, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_POLIZA)) {
            stmt.setString(1, nroPoliza);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
                return null;
            }
        }
    }
    
    /**
     * Mapea un ResultSet a una entidad SeguroVehicular
     */
    private SeguroVehicular mapResultSetToEntity(ResultSet rs) throws SQLException {
        SeguroVehicular seguro = new SeguroVehicular();
        
        seguro.setId(rs.getLong("id"));
        seguro.setEliminado(rs.getBoolean("eliminado"));
        seguro.setAseguradora(rs.getString("aseguradora"));
        seguro.setNroPoliza(rs.getString("nro_poliza"));
        seguro.setCobertura(Cobertura.valueOf(rs.getString("cobertura")));
        
        Date vencimiento = rs.getDate("vencimiento");
        if (vencimiento != null) {
            seguro.setVencimiento(vencimiento.toLocalDate());
        }
        
        return seguro;
    }
}