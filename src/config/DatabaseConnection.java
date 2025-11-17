package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión a la base de datos MySQL
 */
public class DatabaseConnection {
    
    // Configuración de la base de datos
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vehiculos_seguros_db?useSSL=false&serverTimezone=America/Argentina/Buenos_Aires&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Si tu MySQL tiene password, ponlo aquí
    
    // Cargar el driver MySQL
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ Driver MySQL cargado correctamente");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ ERROR: No se encontró el driver de MySQL");
            e.printStackTrace();
        }
    }
    
    /**
     * Obtiene una nueva conexión a la base de datos
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(true);
            return conn;
        } catch (SQLException e) {
            System.err.println("✗ Error al conectar a la base de datos:");
            System.err.println("   URL: " + DB_URL);
            System.err.println("   Usuario: " + DB_USER);
            throw e;
        }
    }
    
    /**
     * Cierra una conexión de forma segura
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión:");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Prueba la conexión a la base de datos
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error al probar la conexión:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Muestra la configuración actual
     */
    public static void printConfiguration() {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║         CONFIGURACIÓN DE BASE DE DATOS                ║");
        System.out.println("╠════════════════════════════════════════════════════════╣");
        System.out.println("║ Base de Datos: vehiculos_seguros_db                   ║");
        System.out.println("║ Usuario:       root                                    ║");
        System.out.println("║ Host:          localhost:3306                          ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
    }
}