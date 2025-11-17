/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package service;
import java.util.List;


/**
 *
 * @author Lenovo
 */


/**
 * *Interfaz genérica que define las operaciones de negocio básicas
 * @param <T> Tipo de entiedad que maneja el Service
 */
public interface GenericService<T> {
    
    /**
     * Inserta una nueva entidad en el sistema
     * Aplica validaciones de negocio y maneja transacciones
     */
    void insertar(T entity) throws Exception;
    
    /**
     * Actualiza una entidad existente
     * Aplica validaciones de negocio y maneja transacciones
     */
    void actualizar(T entity) throws Exception;
    
    /**
     * Elimina lógicamente una entidad
     * Maneja transacciones y efectos en cascada si es necesario
     */
    void eliminar(long id) throws Exception;
    
    /**
     * Obtiene una entidad por su ID
     */
    T getById(long id) throws Exception;
    
    /**
     * Obtiene todas las entidades activas (no eliminadas)
     */
    List<T> getAll() throws Exception;
}