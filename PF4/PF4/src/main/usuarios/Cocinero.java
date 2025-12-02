package usuarios;

import restaurante.Platillo;
import restaurante.Orden;

public class Cocinero extends Empleado {
    private int platillosPreparados;
    
    public Cocinero(String nombre, String email, String password) {
        super(nombre, email, "Cocinero", password);
        this.platillosPreparados = 0;
    }
    
    public void marcarComidaLista(Orden orden, Platillo platillo) {
        orden.marcarPlatilloListo(platillo);
        platillosPreparados++;
        System.out.println("Platillo " + platillo.getNombre() + " marcado como listo.");
    }
    
    public int getPlatillosPreparados() {
        return platillosPreparados;
    }
}
