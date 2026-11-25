package application.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Comprobante de devolucion y reembolso
public class Devolucion {

    private String folioDevolucion;
    private String folioVentaOriginal;
    private LocalDateTime fechaHora;
    private String motivo;
    private boolean reintegrarInventario;
    private double totalReembolso;
    private List<ItemDevolucion> items;

    public Devolucion() {
        this.items = new ArrayList<>();
        this.fechaHora = LocalDateTime.now();
    }

    public Devolucion(String folioDevolucion, String folioVentaOriginal, String motivo,
                      boolean reintegrarInventario, double totalReembolso, List<ItemDevolucion> items) {
        this.folioDevolucion = folioDevolucion;
        this.folioVentaOriginal = folioVentaOriginal;
        this.fechaHora = LocalDateTime.now();
        this.motivo = motivo;
        this.reintegrarInventario = reintegrarInventario;
        this.totalReembolso = totalReembolso;
        this.items = new ArrayList<>(items);
    }

    public String getFolioDevolucion() { return folioDevolucion; }
    public void setFolioDevolucion(String folioDevolucion) { this.folioDevolucion = folioDevolucion; }

    public String getFolioVentaOriginal() { return folioVentaOriginal; }
    public void setFolioVentaOriginal(String folioVentaOriginal) { this.folioVentaOriginal = folioVentaOriginal; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public boolean isReintegrarInventario() { return reintegrarInventario; }
    public void setReintegrarInventario(boolean reintegrarInventario) { this.reintegrarInventario = reintegrarInventario; }

    public double getTotalReembolso() { return totalReembolso; }
    public void setTotalReembolso(double totalReembolso) { this.totalReembolso = totalReembolso; }

    public List<ItemDevolucion> getItems() { return items; }
    public void setItems(List<ItemDevolucion> items) { this.items = items; }
}
