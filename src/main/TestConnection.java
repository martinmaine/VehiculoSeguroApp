package main;

import config.DatabaseConnection;

public class TestConnection {
    
    public static void main(String[] args) {
        System.out.println("══════════════════════════════════════════════════════════");
        System.out.println("         PRUEBA DE CONEXIÓN A BASE DE DATOS");
        System.out.println("══════════════════════════════════════════════════════════\n");
        
        DatabaseConnection.printConfiguration();
        
        System.out.println("\n⚡ Probando conexión...\n");
        
        boolean conectado = DatabaseConnection.testConnection();
        
        if (conectado) {
            System.out.println("✓✓✓ CONEXIÓN EXITOSA ✓✓✓");
            System.out.println("La base de datos está lista para usar.\n");
        } else {
            System.out.println("✗✗✗ ERROR DE CONEXIÓN ✗✗✗");
            System.out.println("\nVerifica:");
            System.out.println("  1. MySQL está corriendo en XAMPP (luz verde)");
            System.out.println("  2. La base de datos 'vehiculos_seguros_db' existe");
            System.out.println("  3. Las credenciales son correctas\n");
        }
        
        System.out.println("══════════════════════════════════════════════════════════");
    }
}