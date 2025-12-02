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
    private List<Platillo> platillos;
    private List<Platillo> platillosListos;
    private boolean entregada;
    private double total;
    
    public Orden(Mesa mesa, Mesero mesero) {
        this.id = contadorId++;
        this.mesa = mesa;
        this.mesero = mesero;
        this.fecha = new Date();
        this.platillos = new ArrayList<>();
        this.platillosListos = new ArrayList<>();
        this.entregada = false;
        this.total = 0.0;
    }
    
    public void agregarPlatillo(Platillo platillo) {
        platillos.add(platillo);
        total += platillo.getPrecio();
    }
    
    public void marcarPlatilloListo(Platillo platillo) {
        if (platillos.contains(platillo) && !platillosListos.contains(platillo)) {
            platillosListos.add(platillo);
        }
    }
    
    public boolean estaLista() {
        return !platillos.isEmpty() && platillosListos.size() == platillos.size();
    }
    
    public void mostrarOrden() {
        System.out.println("Orden #" + id + " - Mesa: " + mesa.getNumero());
        System.out.println("Mesero: " + mesero.getNombre());
        System.out.println("Fecha: " + fecha);
        System.out.println("Platillos:");
        for (Platillo p : platillos) {
            System.out.println("  - " + p.getNombre() + " $" + p.getPrecio());
        }
        System.out.println("Total: $" + total);
        System.out.println("Estado: " + (estaLista() ? "Lista" : "En preparación"));
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public Mesa getMesa() { return mesa; }
    public Mesero getMesero() { return mesero; }
    public double getTotal() { return total; }
    public boolean isEntregada() { return entregada; }
    public void setEntregada(boolean entregada) { this.entregada = entregada; }
}
