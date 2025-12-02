package usuarios;

import java.util.ArrayList;
import java.util.List;
import restaurante.*;

public class Mesero extends Empleado {
    private int mesasAtendidas;
    private List<Orden> ordenesActivas;
    
    public Mesero(String nombre, String email, String password) {
        super(nombre, email, "Mesero", password);
        this.mesasAtendidas = 0;
        this.ordenesActivas = new ArrayList<>();
    }
    
    public Orden tomarPedido(Mesa mesa, List<Platillo> platillos) {
        Orden orden = new Orden(mesa, this);
        for (Platillo platillo : platillos) {
            orden.agregarPlatillo(platillo);
        }
        ordenesActivas.add(orden);
        mesasAtendidas++;
        return orden;
    }
    
    public void entregarPedido(Orden orden) {
        if (orden.estaLista()) {
            System.out.println("Pedido entregado a la mesa " + orden.getMesa().getNumero());
            orden.setEntregada(true);
            ordenesActivas.remove(orden);
        } else {
            System.out.println("El pedido aún no está listo para entregar.");
        }
    }
    
    public int getMesasAtendidas() {
        return mesasAtendidas;
    }
}
