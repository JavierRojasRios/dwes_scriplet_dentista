package es.dwes.dentista.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cita {
    private final int id;
    private final LocalDate fecha;
    private final LocalTime hora;
    private final String paciente;
    private final String motivo;
    private final String odontologo;

    public Cita(int id, LocalDate fecha, LocalTime hora, String paciente, String motivo, String odontologo) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.paciente = paciente;
        this.motivo = motivo;
        this.odontologo = odontologo;
    }

    public int getId() { return id; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }
    public String getPaciente() { return paciente; }
    public String getMotivo() { return motivo; }
    public String getOdontologo() { return odontologo; }
}
