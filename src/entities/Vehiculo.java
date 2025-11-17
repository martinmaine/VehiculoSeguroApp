package entities;

/**
 * Clase que representa un Vehículo (Clase A)
 * Contiene una relación unidireccional 1→1 con SeguroVehicular
 */
public class Vehiculo {
    
    private Long id;
    private Boolean eliminado;
    private String dominio;
    private String marca;
    private String modelo;
    private Integer anio;
    private String nroChasis;
    private SeguroVehicular seguro; // Relación 1→1 unidireccional

    /**
     * Constructor vacío
     */
    public Vehiculo() {
        this.eliminado = false;
    }

    /**
     * Constructor completo
     */
    public Vehiculo(Long id, Boolean eliminado, String dominio, String marca, 
                   String modelo, Integer anio, String nroChasis, SeguroVehicular seguro) {
        this.id = id;
        this.eliminado = eliminado != null ? eliminado : false;
        this.dominio = dominio;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.nroChasis = nroChasis;
        this.seguro = seguro;
    }

    /**
     * Constructor sin ID (para nuevas inserciones)
     */
    public Vehiculo(String dominio, String marca, String modelo, 
                   Integer anio, String nroChasis, SeguroVehicular seguro) {
        this(null, false, dominio, marca, modelo, anio, nroChasis, seguro);
    }

    /**
     * Constructor sin seguro (para vehículos sin seguro aún)
     */
    public Vehiculo(String dominio, String marca, String modelo, Integer anio, String nroChasis) {
        this(null, false, dominio, marca, modelo, anio, nroChasis, null);
    }

    // Getters y Setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getNroChasis() {
        return nroChasis;
    }

    public void setNroChasis(String nroChasis) {
        this.nroChasis = nroChasis;
    }

    public SeguroVehicular getSeguro() {
        return seguro;
    }

    public void setSeguro(SeguroVehicular seguro) {
        this.seguro = seguro;
    }

    /**
     * Verifica si el vehículo tiene seguro asignado
     */
    public boolean tieneSeguro() {
        return this.seguro != null;
    }

    /**
     * Verifica si el vehículo tiene seguro vigente
     */
    public boolean tieneSeguroVigente() {
        return tieneSeguro() && seguro.estaVigente();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║                    VEHÍCULO                                ║\n");
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ ID:           %-44s ║\n", id != null ? id : "N/A"));
        sb.append(String.format("║ Dominio:      %-44s ║\n", dominio != null ? dominio : "N/A"));
        sb.append(String.format("║ Marca:        %-44s ║\n", marca != null ? marca : "N/A"));
        sb.append(String.format("║ Modelo:       %-44s ║\n", modelo != null ? modelo : "N/A"));
        sb.append(String.format("║ Año:          %-44s ║\n", anio != null ? anio : "N/A"));
        sb.append(String.format("║ Nro. Chasis:  %-44s ║\n", nroChasis != null ? nroChasis : "N/A"));
        sb.append(String.format("║ Eliminado:    %-44s ║\n", eliminado ? "SÍ" : "NO"));
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        
        if (tieneSeguro()) {
            sb.append("║ SEGURO ASOCIADO:                                           ║\n");
            sb.append("╠════════════════════════════════════════════════════════════╣\n");
            sb.append(String.format("║   Aseguradora: %-43s ║\n", seguro.getAseguradora()));
            sb.append(String.format("║   Póliza:      %-43s ║\n", seguro.getNroPoliza()));
            sb.append(String.format("║   Cobertura:   %-43s ║\n", seguro.getCobertura().getDescripcion()));
            sb.append(String.format("║   Estado:      %-43s ║\n", seguro.estaVigente() ? "VIGENTE" : "VENCIDO"));
        } else {
            sb.append("║ SEGURO:       SIN SEGURO ASIGNADO                          ║\n");
        }
        
        sb.append("╚════════════════════════════════════════════════════════════╝");
        
        return sb.toString();
    }
}
