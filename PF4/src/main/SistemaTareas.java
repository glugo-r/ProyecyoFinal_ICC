import usuarios.*;
import tareas.*;
import restaurante.*;
import excepciones.*;
import database.DatabaseManager;
import java.util.*;
import java.util.stream.Collectors;

public class SistemaTareas {
    private List<Usuario> usuarios;
    private List<Tarea> tareas;
    private List<Platillo> platillos;
    private List<Mesa> mesas;
    private List<Orden> ordenes;
    private Usuario usuarioActual;
    private double ventasDia;
    
    public SistemaTareas() {
    this.usuarios = DatabaseManager.cargarUsuarios();
    this.tareas = new ArrayList<>();
    this.platillos = new ArrayList<>();
    this.mesas = new ArrayList<>();
    this.ordenes = new ArrayList<>();
    this.ventasDia = 0.0;
    
    // Verificar si existe el Sudo
    boolean sudoExiste = usuarios.stream()
        .anyMatch(u -> u instanceof Sudo);
    
    if (!sudoExiste) {
        System.out.println("Creando usuario Sudo por defecto...");
        Sudo sudo = new Sudo();
        usuarios.add(sudo);
        try {
            DatabaseManager.guardarUsuarios(usuarios);
            System.out.println("Sudo creado: email=sudo@restaurante.com, password=admin123");
        } catch (Exception e) {
            System.out.println("Error al crear Sudo: " + e.getMessage());
        }
    }
    
    inicializarDatos();
}
    
    private void inicializarDatos() {
        // Inicializar mesas
        for (int i = 1; i <= 10; i++) {
            mesas.add(new Mesa(i, 4));
        }
        
        // Inicializar platillos predeterminados
        platillos.add(new Platillo("Hamburguesa Clásica", "Carne, lechuga, tomate, queso", 12.99, 15, "Principal"));
        platillos.add(new Platillo("Ensalada César", "Lechuga, pollo, croutones, aderezo", 9.99, 10, "Entrada"));
        platillos.add(new Platillo("Pasta Alfredo", "Pasta con salsa de crema y pollo", 14.99, 20, "Principal"));
        platillos.add(new Platillo("Sopa del día", "Sopa casera", 6.99, 5, "Entrada"));
        platillos.add(new Platillo("Postre de chocolate", "Pastel de chocolate con helado", 7.99, 8, "Postre"));
        
        DatabaseManager.guardarPlatillos(platillos);
    }
    
    public Usuario autenticarUsuario(String email, String password) {
        return usuarios.stream()
            .filter(u -> u.getEmail().equals(email) && u.verificarPassword(password))
            .findFirst()
            .orElse(null);
    }
    
    public void agregarUsuario(Usuario usuario) throws EmailInvalidoException, NombreInvalidoException {
        validarEmail(usuario.getEmail());
        validarNombre(usuario.getNombre());
        usuarios.add(usuario);
        DatabaseManager.guardarUsuarios(usuarios);
    }
    
    public void eliminarUsuario(int id) {
        if (id == 1) { // No se puede eliminar al Sudo
            System.out.println("No se puede eliminar al super administrador.");
            return;
        }
        usuarios.removeIf(u -> u.getId() == id);
        DatabaseManager.guardarUsuarios(usuarios);
    }
    
    public void agregarTarea(Tarea tarea) {
        tareas.add(tarea);
    }
    
    public void asignarTarea(Tarea tarea, Empleado empleado) {
        tarea.setUsuarioAsignado(empleado);
        empleado.agregarTarea(tarea);
    }
    
    public void listarUsuarios() {
    listarUsuarios(false); // Por defecto no mostrar contraseñas
}

public void listarUsuarios(boolean mostrarPasswords) {
    System.out.println("\n=== LISTA DE USUARIOS ===");
    System.out.println("Total usuarios: " + usuarios.size());
    System.out.println("-------------------");
    
    for (Usuario usuario : usuarios) {
        usuario.mostrarInfo(mostrarPasswords);
        System.out.println("-------------------");
    }
}
    
    public void listarTareas() {
        for (Tarea tarea : tareas) {
            tarea.mostrarDetalles();
            System.out.println("-------------------");
        }
    }
    
    public List<Empleado> getEmpleados() {
        return usuarios.stream()
            .filter(u -> u instanceof Empleado)
            .map(u -> (Empleado) u)
            .collect(Collectors.toList());
    }
    
    public List<Administrador> getAdministradores() {
        return usuarios.stream()
            .filter(u -> u instanceof Administrador)
            .map(u -> (Administrador) u)
            .collect(Collectors.toList());
    }
    
    public Usuario buscarUsuarioPorId(int id) {
        return usuarios.stream()
            .filter(u -> u.getId() == id)
            .findFirst()
            .orElse(null);
    }
    
    public Tarea buscarTareaPorId(int id) {
        return tareas.stream()
            .filter(t -> t.getId() == id)
            .findFirst()
            .orElse(null);
    }
    
    private void validarEmail(String email) throws EmailInvalidoException {
        if (!email.contains("@") || !email.contains(".")) {
            throw new EmailInvalidoException("El email debe contener @ y .");
        }
    }
    
    private void validarNombre(String nombre) throws NombreInvalidoException {
        if (nombre == null || nombre.trim().length() < 3) {
            throw new NombreInvalidoException("El nombre debe tener al menos 3 letras");
        }
    }
    
    // Getters y Setters
    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Tarea> getTareas() { return tareas; }
    public List<Platillo> getPlatillos() { return platillos; }
    public List<Mesa> getMesas() { return mesas; }
    public List<Orden> getOrdenes() { return ordenes; }
    public Usuario getUsuarioActual() { return usuarioActual; }
    public void setUsuarioActual(Usuario usuarioActual) { this.usuarioActual = usuarioActual; }
    public double getVentasDia() { return ventasDia; }
    public void agregarVenta(double monto) { 
        ventasDia += monto; 
        DatabaseManager.registrarVenta(monto, "Venta registrada");
    }
}
