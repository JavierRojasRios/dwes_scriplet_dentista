package es.dwes.dentista.model;

public class PacienteOpcion {
    private final int id;
    private final String nombre;
    private final String apellidos;

    public PacienteOpcion(int id, String nombre, String apellidos) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
}
