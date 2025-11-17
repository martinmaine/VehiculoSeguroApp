
-- ============================================================================
-- Script de Datos de Prueba - Sistema de Vehículos y Seguros
-- ============================================================================

USE vehiculos_seguros_db;

-- ============================================================================
-- Insertar datos en seguro_vehicular
-- ============================================================================

INSERT INTO seguro_vehicular (aseguradora, nro_poliza, cobertura, vencimiento, eliminado) VALUES
('La Caja Seguros', 'POL-2024-001', 'TODO_RIESGO', '2025-12-31', FALSE),
('Sancor Seguros', 'POL-2024-002', 'TERCEROS', '2025-06-30', FALSE),
('Mercantil Andina', 'POL-2024-003', 'RC', '2024-12-31', FALSE),
('Allianz Argentina', 'POL-2024-004', 'TODO_RIESGO', '2026-03-15', FALSE),
('Federación Patronal', 'POL-2024-005', 'TERCEROS', '2025-09-20', FALSE),
('Río Uruguay Seguros', 'POL-2024-006', 'TODO_RIESGO', '2025-11-10', FALSE),
('Provincia Seguros', 'POL-2024-007', 'RC', '2024-11-30', FALSE),
('ATM Seguros', 'POL-2024-008', 'TERCEROS', '2025-08-25', FALSE),
('Berkley International', 'POL-2024-009', 'TODO_RIESGO', '2026-01-18', FALSE),
('Zurich Argentina', 'POL-2024-010', 'TERCEROS', '2025-07-12', FALSE),
-- Seguros sin asignar a vehículos
('San Cristóbal Seguros', 'POL-2024-011', 'RC', '2025-05-05', FALSE),
('Galeno Seguros', 'POL-2024-012', 'TODO_RIESGO', '2026-02-28', FALSE);

-- ============================================================================
-- Insertar datos en vehiculo
-- ============================================================================

INSERT INTO vehiculo (dominio, marca, modelo, anio, nro_chasis, seguro_id, eliminado) VALUES
-- Vehículos con seguro asignado
('AB123CD', 'Toyota', 'Corolla', 2020, 'CHASIS001-TOYOTA', 1, FALSE),
('EF456GH', 'Ford', 'Focus', 2019, 'CHASIS002-FORD', 2, FALSE),
('IJ789KL', 'Chevrolet', 'Cruze', 2021, 'CHASIS003-CHEVROLET', 3, FALSE),
('MN012OP', 'Volkswagen', 'Vento', 2022, 'CHASIS004-VW', 4, FALSE),
('QR345ST', 'Fiat', 'Cronos', 2020, 'CHASIS005-FIAT', 5, FALSE),
('UV678WX', 'Peugeot', '208', 2021, 'CHASIS006-PEUGEOT', 6, FALSE),
('YZ901AB', 'Renault', 'Sandero', 2018, 'CHASIS007-RENAULT', 7, FALSE),
('CD234EF', 'Nissan', 'Versa', 2023, 'CHASIS008-NISSAN', 8, FALSE),
('GH567IJ', 'Honda', 'Civic', 2022, 'CHASIS009-HONDA', 9, FALSE),
('KL890MN', 'Citroën', 'C4', 2019, 'CHASIS010-CITROEN', 10, FALSE),

-- Vehículos sin seguro asignado
('OP123QR', 'Hyundai', 'Elantra', 2021, 'CHASIS011-HYUNDAI', NULL, FALSE),
('ST456UV', 'Kia', 'Rio', 2020, 'CHASIS012-KIA', NULL, FALSE),
('WX789YZ', 'Suzuki', 'Swift', 2019, 'CHASIS013-SUZUKI', NULL, FALSE),

-- Vehículos eliminados lógicamente
('AA111BB', 'Ford', 'Fiesta', 2017, 'CHASIS014-FORD-OLD', NULL, TRUE),
('CC222DD', 'Chevrolet', 'Onix', 2018, 'CHASIS015-CHEVROLET-OLD', NULL, TRUE);

-- ============================================================================
-- Consultas de verificación
-- ============================================================================

-- Ver todos los seguros
SELECT 
    id,
    aseguradora,
    nro_poliza,
    cobertura,
    vencimiento,
    CASE 
        WHEN vencimiento >= CURDATE() THEN 'VIGENTE'
        ELSE 'VENCIDO'
    END AS estado,
    eliminado
FROM seguro_vehicular
ORDER BY id;

-- Ver todos los vehículos con sus seguros
SELECT 
    v.id,
    v.dominio,
    v.marca,
    v.modelo,
    v.anio,
    v.nro_chasis,
    s.aseguradora,
    s.nro_poliza,
    s.cobertura,
    s.vencimiento,
    v.eliminado
FROM vehiculo v
LEFT JOIN seguro_vehicular s ON v.seguro_id = s.id
ORDER BY v.id;

-- Ver solo vehículos activos (no eliminados)
SELECT 
    v.dominio,
    v.marca,
    v.modelo,
    v.anio,
    COALESCE(s.aseguradora, 'SIN SEGURO') AS aseguradora,
    COALESCE(s.nro_poliza, 'N/A') AS nro_poliza
FROM vehiculo v
LEFT JOIN seguro_vehicular s ON v.seguro_id = s.id
WHERE v.eliminado = FALSE
ORDER BY v.dominio;

-- Ver seguros disponibles (no asignados a ningún vehículo)
SELECT 
    s.id,
    s.aseguradora,
    s.nro_poliza,
    s.cobertura,
    s.vencimiento
FROM seguro_vehicular s
WHERE s.id NOT IN (SELECT seguro_id FROM vehiculo WHERE seguro_id IS NOT NULL)
  AND s.eliminado = FALSE
ORDER BY s.id;

-- Estadísticas
SELECT 
    'Total Vehículos' AS concepto,
    COUNT(*) AS cantidad
FROM vehiculo
UNION ALL
SELECT 
    'Vehículos Activos',
    COUNT(*)
FROM vehiculo
WHERE eliminado = FALSE
UNION ALL
SELECT 
    'Vehículos con Seguro',
    COUNT(*)
FROM vehiculo
WHERE seguro_id IS NOT NULL AND eliminado = FALSE
UNION ALL
SELECT 
    'Total Seguros',
    COUNT(*)
FROM seguro_vehicular
UNION ALL
SELECT 
    'Seguros Vigentes',
    COUNT(*)
FROM seguro_vehicular
WHERE vencimiento >= CURDATE() AND eliminado = FALSE;