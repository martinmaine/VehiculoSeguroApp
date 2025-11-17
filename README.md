Tectinicatura en Programación
Universidad Tecnológica Nacional

**Trabajo Final Integrador - Programación 2**
Sistema de Gestión de Vehículos y Seguros


## 👥 Equipo

Martín Maine
Juan Martinez
Gevont Joaquin Utmazian


##  🎬 Video: 


Características

- ✅ Arquitectura en capas (Entities, DAO, Service, UI)
- ✅ Patrón DAO con PreparedStatement
- ✅ Transacciones con commit/rollback
- ✅ Relación 1→1 unidireccional garantizada
- ✅ CRUD completo de ambas entidades
- ✅ Baja lógica (soft delete)
- ✅ Validaciones de negocio
- ✅ Menú de consola interactivo

Arquitectura
```
src/
├── config/          - Configuración de BD
├── entities/        - Clases de dominio
├── dao/             - Acceso a datos
├── service/         - Lógica de negocio
└── main/            - Interfaz de usuario

## 📦 Instalación

### 1. Clonar el repositorio
```bash
git clone https://github.com/TU-USUARIO/VehiculoSeguroApp.git
cd VehiculoSeguroApp
```

### 2. Configurar la Base de Datos

**Opción A: MySQL Command Line**
```bash
mysql -u root -p < db_create.sql
mysql -u root -p vehiculos_seguros_db < db_data.sql
```

**Opción B: MySQL Workbench**
1. Abrir MySQL Workbench
2. Ejecutar `db_create.sql`
3. Ejecutar `db_data.sql`

### 3. Configurar credenciales

Edita `src/config/DatabaseConnection.java`:
```java
private static final String DB_PASSWORD = ""; // Tu contraseña
```

### 4. Compilar y ejecutar
```bash
# Si usas Maven
mvn clean install
mvn exec:java -Dexec.mainClass="main.Main"

# Si usas NetBeans
# Run File en Main.java
```

---

## 🎮 Uso

### Menú Principal

1. **Gestión de Vehículos** - CRUD completo
2. **Gestión de Seguros** - CRUD completo
3. **Operaciones Especiales** - Transacciones complejas

### Operaciones Destacadas

- ✅ Crear vehículo con seguro (transacción atómica)
- ✅ Búsqueda por dominio
- ✅ Búsqueda por póliza
- ✅ Asignar/desasignar seguros
- ✅ Demostración de ROLLBACK

---

## 📊 Base de Datos

### Tablas

- `seguro_vehicular` - Seguros vehiculares
- `vehiculo` - Vehículos (con FK UNIQUE a seguros)

### Relación 1→1
```
vehiculo.seguro_id → seguro_vehicular.id (UNIQUE)
```

---


