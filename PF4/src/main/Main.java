import usuarios.*;
import tareas.*;
import restaurante.*;
import excepciones.*;
import database.DatabaseManager;
import notificaciones.NotificadorTareas;
import java.util.*;
import java.text.SimpleDateFormat;

public class Main {
    private static SistemaTareas sistema;
    private static Scanner scanner;
    private static NotificadorTareas notificador;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public static void main(String[] args) {
        sistema = new SistemaTareas();
        scanner = new Scanner(System.in);
        
        // Iniciar hilo de notificaciones
        notificador = new NotificadorTareas(sistema.getTareas());
        notificador.start();
        
        System.out.println("=== SISTEMA DE GESTIÓN DE RESTAURANTE ===");
        mostrarMenuPrincipal();
        
        scanner.close();
        notificador.detener();
    }
    
    private static void mostrarMenuPrincipal() {
        boolean salir = false;
        
        while (!salir) {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Salir");
            System.out.print("Seleccione opción: ");
            
            int opcion = leerEntero();
            
            switch (opcion) {
                case 1:
                    iniciarSesion();
                    break;
                case 2:
                    salir = true;
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }
    
    private static void iniciarSesion() {
        System.out.print("Ingrese email: ");
        String email = scanner.nextLine();
        
        System.out.print("Ingrese nombre: ");
        String nombre = scanner.nextLine();
        
        // Buscar usuario
        Usuario usuario = sistema.getUsuarios().stream()
            .filter(u -> u.getEmail().equals(email) && u.getNombre().equals(nombre))
            .findFirst()
            .orElse(null);
        
        if (usuario != null) {
            sistema.setUsuarioActual(usuario);
            System.out.println("¡Bienvenido, " + usuario.getNombre() + "!");
            mostrarMenuSegunRol();
        } else {
            System.out.println("Usuario no encontrado");
        }
    }
    
    private static void mostrarMenuSegunRol() {
        Usuario usuario = sistema.getUsuarioActual();
        boolean cerrarSesion = false;
        
        while (!cerrarSesion) {
            System.out.println("\n=== MENÚ DE " + usuario.getRol().toUpperCase() + " ===");
            
            if (usuario instanceof Sudo) {
                cerrarSesion = mostrarMenuSudo();
            } else if (usuario instanceof Administrador) {
                cerrarSesion = mostrarMenuAdministrador((Administrador) usuario);
            } else if (usuario instanceof Cocinero) {
                cerrarSesion = mostrarMenuCocinero((Cocinero) usuario);
            } else if (usuario instanceof Mesero) {
                cerrarSesion = mostrarMenuMesero((Mesero) usuario);
            } else {
                System.out.println("Rol no reconocido");
                cerrarSesion = true;
            }
        }
    }
    
    private static boolean mostrarMenuSudo() {
        System.out.println("\n1. Crear nuevo administrador");
        System.out.println("2. Listar todos los usuarios");
        System.out.println("3. Ver ventas del día");
        System.out.println("4. Ver mi información");
        System.out.println("5. Cerrar sesión");
        System.out.print("Seleccione opción: ");
        
        int opcion = leerEntero();
        
        switch (opcion) {
            case 1:
                crearNuevoAdministrador();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false; // Continuar en el menú Sudo
            case 2:
                sistema.listarUsuarios();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 3:
                System.out.println("Ventas del día: $" + sistema.getVentasDia());
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 4:
                sistema.getUsuarioActual().mostrarInfo();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 5:
                sistema.setUsuarioActual(null);
                System.out.println("Sesión cerrada correctamente.");
                return true; // Cerrar sesión
            default:
                System.out.println("Opción inválida");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
        }
    }
    
    private static boolean mostrarMenuAdministrador(Administrador admin) {
        System.out.println("\n1. Crear tarea");
        System.out.println("2. Asignar tarea");
        System.out.println("3. Listar tareas");
        System.out.println("4. Listar empleados");
        System.out.println("5. Agregar nuevo empleado");
        System.out.println("6. Ver mi información");
        System.out.println("7. Cerrar sesión");
        System.out.print("Seleccione opción: ");
        
        int opcion = leerEntero();
        
        switch (opcion) {
            case 1:
                crearTarea(admin);
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 2:
                asignarTarea(admin);
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 3:
                sistema.listarTareas();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 4:
                listarEmpleados();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 5:
                agregarNuevoEmpleado();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 6:
                admin.mostrarInfo();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 7:
                sistema.setUsuarioActual(null);
                System.out.println("Sesión cerrada correctamente.");
                return true;
            default:
                System.out.println("Opción inválida");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
        }
    }
    
    private static boolean mostrarMenuCocinero(Cocinero cocinero) {
        System.out.println("\n1. Ver mis tareas");
        System.out.println("2. Ver órdenes pendientes");
        System.out.println("3. Marcar platillo como listo");
        System.out.println("4. Ver platillos preparados hoy");
        System.out.println("5. Ver mi información");
        System.out.println("6. Cerrar sesión");
        System.out.print("Seleccione opción: ");
        
        int opcion = leerEntero();
        
        switch (opcion) {
            case 1:
                cocinero.consultarTareas();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 2:
                verOrdenesPendientes();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 3:
                marcarPlatilloListo(cocinero);
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 4:
                System.out.println("Platillos preparados: " + cocinero.getPlatillosPreparados());
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 5:
                cocinero.mostrarInfo();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 6:
                sistema.setUsuarioActual(null);
                System.out.println("Sesión cerrada correctamente.");
                return true;
            default:
                System.out.println("Opción inválida");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
        }
    }
    
    private static boolean mostrarMenuMesero(Mesero mesero) {
        System.out.println("\n1. Tomar pedido");
        System.out.println("2. Ver mis órdenes");
        System.out.println("3. Entregar pedido");
        System.out.println("4. Ver mesas disponibles");
        System.out.println("5. Ver mis tareas");
        System.out.println("6. Ver mi información");
        System.out.println("7. Cerrar sesión");
        System.out.print("Seleccione opción: ");
        
        int opcion = leerEntero();
        
        switch (opcion) {
            case 1:
                tomarPedido(mesero);
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 2:
                verOrdenesMesero(mesero);
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 3:
                entregarPedido(mesero);
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 4:
                verMesasDisponibles();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 5:
                mesero.consultarTareas();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 6:
                mesero.mostrarInfo();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 7:
                sistema.setUsuarioActual(null);
                System.out.println("Sesión cerrada correctamente.");
                return true;
            default:
                System.out.println("Opción inválida");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
        }
    }
    
    private static void crearNuevoAdministrador() {
        try {
            System.out.print("Nombre del nuevo administrador: ");
            String nombre = scanner.nextLine();
            
            System.out.print("Email: ");
            String email = scanner.nextLine();
            
            Administrador nuevoAdmin = new Administrador(nombre, email);
            sistema.agregarUsuario(nuevoAdmin);
            System.out.println("Administrador creado exitosamente");
        } catch (EmailInvalidoException | NombreInvalidoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static void crearTarea(Administrador admin) {
        System.out.print("Título de la tarea: ");
        String titulo = scanner.nextLine();
        
        System.out.print("Descripción: ");
        String descripcion = scanner.nextLine();
        
        System.out.print("Fecha límite (yyyy-MM-dd HH:mm): ");
        String fechaLimite = scanner.nextLine();
        
        Tarea tarea = admin.crearTarea(titulo, descripcion, fechaLimite);
        sistema.agregarTarea(tarea);
        System.out.println("Tarea creada exitosamente");
    }
    
    private static void asignarTarea(Administrador admin) {
        System.out.println("Tareas disponibles:");
        sistema.listarTareas();
        
        System.out.print("ID de la tarea a asignar: ");
        int idTarea = leerEntero();
        
        Tarea tarea = sistema.buscarTareaPorId(idTarea);
        if (tarea == null) {
            System.out.println("Tarea no encontrada");
            return;
        }
        
        listarEmpleados();
        System.out.print("ID del empleado a asignar: ");
        int idEmpleado = leerEntero();
        
        Empleado empleado = sistema.getEmpleados().stream()
            .filter(e -> e.getId() == idEmpleado)
            .findFirst()
            .orElse(null);
        
        if (empleado != null) {
            admin.asignarTarea(tarea, empleado);
            System.out.println("Tarea asignada exitosamente");
        } else {
            System.out.println("Empleado no encontrado");
        }
    }
    
    private static void listarEmpleados() {
        List<Empleado> empleados = sistema.getEmpleados();
        if (empleados.isEmpty()) {
            System.out.println("No hay empleados registrados");
        } else {
            for (Empleado emp : empleados) {
                emp.mostrarInfo();
                System.out.println("-------------------");
            }
        }
    }
    
    private static void agregarNuevoEmpleado() {
        System.out.println("Tipo de empleado:");
        System.out.println("1. Cocinero");
        System.out.println("2. Mesero");
        System.out.print("Seleccione: ");
        
        int tipo = leerEntero();
        
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        try {
            if (tipo == 1) {
                sistema.agregarUsuario(new Cocinero(nombre, email));
                System.out.println("Cocinero agregado exitosamente");
            } else if (tipo == 2) {
                sistema.agregarUsuario(new Mesero(nombre, email));
                System.out.println("Mesero agregado exitosamente");
            } else {
                System.out.println("Opción inválida");
            }
        } catch (EmailInvalidoException | NombreInvalidoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static void tomarPedido(Mesero mesero) {
        verMesasDisponibles();
        System.out.print("Número de mesa: ");
        int numeroMesa = leerEntero();
        
        Mesa mesa = sistema.getMesas().stream()
            .filter(m -> m.getNumero() == numeroMesa && !m.isOcupada())
            .findFirst()
            .orElse(null);
        
        if (mesa == null) {
            System.out.println("Mesa no disponible");
            return;
        }
        
        mesa.setOcupada(true);
        System.out.println("\nPlatillos disponibles:");
        for (Platillo platillo : sistema.getPlatillos()) {
            platillo.mostrarInfo();
            System.out.println("-------------------");
        }
        
        List<Platillo> platillosPedido = new ArrayList<>();
        boolean agregando = true;
        
        while (agregando) {
            System.out.print("ID del platillo (0 para terminar): ");
            int idPlatillo = leerEntero();
            
            if (idPlatillo == 0) {
                agregando = false;
            } else {
                Platillo platillo = sistema.getPlatillos().stream()
                    .filter(p -> p.getId() == idPlatillo)
                    .findFirst()
                    .orElse(null);
                
                if (platillo != null) {
                    platillosPedido.add(platillo);
                    System.out.println("Platillo agregado: " + platillo.getNombre());
                } else {
                    System.out.println("Platillo no encontrado");
                }
            }
        }
        
        if (!platillosPedido.isEmpty()) {
            Orden orden = mesero.tomarPedido(mesa, platillosPedido);
            sistema.getOrdenes().add(orden);
            System.out.println("Pedido registrado. Orden #" + orden.getId());
            sistema.agregarVenta(orden.getTotal());
        }
    }
    
    private static void verOrdenesPendientes() {
        List<Orden> ordenesPendientes = sistema.getOrdenes().stream()
            .filter(o -> !o.isEntregada())
            .collect(java.util.stream.Collectors.toList());
        
        if (ordenesPendientes.isEmpty()) {
            System.out.println("No hay órdenes pendientes");
        } else {
            for (Orden orden : ordenesPendientes) {
                orden.mostrarOrden();
                System.out.println("===================");
            }
        }
    }
    
    private static void marcarPlatilloListo(Cocinero cocinero) {
        verOrdenesPendientes();
        System.out.print("ID de la orden: ");
        int idOrden = leerEntero();
        
        Orden orden = sistema.getOrdenes().stream()
            .filter(o -> o.getId() == idOrden)
            .findFirst()
            .orElse(null);
        
        if (orden == null) {
            System.out.println("Orden no encontrada");
            return;
        }
        
        System.out.print("ID del platillo a marcar como listo: ");
        int idPlatillo = leerEntero();
        
        Platillo platillo = sistema.getPlatillos().stream()
            .filter(p -> p.getId() == idPlatillo)
            .findFirst()
            .orElse(null);
        
        if (platillo != null) {
            cocinero.marcarComidaLista(orden, platillo);
        } else {
            System.out.println("Platillo no encontrado");
        }
    }
    
    private static void verOrdenesMesero(Mesero mesero) {
        List<Orden> ordenesMesero = sistema.getOrdenes().stream()
            .filter(o -> o.getMesero().getId() == mesero.getId())
            .collect(java.util.stream.Collectors.toList());
        
        if (ordenesMesero.isEmpty()) {
            System.out.println("No tienes órdenes activas");
        } else {
            for (Orden orden : ordenesMesero) {
                orden.mostrarOrden();
                System.out.println("===================");
            }
        }
    }
    
    private static void entregarPedido(Mesero mesero) {
        verOrdenesMesero(mesero);
        System.out.print("ID de la orden a entregar: ");
        int idOrden = leerEntero();
        
        Orden orden = sistema.getOrdenes().stream()
            .filter(o -> o.getId() == idOrden && o.getMesero().getId() == mesero.getId())
            .findFirst()
            .orElse(null);
        
        if (orden != null) {
            mesero.entregarPedido(orden);
            orden.getMesa().setOcupada(false);
        } else {
            System.out.println("Orden no encontrada o no pertenece a este mesero");
        }
    }
    
    private static void verMesasDisponibles() {
        System.out.println("Mesas disponibles:");
        for (Mesa mesa : sistema.getMesas()) {
            if (!mesa.isOcupada()) {
                System.out.println("Mesa #" + mesa.getNumero() + " (Capacidad: " + mesa.getCapacidad() + ")");
            }
        }
    }
    
    private static int leerEntero() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Ingrese un número válido: ");
            }
        }
    }
}
