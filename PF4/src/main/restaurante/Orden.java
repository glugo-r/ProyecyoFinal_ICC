package restaurante;

import usuarios.Mesero;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Orden {
    private static int contadorId = 1;
    private int id;
    private Mesa mesa;
    private Mesero mesero;
    private Date fecha;
    private List<ItemOrden> items;
    private boolean entregada;
    private double total;
    
    public Orden(Mesa mesa, Mesero mesero) {
        this.id = contadorId++;
        this.mesa = mesa;
        this.mesero = mesero;
        this.fecha = new Date();
        this.items = new ArrayList<>();
        this.entregada = false;
        this.total = 0.0;
    }
    
    public void agregarPlatillo(Platillo platillo, int cantidad) {
        // Verificar si ya existe este platillo en la orden
        for (ItemOrden item : items) {
            if (item.getPlatillo().getId() == platillo.getId()) {
                // Podríamos incrementar la cantidad, pero por simplicidad
                // agregamos como nuevo item por ahora
            }
        }
        items.add(new ItemOrden(platillo, cantidad));
        total += platillo.getPrecio() * cantidad;
    }
    
    public void agregarItem(ItemOrden item) {
        items.add(item);
        total += item.getSubtotal();
    }
    
    public void marcarPlatilloListo(Platillo platillo) {
    for (ItemOrden item : items) {
        if (item.getPlatillo().getId() == platillo.getId() && !item.estaCompleto()) {
            item.marcarListo(); // Esto solo marca UNA unidad
            return;
        }
    }
}
    
    public boolean estaLista() {
        if (items.isEmpty()) return false;
        for (ItemOrden item : items) {
            if (!item.estaCompleto()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean platilloEstaEnOrden(Platillo platillo) {
        for (ItemOrden item : items) {
            if (item.getPlatillo().getId() == platillo.getId() && !item.estaCompleto()) {
                return true;
            }
        }
        return false;
    }
    
    public List<ItemOrden> getItemsPendientes() {
        List<ItemOrden> pendientes = new ArrayList<>();
        for (ItemOrden item : items) {
            if (!item.estaCompleto()) {
                pendientes.add(item);
            }
        }
        return pendientes;
    }
    
    public List<ItemOrden> getItemsListos() {
        List<ItemOrden> listos = new ArrayList<>();
        for (ItemOrden item : items) {
            if (item.estaCompleto()) {
                listos.add(item);
            }
        }
        return listos;
    }
    
    public int getTotalPlatillos() {
        int total = 0;
        for (ItemOrden item : items) {
            total += item.getCantidad();
        }
        return total;
    }
    
    public int getCantidadPlatillosListos() {
        int listos = 0;
        for (ItemOrden item : items) {
            listos += item.getCantidadLista();
        }
        return listos;
    }
    
    public int getCantidadPlatillosPendientes() {
        return getTotalPlatillos() - getCantidadPlatillosListos();
    }
    
    // Para compatibilidad con código que espera lista de platillos
    public List<Platillo> getPlatillos() {
        List<Platillo> platillos = new ArrayList<>();
        for (ItemOrden item : items) {
            for (int i = 0; i < item.getCantidad(); i++) {
                platillos.add(item.getPlatillo());
            }
        }
        return platillos;
    }
    
    // Para compatibilidad
    public List<Platillo> getListaPlatillosListos() {
        List<Platillo> platillosListos = new ArrayList<>();
        for (ItemOrden item : items) {
            for (int i = 0; i < item.getCantidadLista(); i++) {
                platillosListos.add(item.getPlatillo());
            }
        }
        return platillosListos;
    }
    
    public void mostrarOrden() {
        System.out.println("\n=== ORDEN #" + id + " ===");
        System.out.println("Mesa: " + mesa.getNumero());
        System.out.println("Mesero: " + mesero.getNombre());
        System.out.println("Fecha: " + fecha);
        System.out.println("Items:");
        
        for (ItemOrden item : items) {
            item.mostrarItem();
        }
        
        System.out.println("Total: $" + total);
        System.out.println("Estado general: " + (estaLista() ? "✅ LISTA PARA ENTREGAR" : "🔄 EN PREPARACIÓN"));
        System.out.println("Progreso: " + getCantidadPlatillosListos() + "/" + getTotalPlatillos() + " platillos listos");
    }
    
    public void mostrarParaCocinero() {
        System.out.println("\n=== ORDEN #" + id + " ===");
        System.out.println("Mesa: " + mesa.getNumero());
        System.out.println("Items a preparar:");
        
        if (items.isEmpty()) {
            System.out.println("  No hay items en esta orden.");
            return;
        }
        
        for (ItemOrden item : items) {
            if (!item.estaCompleto()) {
                System.out.println("  [ID Platillo: " + item.getPlatillo().getId() + "] " + 
                                 item.getPlatillo().getNombre() + 
                                 " x" + item.getCantidadPendiente() + 
                                 " de " + item.getCantidad() + 
                                 " | Tiempo: " + item.getPlatillo().getTiempoPreparacion() + " min c/u");
            }
        }
        
        // Mostrar items ya listos
        boolean hayListos = false;
        for (ItemOrden item : items) {
            if (item.getCantidadLista() > 0) {
                if (!hayListos) {
                    System.out.println("\nItems ya listos:");
                    hayListos = true;
                }
                System.out.println("  [ID Platillo: " + item.getPlatillo().getId() + "] " + 
                                 item.getPlatillo().getNombre() + 
                                 " " + item.getCantidadLista() + "/" + item.getCantidad() + " ✅");
            }
        }
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public Mesa getMesa() { return mesa; }
    public Mesero getMesero() { return mesero; }
    public Date getFecha() { return fecha; }
    public double getTotal() { return total; }
    public boolean isEntregada() { return entregada; }
    public List<ItemOrden> getItems() { return new ArrayList<>(items); }
    public void setEntregada(boolean entregada) { this.entregada = entregada; }
}
