package entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Clase que representa un Seguro Vehicular (Clase B)
 * Contiene la información del seguro asociado a un vehículo
 */
public class SeguroVehicular {
    
    private Long id;
    private Boolean eliminado;
    private String aseguradora;
    private String nroPoliza;
    private Cobertura cobertura;
    private LocalDate vencimiento;

    /**
     * Constructor vacío
     */
    public SeguroVehicular() {
        this.eliminado = false;
    }

    /**
     * Constructor completo
     */
    public SeguroVehicular(Long id, Boolean eliminado, String aseguradora, 
                          String nroPoliza, Cobertura cobertura, LocalDate vencimiento) {
        this.id = id;
        this.eliminado = eliminado != null ? eliminado : false;
        this.aseguradora = aseguradora;
        this.nroPoliza = nroPoliza;
        this.cobertura = cobertura;
        this.vencimiento = vencimiento;
    }

    /**
     * Constructor sin ID (para nuevas inserciones)
     */
    public SeguroVehicular(String aseguradora, String nroPoliza, 
                          Cobertura cobertura, LocalDate vencimiento) {
        this(null, false, aseguradora, nroPoliza, cobertura, vencimiento);
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

    public String getAseguradora() {
        return aseguradora;
    }

    public void setAseguradora(String aseguradora) {
        this.aseguradora = aseguradora;
    }

    public String getNroPoliza() {
        return nroPoliza;
    }

    public void setNroPoliza(String nroPoliza) {
        this.nroPoliza = nroPoliza;
    }

    public Cobertura getCobertura() {
        return cobertura;
    }

    public void setCobertura(Cobertura cobertura) {
        this.cobertura = cobertura;
    }

    public LocalDate getVencimiento() {
        return vencimiento;
    }

    public void setVencimiento(LocalDate vencimiento) {
        this.vencimiento = vencimiento;
    }

    /**
     * Verifica si el seguro está vigente
     */
    public boolean estaVigente() {
        if (vencimiento == null) return false;
        return !vencimiento.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║              SEGURO VEHICULAR                              ║\n");
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ ID:           %-44s ║\n", id != null ? id : "N/A"));
        sb.append(String.format("║ Aseguradora:  %-44s ║\n", aseguradora != null ? aseguradora : "N/A"));
        sb.append(String.format("║ Nro. Póliza:  %-44s ║\n", nroPoliza != null ? nroPoliza : "N/A"));
        sb.append(String.format("║ Cobertura:    %-44s ║\n", cobertura != null ? cobertura.getDescripcion() : "N/A"));
        sb.append(String.format("║ Vencimiento:  %-44s ║\n", vencimiento != null ? vencimiento.format(formatter) : "N/A"));
        sb.append(String.format("║ Estado:       %-44s ║\n", estaVigente() ? "VIGENTE" : "VENCIDO"));
        sb.append(String.format("║ Eliminado:    %-44s ║\n", eliminado ? "SÍ" : "NO"));
        sb.append("╚════════════════════════════════════════════════════════════╝");
        
        return sb.toString();
    }
}