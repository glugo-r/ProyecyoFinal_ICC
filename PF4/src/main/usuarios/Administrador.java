package usuarios;

import java.util.List;
import tareas.Tarea;

public class Administrador extends Usuario {
    
    public Administrador(String nombre, String email) {
        super(nombre, email, "Administrador");
    }
    
    public Tarea crearTarea(String titulo, String descripcion, String fechaLimite) {
        return new Tarea(titulo, descripcion, fechaLimite);
    }
    
    public void asignarTarea(Tarea tarea, Empleado empleado) {
        tarea.setUsuarioAsignado(empleado);
        empleado.agregarTarea(tarea);
    }
    
    public void listarUsuarios(List<Usuario> usuarios) {
        for (Usuario usuario : usuarios) {
            usuario.mostrarInfo();
            System.out.println("-------------------");
        }
    }
    
    public void listarTareas(List<Tarea> tareas) {
        for (Tarea tarea : tareas) {
            tarea.mostrarDetalles();
            System.out.println("-------------------");
        }
    }
}
