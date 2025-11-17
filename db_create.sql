
-- Eliminar la base de datos
DROP DATABASE IF EXISTS vehiculos_seguros_db;

-- Creamos la base de datos

CREATE DATABASE vehiculos_seguros_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Usamos la base de datos

USE vehiculos_seguros_db;

-- ============================================================================
-- Tabla: seguro_vehicular (Clase B)
-- Esta tabla se crea primero porque Vehiculo tiene FK hacia ella
-- ============================================================================

CREATE TABLE seguro_vehicular (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    aseguradora VARCHAR(80) NOT NULL,
    nro_poliza VARCHAR(50) NOT NULL,
    cobertura ENUM('RC', 'TERCEROS', 'TODO_RIESGO') NOT NULL,
    vencimiento DATE NOT NULL,
    
    -- Índices
    CONSTRAINT uk_seguro_nro_poliza UNIQUE (nro_poliza),
    INDEX idx_seguro_aseguradora (aseguradora),
    INDEX idx_seguro_vencimiento (vencimiento),
    INDEX idx_seguro_eliminado (eliminado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- Tabla: vehiculo (Clase A)
-- Contiene la relación 1→1 unidireccional hacia SeguroVehicular
-- ============================================================================

CREATE TABLE vehiculo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    dominio VARCHAR(10) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    anio INT,
    nro_chasis VARCHAR(50),
    seguro_id BIGINT,
    
    -- Constraints
    CONSTRAINT uk_vehiculo_dominio UNIQUE (dominio),
    CONSTRAINT uk_vehiculo_nro_chasis UNIQUE (nro_chasis),
    
    -- Relación 1→1 con SeguroVehicular
    -- UNIQUE garantiza que un seguro solo puede estar asociado a UN vehículo
    CONSTRAINT uk_vehiculo_seguro_id UNIQUE (seguro_id),
    CONSTRAINT fk_vehiculo_seguro FOREIGN KEY (seguro_id) 
        REFERENCES seguro_vehicular(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    
    -- Índices
    INDEX idx_vehiculo_marca (marca),
    INDEX idx_vehiculo_modelo (modelo),
    INDEX idx_vehiculo_anio (anio),
    INDEX idx_vehiculo_eliminado (eliminado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- Comentarios sobre las tablas
-- ============================================================================

ALTER TABLE seguro_vehicular COMMENT = 'Almacena los seguros vehiculares del sistema';
ALTER TABLE vehiculo COMMENT = 'Almacena los vehículos con relación 1→1 a SeguroVehicular';

-- ============================================================================
-- Verificación de la estructura
-- ============================================================================

SHOW TABLES;

DESCRIBE vehiculo;
DESCRIBE seguro_vehicular;

-- ============================================================================
-- Fin del script de creación
-- ============================================================================