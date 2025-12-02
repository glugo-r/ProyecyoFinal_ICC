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
        
        System.out.print("Ingrese contraseña: ");
        String password = scanner.nextLine();
        
        // Autenticar usuario
        Usuario usuario = sistema.autenticarUsuario(email, password);
        
        if (usuario != null) {
            sistema.setUsuarioActual(usuario);
            System.out.println("¡Bienvenido, " + usuario.getNombre() + "!");
            mostrarMenuSegunRol();
        } else {
            System.out.println("Credenciales incorrectas. Intente nuevamente.");
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
    System.out.println("\n=== MENÚ SUDO ===");
    System.out.println("1. Crear nuevo usuario");
    System.out.println("2. Listar todos los usuarios");
    System.out.println("3. Ver ventas del día");
    System.out.println("4. Ver/eliminar tareas");
    System.out.println("5. Ver mi información");
    System.out.println("6. Cambiar mi contraseña");
    System.out.println("7. Eliminar usuario");
    System.out.println("8. Cerrar sesión");
    System.out.print("Seleccione opción: ");
    
    int opcion = leerEntero();
    
    switch (opcion) {
        case 1:
            crearNuevoUsuario();
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 2:
            System.out.println("\n¿Mostrar contraseñas? (s/n): ");
            String respuesta = scanner.nextLine().toLowerCase();
            boolean mostrarPasswords = respuesta.equals("s") || respuesta.equals("si");
            sistema.listarUsuarios(mostrarPasswords);
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 3:
            System.out.println("Ventas del día: $" + sistema.getVentasDia());
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 4:
            gestionarTareasSudo();
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 5:
            sistema.getUsuarioActual().mostrarInfo(true);
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 6:
            cambiarPasswordSudo();
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 7:
            eliminarUsuarioSudo();
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 8:
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

private static void gestionarTareasSudo() {
    System.out.println("\n=== GESTIÓN DE TAREAS ===");
    System.out.println("1. Listar todas las tareas");
    System.out.println("2. Eliminar tarea");
    System.out.print("Seleccione opción: ");
    
    int opcion = leerEntero();
    
    switch (opcion) {
        case 1:
            sistema.listarTareas();
            break;
        case 2:
            eliminarTarea();
            break;
        default:
            System.out.println("Opción inválida");
    }
}
    
    private static void cambiarPasswordSudo() {
        System.out.print("Ingrese nueva contraseña: ");
        String nuevaPassword = scanner.nextLine();
        
        System.out.print("Confirme nueva contraseña: ");
        String confirmPassword = scanner.nextLine();
        
        if (nuevaPassword.equals(confirmPassword)) {
            if (nuevaPassword.length() < 4) {
                System.out.println("La contraseña debe tener al menos 4 caracteres.");
                return;
            }
            
            sistema.getUsuarioActual().setPassword(nuevaPassword);
            DatabaseManager.guardarUsuarios(sistema.getUsuarios());
            System.out.println("Contraseña cambiada exitosamente.");
        } else {
            System.out.println("Las contraseñas no coinciden.");
        }
    }
    
    private static void eliminarUsuarioSudo() {
    System.out.println("\n=== ELIMINAR USUARIO ===");
    sistema.listarUsuarios(false);
    
    System.out.print("\nID del usuario a eliminar (0 para cancelar): ");
    int idUsuario = leerEntero();
    
    if (idUsuario == 0) {
        System.out.println("Operación cancelada.");
        return;
    }
    
    if (idUsuario == 1) {
        System.out.println("❌ No se puede eliminar al usuario Sudo (ID: 1).");
        return;
    }
    
    Usuario usuario = sistema.buscarUsuarioPorId(idUsuario);
    if (usuario == null) {
        System.out.println("❌ Usuario no encontrado.");
        return;
    }
    
    System.out.println("Usuario a eliminar:");
    usuario.mostrarInfo(false);
    
    System.out.print("\n¿Está seguro de eliminar este usuario? (s/n): ");
    String confirmacion = scanner.nextLine().toLowerCase();
    
    if (confirmacion.equals("s") || confirmacion.equals("si")) {
        sistema.eliminarUsuario(idUsuario);
        System.out.println("✅ Usuario eliminado exitosamente.");
    } else {
        System.out.println("Operación cancelada.");
    }
}
    
    private static boolean mostrarMenuAdministrador(Administrador admin) {
    System.out.println("\n=== MENÚ ADMINISTRADOR ===");
    System.out.println("1. Crear tarea");
    System.out.println("2. Asignar tarea");
    System.out.println("3. Listar tareas");
    System.out.println("4. Eliminar tarea");
    System.out.println("5. Listar empleados");
    System.out.println("6. Agregar nuevo empleado");
    System.out.println("7. Eliminar empleado");
    System.out.println("8. Ver mi información");
    System.out.println("9. Cerrar sesión");
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
            eliminarTarea();
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 5:
            listarEmpleados();
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 6:
            agregarNuevoEmpleado();
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 7:
            eliminarEmpleadoAdministrador(admin);
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 8:
            admin.mostrarInfo(false);
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 9:
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

    private static void eliminarEmpleadoAdministrador(Administrador admin) {
    System.out.println("\n=== ELIMINAR EMPLEADO ===");
    
    // Listar solo empleados (no administradores)
    List<Empleado> empleados = sistema.getEmpleados();
    
    if (empleados.isEmpty()) {
        System.out.println("No hay empleados registrados.");
        return;
    }
    
    System.out.println("Lista de empleados:");
    for (Empleado empleado : empleados) {
        System.out.println("ID: " + empleado.getId() + 
                         " | Nombre: " + empleado.getNombre() + 
                         " | Rol: " + empleado.getRol());
    }
    
    System.out.print("\nID del empleado a eliminar (0 para cancelar): ");
    int idEmpleado = leerEntero();
    
    if (idEmpleado == 0) {
        System.out.println("Operación cancelada.");
        return;
    }
    
    // Verificar que sea un empleado (no administrador o sudo)
    Usuario usuario = sistema.buscarUsuarioPorId(idEmpleado);
    
    if (usuario == null) {
        System.out.println("❌ Usuario no encontrado.");
        return;
    }
    
    // Verificar que no sea administrador o sudo
    if (usuario instanceof Administrador || usuario instanceof Sudo) {
        System.out.println("❌ No tiene permisos para eliminar administradores.");
        System.out.println("   Solo el Sudo puede eliminar administradores.");
        return;
    }
    
    if (!(usuario instanceof Empleado)) {
        System.out.println("❌ El usuario seleccionado no es un empleado.");
        return;
    }
    
    System.out.println("Empleado a eliminar:");
    usuario.mostrarInfo(false);
    
    System.out.print("\n¿Está seguro de eliminar este empleado? (s/n): ");
    String confirmacion = scanner.nextLine().toLowerCase();
    
    if (confirmacion.equals("s") || confirmacion.equals("si")) {
        sistema.eliminarUsuario(idEmpleado);
        System.out.println("✅ Empleado eliminado exitosamente.");
    } else {
        System.out.println("Operación cancelada.");
    }
}
    
    private static boolean mostrarMenuCocinero(Cocinero cocinero) {
        System.out.println("\n=== MENÚ COCINERO ===");
        System.out.println("Bienvenido, " + cocinero.getNombre());
        System.out.println("Platillos preparados hoy: " + cocinero.getPlatillosPreparados());
        System.out.println("\n1. Ver mis tareas asignadas");
        System.out.println("2. Ver órdenes pendientes (resumen)");
        System.out.println("3. Ver detalles de órdenes pendientes");
        System.out.println("4. Marcar platillo como listo");
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
                verResumenOrdenesPendientes();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 3:
                verOrdenesPendientes();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 4:
                marcarPlatilloListo(cocinero);
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 5:
                cocinero.mostrarInfo(false);
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
                mesero.mostrarInfo(false);
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
    
    private static void crearNuevoUsuario() {
    System.out.println("\n=== CREAR NUEVO USUARIO ===");
    System.out.println("Tipo de usuario:");
    System.out.println("1. Administrador");
    System.out.println("2. Cocinero");
    System.out.println("3. Mesero");
    System.out.print("Seleccione tipo: ");
    
    int tipo = leerEntero();
    
    System.out.print("Nombre: ");
    String nombre = scanner.nextLine();
    
    System.out.print("Email (debe terminar en .com): ");
    String email = scanner.nextLine();
    
    System.out.print("Contraseña: ");
    String password = scanner.nextLine();
    
    try {
        switch (tipo) {
            case 1:
                sistema.agregarUsuario(new Administrador(nombre, email, password));
                System.out.println("✅ Administrador creado exitosamente");
                break;
            case 2:
                sistema.agregarUsuario(new Cocinero(nombre, email, password));
                System.out.println("✅ Cocinero creado exitosamente");
                break;
            case 3:
                sistema.agregarUsuario(new Mesero(nombre, email, password));
                System.out.println("✅ Mesero creado exitosamente");
                break;
            default:
                System.out.println("❌ Tipo de usuario inválido");
                return;
        }
    } catch (EmailInvalidoException | NombreInvalidoException e) {
        System.out.println("❌ Error: " + e.getMessage());
    }
}
    
    private static void crearTarea(Administrador admin) {
    System.out.print("Título de la tarea: ");
    String titulo = scanner.nextLine();
    
    System.out.print("Descripción: ");
    String descripcion = scanner.nextLine();
    
    // Solicitar fecha con formato específico
    System.out.print("Fecha límite (Formato: yyyy-MM-dd HH:mm, ejemplo: 2024-12-31 18:30): ");
    String fechaLimite = scanner.nextLine();
    
    // Validar formato de fecha
    if (!validarFormatoFecha(fechaLimite)) {
        System.out.println("❌ Error: El formato de fecha debe ser yyyy-MM-dd HH:mm");
        System.out.println("   Ejemplo: 2024-12-31 18:30");
        return;
    }
    
    Tarea tarea = admin.crearTarea(titulo, descripcion, fechaLimite);
    sistema.agregarTarea(tarea);
    System.out.println("✅ Tarea creada exitosamente");
}

// Método para validar el formato de fecha
private static boolean validarFormatoFecha(String fecha) {
    try {
        // Intentar parsear la fecha
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setLenient(false); // No permitir fechas inválidas como 2024-02-30
        Date fechaParseada = sdf.parse(fecha);
        return true;
    } catch (Exception e) {
        return false;
    }
}

    private static void eliminarTarea() {
    System.out.println("\n=== ELIMINAR TAREA ===");
    
    if (sistema.getTareas().isEmpty()) {
        System.out.println("No hay tareas registradas.");
        return;
    }
    
    System.out.println("Lista de tareas:");
    sistema.listarTareas();
    
    System.out.print("\nID de la tarea a eliminar (0 para cancelar): ");
    int idTarea = leerEntero();
    
    if (idTarea == 0) {
        System.out.println("Operación cancelada.");
        return;
    }
    
    Tarea tarea = sistema.buscarTareaPorId(idTarea);
    if (tarea == null) {
        System.out.println("❌ Tarea no encontrada.");
        return;
    }
    
    System.out.println("Tarea a eliminar:");
    tarea.mostrarDetalles();
    
    System.out.print("\n¿Está seguro de eliminar esta tarea? (s/n): ");
    String confirmacion = scanner.nextLine().toLowerCase();
    
    if (confirmacion.equals("s") || confirmacion.equals("si")) {
        // Remover tarea de cualquier empleado asignado
        if (tarea.getUsuarioAsignado() != null) {
            tarea.getUsuarioAsignado().removerTarea(tarea);
        }
        
        // Eliminar tarea del sistema
        sistema.getTareas().removeIf(t -> t.getId() == idTarea);
        System.out.println("✅ Tarea eliminada exitosamente.");
    } else {
        System.out.println("Operación cancelada.");
    }
}
    
    private static void asignarTarea(Administrador admin) {
    System.out.println("\n=== ASIGNAR TAREA ===");
    
    if (sistema.getTareas().isEmpty()) {
        System.out.println("No hay tareas disponibles para asignar.");
        System.out.println("Primero debe crear tareas.");
        return;
    }
    
    System.out.println("Tareas disponibles:");
    sistema.listarTareas();
    
    System.out.print("\nID de la tarea a asignar (0 para cancelar): ");
    int idTarea = leerEntero();
    
    if (idTarea == 0) {
        System.out.println("Operación cancelada.");
        return;
    }
    
    Tarea tarea = sistema.buscarTareaPorId(idTarea);
    if (tarea == null) {
        System.out.println("❌ Tarea no encontrada");
        return;
    }
    
    // Mostrar empleados disponibles
    List<Empleado> empleados = sistema.getEmpleados();
    if (empleados.isEmpty()) {
        System.out.println("❌ No hay empleados registrados para asignar tareas.");
        return;
    }
    
    System.out.println("\nEmpleados disponibles:");
    for (Empleado emp : empleados) {
        System.out.println("ID: " + emp.getId() + 
                         " | Nombre: " + emp.getNombre() + 
                         " | Rol: " + emp.getRol() +
                         " | Tareas asignadas: " + emp.getTareasAsignadas().size());
    }
    
    System.out.print("\nID del empleado a asignar (0 para cancelar): ");
    int idEmpleado = leerEntero();
    
    if (idEmpleado == 0) {
        System.out.println("Operación cancelada.");
        return;
    }
    
    Empleado empleado = sistema.getEmpleados().stream()
        .filter(e -> e.getId() == idEmpleado)
        .findFirst()
        .orElse(null);
    
    if (empleado != null) {
        // Si la tarea ya tenía asignado a alguien, removerla
        if (tarea.getUsuarioAsignado() != null) {
            tarea.getUsuarioAsignado().removerTarea(tarea);
            System.out.println("⚠️  Tarea reasignada. Anterior asignado: " + 
                             tarea.getUsuarioAsignado().getNombre());
        }
        
        admin.asignarTarea(tarea, empleado);
        System.out.println("✅ Tarea '" + tarea.getTitulo() + 
                         "' asignada exitosamente a " + empleado.getNombre());
    } else {
        System.out.println("❌ Empleado no encontrado");
    }
}
    
    private static void listarEmpleados() {
        List<Empleado> empleados = sistema.getEmpleados();
        if (empleados.isEmpty()) {
            System.out.println("No hay empleados registrados");
        } else {
            for (Empleado emp : empleados) {
                emp.mostrarInfo(false);
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
    
    System.out.print("Email (debe terminar en .com): ");
    String email = scanner.nextLine();
    
    System.out.print("Contraseña: ");
    String password = scanner.nextLine();
    
    try {
        if (tipo == 1) {
            sistema.agregarUsuario(new Cocinero(nombre, email, password));
            System.out.println("✅ Cocinero agregado exitosamente");
        } else if (tipo == 2) {
            sistema.agregarUsuario(new Mesero(nombre, email, password));
            System.out.println("✅ Mesero agregado exitosamente");
        } else {
            System.out.println("❌ Opción inválida");
        }
    } catch (EmailInvalidoException | NombreInvalidoException e) {
        System.out.println("❌ Error: " + e.getMessage());
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
            System.out.println("[ID: " + platillo.getId() + "] " + 
                             platillo.getNombre() + 
                             " - $" + platillo.getPrecio() +
                             " (" + platillo.getTiempoPreparacion() + " min)");
            System.out.println("  Descripción: " + platillo.getDescripcion());
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
    
    private static void verResumenOrdenesPendientes() {
        List<Orden> ordenesPendientes = sistema.getOrdenes().stream()
            .filter(o -> !o.isEntregada())
            .collect(java.util.stream.Collectors.toList());
        
        if (ordenesPendientes.isEmpty()) {
            System.out.println("✅ No hay órdenes pendientes.");
            return;
        }
        
        System.out.println("\n=== RESUMEN DE ÓRDENES PENDIENTES ===");
        System.out.println("Total órdenes: " + ordenesPendientes.size());
        
        for (Orden orden : ordenesPendientes) {
            int totalPlatillos = orden.getPlatillos().size();
            int listos = orden.getPlatillosListos().size();
            int pendientes = totalPlatillos - listos;
            
            String estado = orden.estaLista() ? "✅ LISTA" : "🔄 EN PROCESO";
            
            System.out.println("\nOrden #" + orden.getId() + 
                             " | Mesa: " + orden.getMesa().getNumero() +
                             " | " + estado +
                             " | Platillos: " + listos + "/" + totalPlatillos);
            
            if (pendientes > 0) {
                System.out.print("  Pendientes: ");
                for (Platillo p : orden.getPlatillosPendientes()) {
                    System.out.print("[ID:" + p.getId() + "] ");
                }
                System.out.println();
            }
        }
    }
    
    private static void verOrdenesMesero(Mesero mesero) {
        List<Orden> ordenesMesero = sistema.getOrdenes().stream()
            .filter(o -> o.getMesero().getId() == mesero.getId() && !o.isEntregada())
            .collect(java.util.stream.Collectors.toList());
        
        if (ordenesMesero.isEmpty()) {
            System.out.println("No tienes órdenes activas");
        } else {
            System.out.println("\n=== MIS ÓRDENES ACTIVAS ===");
            System.out.println("Total órdenes: " + ordenesMesero.size());
            
            for (Orden orden : ordenesMesero) {
                System.out.println("\n[Orden #" + orden.getId() + "]");
                System.out.println("Mesa: " + orden.getMesa().getNumero());
                
                // Mostrar estado de la orden
                if (orden.estaLista()) {
                    System.out.println("Estado: ✅ LISTA PARA ENTREGAR");
                } else {
                    int total = orden.getPlatillos().size();
                    int listos = orden.getPlatillosListos().size();
                    System.out.println("Estado: 🔄 EN PREPARACIÓN (" + listos + "/" + total + " listos)");
                }
                
                // Mostrar platillos con su estado
                System.out.println("Platillos:");
                for (Platillo platillo : orden.getPlatillos()) {
                    boolean listo = orden.getPlatillosListos().contains(platillo);
                    System.out.println("  - " + platillo.getNombre() + 
                                     " $" + platillo.getPrecio() +
                                     " [" + (listo ? "✅ LISTO" : "⏳ PREPARANDO") + "]");
                }
                
                System.out.println("Total: $" + orden.getTotal());
                System.out.println("-------------------");
            }
        }
    }
    
    private static void marcarPlatilloListo(Cocinero cocinero) {
        // Mostrar órdenes pendientes
        List<Orden> ordenesPendientes = sistema.getOrdenes().stream()
            .filter(o -> !o.isEntregada())
            .collect(java.util.stream.Collectors.toList());
        
        if (ordenesPendientes.isEmpty()) {
            System.out.println("No hay órdenes pendientes para preparar.");
            return;
        }
        
        System.out.println("\n=== ÓRDENES PENDIENTES ===");
        for (Orden orden : ordenesPendientes) {
            System.out.println("\n[Orden #" + orden.getId() + "]");
            System.out.println("Mesa: " + orden.getMesa().getNumero());
            System.out.println("Mesero: " + orden.getMesero().getNombre());
            System.out.println("Platillos: " + orden.getPlatillos().size() + 
                             " | Listos: " + orden.getPlatillosListos().size() +
                             " | Pendientes: " + orden.getPlatillosPendientes().size());
            System.out.println("-------------------");
        }
        
        System.out.print("\nID de la orden a trabajar: ");
        int idOrden = leerEntero();
        
        Orden orden = sistema.getOrdenes().stream()
            .filter(o -> o.getId() == idOrden && !o.isEntregada())
            .findFirst()
            .orElse(null);
        
        if (orden == null) {
            System.out.println("Orden no encontrada o ya entregada.");
            return;
        }
        
        // Mostrar platillos específicos de esta orden
        System.out.println("\n=== PLATILLOS DE LA ORDEN #" + orden.getId() + " ===");
        
        List<Platillo> platillosPendientes = orden.getPlatillosPendientes();
        List<Platillo> platillosListos = orden.getPlatillosListos();
        
        if (platillosPendientes.isEmpty()) {
            System.out.println("¡Todos los platillos de esta orden ya están listos!");
            System.out.println("Estado: ✅ LISTA PARA ENTREGAR");
            return;
        }
        
        System.out.println("Platillos PENDIENTES de preparar:");
        for (Platillo platillo : platillosPendientes) {
            System.out.println("  [ID: " + platillo.getId() + "] " + 
                             platillo.getNombre() + 
                             " | Tiempo: " + platillo.getTiempoPreparacion() + " min" +
                             " | $" + platillo.getPrecio());
        }
        
        if (!platillosListos.isEmpty()) {
            System.out.println("\nPlatillos YA LISTOS:");
            for (Platillo platillo : platillosListos) {
                System.out.println("  [ID: " + platillo.getId() + "] " + 
                                 platillo.getNombre() + " ✅");
            }
        }
        
        System.out.print("\nID del platillo a marcar como listo (0 para cancelar): ");
        int idPlatillo = leerEntero();
        
        if (idPlatillo == 0) {
            System.out.println("Operación cancelada.");
            return;
        }
        
        // Buscar el platillo en los pendientes de esta orden
        Platillo platilloSeleccionado = null;
        for (Platillo platillo : platillosPendientes) {
            if (platillo.getId() == idPlatillo) {
                platilloSeleccionado = platillo;
                break;
            }
        }
        
        if (platilloSeleccionado == null) {
            // Verificar si el platillo ya está listo
            boolean yaListo = false;
            for (Platillo platillo : platillosListos) {
                if (platillo.getId() == idPlatillo) {
                    yaListo = true;
                    break;
                }
            }
            
            if (yaListo) {
                System.out.println("Este platillo ya está marcado como listo.");
            } else {
                System.out.println("Error: El platillo con ID " + idPlatillo + 
                                 " no está en esta orden o no existe.");
            }
            return;
        }
        
        // Marcar el platillo como listo
        cocinero.marcarComidaLista(orden, platilloSeleccionado);
        System.out.println("✅ Platillo '" + platilloSeleccionado.getNombre() + 
                         "' marcado como LISTO.");
        
        // Verificar si todos los platillos están listos
        if (orden.estaLista()) {
            System.out.println("\n🎉 ¡¡¡TODOS LOS PLATILLOS DE LA ORDEN #" + 
                             orden.getId() + " ESTÁN LISTOS!!!");
            System.out.println("📢 Informar al mesero que la orden está lista para entregar.");
            System.out.println("Mesa: " + orden.getMesa().getNumero());
            System.out.println("Mesero asignado: " + orden.getMesero().getNombre());
        } else {
            int pendientes = orden.getPlatillosPendientes().size();
            int total = orden.getPlatillos().size();
            System.out.println("🔄 Progreso: " + (total - pendientes) + "/" + 
                             total + " platillos listos");
            System.out.println("⏳ Aún faltan " + pendientes + " platillo(s) por preparar.");
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
            if (!orden.estaLista()) {
                System.out.println("⚠️  Esta orden no está completamente lista.");
                System.out.println("Platillos pendientes: " + 
                                 orden.getPlatillosPendientes().size() + "/" + 
                                 orden.getPlatillos().size());
                System.out.print("¿Desea continuar con la entrega? (s/n): ");
                String respuesta = scanner.nextLine().toLowerCase();
                if (!respuesta.equals("s") && !respuesta.equals("si")) {
                    System.out.println("Entrega cancelada.");
                    return;
                }
            }
            
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
