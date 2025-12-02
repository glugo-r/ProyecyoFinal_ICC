package usuarios;

public class Sudo extends Usuario {
    
    public Sudo() {
        super("SuperAdmin", "sudo@restaurante.com", "Sudo");
    }
    
    public Administrador crearAdministrador(String nombre, String email) {
        return new Administrador(nombre, email);
    }
}
