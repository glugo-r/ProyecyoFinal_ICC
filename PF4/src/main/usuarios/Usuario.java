package usuarios;

public abstract class Usuario {
    protected static int contadorId = 1;
    protected int id;
    protected String nombre;
    protected String email;
    protected String rol;
    
    public Usuario(String nombre, String email, String rol) {
        this.id = contadorId++;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
    }
    
    public void mostrarInfo() {
        System.out.println("ID: " + id);
        System.out.println("Nombre: " + nombre);
        System.out.println("Email: " + email);
        System.out.println("Rol: " + rol);
    }
    
    public void actualizarInfo(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }
    
    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
    
    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
}
