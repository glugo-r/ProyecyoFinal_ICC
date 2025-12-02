package database;

import usuarios.*;
import tareas.Tarea;
import restaurante.Platillo;
import java.io.*;
import java.util.*;

public class DatabaseManager {
    private static final String USUARIOS_FILE = "data/usuarios.csv";
    private static final String TAREAS_FILE = "data/tareas.csv";
    private static final String PLATILLOS_FILE = "data/platillos.csv";
    private static final String VENTAS_FILE = "data/ventas.csv";
    
    public static void guardarUsuarios(List<Usuario> usuarios) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USUARIOS_FILE))) {
            writer.println("id,nombre,email,rol,password");
            for (Usuario usuario : usuarios) {
                writer.println(usuario.getId() + "," + 
                              usuario.getNombre() + "," + 
                              usuario.getEmail() + "," + 
                              usuario.getRol() + "," +
                              usuario.getPassword());
            }
        } catch (IOException e) {
            System.out.println("Error al guardar usuarios: " + e.getMessage());
        }
    }
    
    public static List<Usuario> cargarUsuarios() {
    List<Usuario> usuarios = new ArrayList<>();
    File file = new File(USUARIOS_FILE);
    
    if (!file.exists()) {
        // Crear Sudo por defecto
        Sudo sudo = new Sudo();
        usuarios.add(sudo);
        guardarUsuarios(usuarios);
        System.out.println("Usuario Sudo creado por defecto: email=sudo@restaurante.com, password=admin123");
        return usuarios;
    }
    
    try (BufferedReader reader = new BufferedReader(new FileReader(USUARIOS_FILE))) {
        String line;
        boolean firstLine = true;
        
        while ((line = reader.readLine()) != null) {
            if (firstLine) {
                firstLine = false;
                continue;
            }
            
            String[] datos = line.split(",");
            if (datos.length >= 5) {
                switch (datos[3]) {
                    case "Sudo":
                        Sudo sudo = new Sudo();
                        sudo.setNombre(datos[1]);
                        sudo.setEmail(datos[2]);
                        sudo.setPassword(datos[4]);
                        usuarios.add(sudo);
                        break;
                    case "Administrador":
                        usuarios.add(new Administrador(datos[1], datos[2], datos[4]));
                        break;
                    case "Cocinero":
                        usuarios.add(new Cocinero(datos[1], datos[2], datos[4]));
                        break;
                    case "Mesero":
                        usuarios.add(new Mesero(datos[1], datos[2], datos[4]));
                        break;
                }
            } else if (datos.length >= 4) {
                // Para archivos antiguos sin contraseña
                switch (datos[3]) {
                    case "Sudo":
                        usuarios.add(new Sudo());
                        break;
                    case "Administrador":
                        usuarios.add(new Administrador(datos[1], datos[2], "admin123"));
                        break;
                    case "Cocinero":
                        usuarios.add(new Cocinero(datos[1], datos[2], "admin123"));
                        break;
                    case "Mesero":
                        usuarios.add(new Mesero(datos[1], datos[2], "admin123"));
                        break;
                }
            }
        }
    } catch (IOException e) {
        System.out.println("Error al cargar usuarios: " + e.getMessage());
    }
    
    return usuarios;
}
    
    public static void guardarPlatillos(List<Platillo> platillos) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PLATILLOS_FILE))) {
            writer.println("id,nombre,descripcion,precio,tiempoPreparacion,categoria");
            for (Platillo platillo : platillos) {
                writer.println(platillo.getId() + "," +
                              platillo.getNombre() + "," +
                              platillo.getDescripcion() + "," +
                              platillo.getPrecio() + "," +
                              platillo.getTiempoPreparacion() + "," +
                              platillo.getCategoria());
            }
        } catch (IOException e) {
            System.out.println("Error al guardar platillos: " + e.getMessage());
        }
    }
    
    public static void registrarVenta(double monto, String detalles) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(VENTAS_FILE, true))) {
            writer.println(new Date() + "," + monto + "," + detalles);
        } catch (IOException e) {
            System.out.println("Error al registrar venta: " + e.getMessage());
        }
    }
}
