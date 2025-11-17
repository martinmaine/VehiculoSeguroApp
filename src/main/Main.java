package main;

import config.DatabaseConnection;

/**
 * Clase principal que inicia la aplicación
 * Sistema de Gestión de Vehículos y Seguros
 */
public class Main {
    
    public static void main(String[] args) {
        mostrarBanner();
        
        // Verificar conexión a la base de datos
        System.out.println("Verificando conexión a la base de datos...");
        if (!DatabaseConnection.testConnection()) {
            System.err.println("\n✗ ERROR: No se pudo conectar a la base de datos");
            System.err.println("Por favor verifica:");
            System.err.println("  1. Que MySQL esté corriendo en XAMPP");
            System.err.println("  2. Que la base de datos 'vehiculos_seguros_db' exista");
            System.err.println("  3. Las credenciales en DatabaseConnection.java");
            System.err.println("\nEjecuta los scripts:");
            System.err.println("  - db_create.sql (crear base de datos y tablas)");
            System.err.println("  - db_data.sql (cargar datos de prueba)");
            System.exit(1);
        }
        
        System.out.println("✓ Conexión exitosa a la base de datos\n");
        
        // Iniciar el menú de la aplicación
        AppMenu menu = new AppMenu();
        menu.mostrarMenu();
        
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║        ¡Gracias por usar el sistema!                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
    
    private static void mostrarBanner() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                            ║");
        System.out.println("║     SISTEMA DE GESTIÓN DE VEHÍCULOS Y SEGUROS             ║");
        System.out.println("║                                                            ║");
        System.out.println("║     Trabajo Final Integrador - Programación 2             ║");
        System.out.println("║     Relación 1→1 Unidireccional                           ║");
        System.out.println("║     Vehiculo → SeguroVehicular                            ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
    }
}