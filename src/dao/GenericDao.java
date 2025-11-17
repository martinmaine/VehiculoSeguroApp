/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;
import java.sql.Connection;
import java.util.List;


/**
 *
 * @author Lenovo
 */

/**
 * Interfaz genérica que define las operaciones CRUD básicas para DAOs
 * 
 * @param <T> Tipo de entidad que maneja el DAO
 */
public interface GenericDao<T> {
    
    /**
     * Crea una nueva entidad en la base de datos
     */
    void crear(T entity) throws Exception;
    
    /**
     * Crea una nueva entidad usando una conexión externa (para transacciones)
     */
    void crear(T entity, Connection conn) throws Exception;
    
    /**
     * Lee una entidad por su ID
     */
    T leer(long id) throws Exception;
    
    /**
     * Lee una entidad por su ID usando una conexión externa
     */
    T leer(long id, Connection conn) throws Exception;
    
    /**
     * Obtiene todas las entidades (no eliminadas lógicamente)
     */
    List<T> leerTodos() throws Exception;
    
    /**
     * Obtiene todas las entidades usando una conexión externa
     */
    List<T> leerTodos(Connection conn) throws Exception;
    
    /**
     * Actualiza una entidad existente
     */
    void actualizar(T entity) throws Exception;
    
    /**
     * Actualiza una entidad usando una conexión externa (para transacciones)
     */
    void actualizar(T entity, Connection conn) throws Exception;
    
    /**
     * Elimina lógicamente una entidad (marca eliminado = true)
     */
    void eliminar(long id) throws Exception;
    
    /**
     * Elimina lógicamente una entidad usando una conexión externa
     */
    void eliminar(long id, Connection conn) throws Exception;
}