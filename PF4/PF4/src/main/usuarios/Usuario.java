package usuarios;

public abstract class Usuario {
    protected static int contadorId = 1;
    protected int id;
    protected String nombre;
    protected String email;
    protected String rol;
    protected String password; // Nueva: contraseña
    
    public Usuario(String nombre, String email, String rol, String password) {
        this.id = contadorId++;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.password = password;
    }
    
    public void mostrarInfo() {
        System.out.println("ID: " + id);
        System.out.println("Nombre: " + nombre);
        System.out.println("Email: " + email);
        System.out.println("Rol: " + rol);
    }
    
    public void actualizarInfo(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }
    
    public boolean verificarPassword(String password) {
    return this.password != null && this.password.equals(password);
}
    
    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
    public String getPassword() { return password; }
    
    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}
