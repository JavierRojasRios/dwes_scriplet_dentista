package es.dwes.dentista.dao;

import es.dwes.dentista.model.Cita;
import es.dwes.dentista.model.Odontologo;
import es.dwes.dentista.model.Paciente;
import es.dwes.dentista.model.PacienteOpcion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DentistaDao {
    private static final String URL = "jdbc:mysql://localhost:3306/dentista"
            + "?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";
    private static final String USUARIO = "user";
    private static final String CLAVE = "user";

    private Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException error) {
            throw new SQLException("No se encuentra MySQL Connector/J en la aplicación.", error);
        }
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }

    public List<Paciente> listarPacientes() throws SQLException {
        String sql = "SELECT p.id_paciente, p.nombre, p.apellidos, p.dni, p.fecha_nacimiento, "
                + "p.telefono, p.email, p.direccion, p.alergias, "
                + "(SELECT COUNT(*) FROM cita c WHERE c.id_paciente = p.id_paciente) AS num_citas "
                + "FROM paciente p ORDER BY p.apellidos, p.nombre";
        List<Paciente> pacientes = new ArrayList<>();
        try (Connection conexion = conectar();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {
            while (resultado.next()) {
                Date fecha = resultado.getDate("fecha_nacimiento");
                pacientes.add(new Paciente(
                        resultado.getInt("id_paciente"),
                        resultado.getString("nombre"),
                        resultado.getString("apellidos"),
                        resultado.getString("dni"),
                        fecha == null ? null : fecha.toLocalDate(),
                        resultado.getString("telefono"),
                        resultado.getString("email"),
                        resultado.getString("direccion"),
                        resultado.getString("alergias"),
                        resultado.getInt("num_citas")));
            }
        }
        return pacientes;
    }

    public List<Cita> listarCitas(boolean todas) throws SQLException {
        String sql = "SELECT c.id_cita, c.fecha, c.hora, c.motivo, "
                + "p.nombre AS paciente_nombre, p.apellidos AS paciente_apellidos, "
                + "o.nombre AS odontologo "
                + "FROM cita c "
                + "JOIN paciente p ON p.id_paciente = c.id_paciente "
                + "JOIN odontologos o ON o.id_odontologo = c.id_odontologo "
                + (todas ? "" : "WHERE c.fecha >= CURDATE() ")
                + "ORDER BY c.fecha, c.hora";
        List<Cita> citas = new ArrayList<>();
        try (Connection conexion = conectar();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {
            while (resultado.next()) {
                Date fecha = resultado.getDate("fecha");
                Time hora = resultado.getTime("hora");
                citas.add(new Cita(
                        resultado.getInt("id_cita"),
                        fecha.toLocalDate(),
                        hora.toLocalTime(),
                        resultado.getString("paciente_apellidos") + ", "
                                + resultado.getString("paciente_nombre"),
                        resultado.getString("motivo"),
                        resultado.getString("odontologo")));
            }
        }
        return citas;
    }

    public List<PacienteOpcion> listarPacientesParaCita() throws SQLException {
        String sql = "SELECT id_paciente, nombre, apellidos FROM paciente ORDER BY apellidos, nombre";
        List<PacienteOpcion> pacientes = new ArrayList<>();
        try (Connection conexion = conectar();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {
            while (resultado.next()) {
                pacientes.add(new PacienteOpcion(
                        resultado.getInt("id_paciente"),
                        resultado.getString("nombre"),
                        resultado.getString("apellidos")));
            }
        }
        return pacientes;
    }

    public List<Odontologo> listarOdontologos() throws SQLException {
        String sql = "SELECT id_odontologo, nombre, especialidad FROM odontologos ORDER BY nombre";
        List<Odontologo> odontologos = new ArrayList<>();
        try (Connection conexion = conectar();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {
            while (resultado.next()) {
                odontologos.add(new Odontologo(
                        resultado.getInt("id_odontologo"),
                        resultado.getString("nombre"),
                        resultado.getString("especialidad")));
            }
        }
        return odontologos;
    }

    public void crearPaciente(String nombre, String apellidos, String dni, LocalDate nacimiento,
                              String telefono, String email, String direccion, String alergias)
            throws SQLException {
        String sql = "INSERT INTO paciente "
                + "(nombre, apellidos, dni, fecha_nacimiento, telefono, email, direccion, alergias) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexion = conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, nombre);
            sentencia.setString(2, apellidos);
            sentencia.setString(3, dni);
            if (nacimiento == null) {
                sentencia.setNull(4, Types.DATE);
            } else {
                sentencia.setDate(4, Date.valueOf(nacimiento));
            }
            sentencia.setString(5, telefono);
            sentencia.setString(6, email);
            sentencia.setString(7, direccion);
            sentencia.setString(8, alergias);
            sentencia.executeUpdate();
        }
    }

    public void crearCita(LocalDate fecha, LocalTime hora, int idPaciente, int idOdontologo, String motivo)
            throws SQLException {
        String sql = "INSERT INTO cita (fecha, hora, id_paciente, id_odontologo, motivo) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conexion = conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setDate(1, Date.valueOf(fecha));
            sentencia.setTime(2, Time.valueOf(hora));
            sentencia.setInt(3, idPaciente);
            sentencia.setInt(4, idOdontologo);
            sentencia.setString(5, motivo);
            sentencia.executeUpdate();
        }
    }

    public void borrarPaciente(int id) throws SQLException {
        try (Connection conexion = conectar();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "DELETE FROM paciente WHERE id_paciente = ?")) {
            sentencia.setInt(1, id);
            sentencia.executeUpdate();
        }
    }

    public void borrarCita(int id) throws SQLException {
        try (Connection conexion = conectar();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "DELETE FROM cita WHERE id_cita = ?")) {
            sentencia.setInt(1, id);
            sentencia.executeUpdate();
        }
    }
}
