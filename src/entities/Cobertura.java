package entities;

/**
 * Enum que representa los tipos de cobertura de seguro vehicular
 */
public enum Cobertura {
    RC("Responsabilidad Civil"),
    TERCEROS("Terceros Completo"),
    TODO_RIESGO("Todo Riesgo");

    private final String descripcion;

    Cobertura(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return this.name() + " - " + descripcion;
    }
}