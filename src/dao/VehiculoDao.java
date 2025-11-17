package dao;

import config.DatabaseConnection;
import entities.SeguroVehicular;
import entities.Vehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Vehiculo
 * Implementa todas las operaciones CRUD con PreparedStatement
 * Maneja la relación 1→1 con SeguroVehicular
 */
public class VehiculoDao implements GenericDao<Vehiculo> {
    
    private final SeguroVehicularDao seguroDao;
    
    // Consultas SQL
    private static final String INSERT = 
        "INSERT INTO vehiculo (dominio, marca, modelo, anio, nro_chasis, seguro_id, eliminado) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID = 
        "SELECT id, dominio, marca, modelo, anio, nro_chasis, seguro_id, eliminado " +
        "FROM vehiculo WHERE id = ?";
    
    private static final String SELECT_ALL = 
        "SELECT id, dominio, marca, modelo, anio, nro_chasis, seguro_id, eliminado " +
        "FROM vehiculo WHERE eliminado = FALSE ORDER BY id";
    
    private static final String UPDATE = 
        "UPDATE vehiculo SET dominio = ?, marca = ?, modelo = ?, anio = ?, " +
        "nro_chasis = ?, seguro_id = ?, eliminado = ? WHERE id = ?";
    
    private static final String DELETE_LOGICAL = 
        "UPDATE vehiculo SET eliminado = TRUE WHERE id = ?";
    
    private static final String SELECT_BY_DOMINIO = 
        "SELECT id, dominio, marca, modelo, anio, nro_chasis, seguro_id, eliminado " +
        "FROM vehiculo WHERE dominio = ? AND eliminado = FALSE";
    
    private static final String SELECT_BY_CHASIS = 
        "SELECT id, dominio, marca, modelo, anio, nro_chasis, seguro_id, eliminado " +
        "FROM vehiculo WHERE nro_chasis = ? AND eliminado = FALSE";
    
    /**
     * Constructor
     */
    public VehiculoDao() {
        this.seguroDao = new SeguroVehicularDao();
    }
    
    @Override
    public void crear(Vehiculo entity) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            crear(entity, conn);
        }
    }
    
    @Override
    public void crear(Vehiculo entity, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getDominio());
            stmt.setString(2, entity.getMarca());
            stmt.setString(3, entity.getModelo());
            
            if (entity.getAnio() != null) {
                stmt.setInt(4, entity.getAnio());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            stmt.setString(5, entity.getNroChasis());
            
            // Manejar la relación con SeguroVehicular
            if (entity.getSeguro() != null && entity.getSeguro().getId() != null) {
                stmt.setLong(6, entity.getSeguro().getId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }
            
            stmt.setBoolean(7, entity.getEliminado() != null ? entity.getEliminado() : false);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Error al crear el vehículo, ninguna fila afectada");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
        }
    }
    
    @Override
    public Vehiculo leer(long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return leer(id, conn);
        }
    }
    
    @Override
    public Vehiculo leer(long id, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs, conn);
                }
                return null;
            }
        }
    }
    
    @Override
    public List<Vehiculo> leerTodos() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return leerTodos(conn);
        }
    }
    
    @Override
    public List<Vehiculo> leerTodos(Connection conn) throws Exception {
        List<Vehiculo> vehiculos = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                vehiculos.add(mapResultSetToEntity(rs, conn));
            }
        }
        
        return vehiculos;
    }
    
    @Override
    public void actualizar(Vehiculo entity) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            actualizar(entity, conn);
        }
    }
    
    @Override
    public void actualizar(Vehiculo entity, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            stmt.setString(1, entity.getDominio());
            stmt.setString(2, entity.getMarca());
            stmt.setString(3, entity.getModelo());
            
            if (entity.getAnio() != null) {
                stmt.setInt(4, entity.getAnio());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            stmt.setString(5, entity.getNroChasis());
            
            // Manejar la relación con SeguroVehicular
            if (entity.getSeguro() != null && entity.getSeguro().getId() != null) {
                stmt.setLong(6, entity.getSeguro().getId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }
            
            stmt.setBoolean(7, entity.getEliminado());
            stmt.setLong(8, entity.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Error al actualizar el vehículo, ninguna fila afectada. ID: " + entity.getId());
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
                throw new SQLException("Error al eliminar el vehículo, ninguna fila afectada. ID: " + id);
            }
        }
    }
    
    /**
     * Busca un vehículo por dominio
     */
    public Vehiculo buscarPorDominio(String dominio) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return buscarPorDominio(dominio, conn);
        }
    }
    
    /**
     * Busca un vehículo por dominio usando una conexión externa
     */
    public Vehiculo buscarPorDominio(String dominio, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_DOMINIO)) {
            stmt.setString(1, dominio);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs, conn);
                }
                return null;
            }
        }
    }
    
    /**
     * Busca un vehículo por número de chasis
     */
    public Vehiculo buscarPorChasis(String nroChasis) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return buscarPorChasis(nroChasis, conn);
        }
    }
    
    /**
     * Busca un vehículo por número de chasis usando una conexión externa
     */
    public Vehiculo buscarPorChasis(String nroChasis, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_CHASIS)) {
            stmt.setString(1, nroChasis);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs, conn);
                }
                return null;
            }
        }
    }
    
    /**
     * Mapea un ResultSet a una entidad Vehiculo
     * Carga también el SeguroVehicular asociado si existe
     */
    private Vehiculo mapResultSetToEntity(ResultSet rs, Connection conn) throws Exception {
        Vehiculo vehiculo = new Vehiculo();
        
        vehiculo.setId(rs.getLong("id"));
        vehiculo.setEliminado(rs.getBoolean("eliminado"));
        vehiculo.setDominio(rs.getString("dominio"));
        vehiculo.setMarca(rs.getString("marca"));
        vehiculo.setModelo(rs.getString("modelo"));
        
        int anio = rs.getInt("anio");
        if (!rs.wasNull()) {
            vehiculo.setAnio(anio);
        }
        
        vehiculo.setNroChasis(rs.getString("nro_chasis"));
        
        // Cargar el SeguroVehicular asociado si existe
        long seguroId = rs.getLong("seguro_id");
        if (!rs.wasNull() && seguroId > 0) {
            SeguroVehicular seguro = seguroDao.leer(seguroId, conn);
            vehiculo.setSeguro(seguro);
        }
        
        return vehiculo;
    }
}
