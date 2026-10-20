package es.dwes.dentista.servlet;

import es.dwes.dentista.dao.DentistaDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@WebServlet(urlPatterns = {
    "/pacientes", "/pacientes/nuevo", "/pacientes/borrar",
    "/citas", "/citas/nueva", "/citas/borrar"
})
public class DentistaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DentistaDao dao = new DentistaDao();
        try {
            switch (request.getServletPath()) {
                case "/pacientes" -> {
                    request.setAttribute("pacientes", dao.listarPacientes());
                    request.setAttribute("registrado", "1".equals(request.getParameter("registrado")));
                    request.setAttribute("eliminado", "1".equals(request.getParameter("eliminado")));
                    forward(request, response, "pacientes.jsp");
                }
                case "/pacientes/nuevo" -> forward(request, response, "formularioPaciente.jsp");
                case "/citas" -> {
                    boolean todas = "1".equals(request.getParameter("todas"));
                    request.setAttribute("citas", dao.listarCitas(todas));
                    request.setAttribute("todas", todas);
                    request.setAttribute("registrada", "1".equals(request.getParameter("registrada")));
                    request.setAttribute("anulada", "1".equals(request.getParameter("anulada")));
                    forward(request, response, "citas.jsp");
                }
                case "/citas/nueva" -> {
                    cargarOpcionesCita(request, dao);
                    request.setAttribute("pacienteSeleccionado", request.getParameter("paciente"));
                    forward(request, response, "formularioCita.jsp");
                }
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException error) {
            mostrarError(request, response, error);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        DentistaDao dao = new DentistaDao();
        try {
            switch (request.getServletPath()) {
                case "/pacientes/nuevo" -> guardarPaciente(request, response, dao);
                case "/pacientes/borrar" -> borrarPaciente(request, response, dao);
                case "/citas/nueva" -> guardarCita(request, response, dao);
                case "/citas/borrar" -> borrarCita(request, response, dao);
                default -> response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        } catch (SQLException error) {
            mostrarError(request, response, error);
        }
    }

    private void guardarPaciente(HttpServletRequest request, HttpServletResponse response, DentistaDao dao)
            throws ServletException, IOException, SQLException {
        String nombre = parametro(request, "nombre");
        String apellidos = parametro(request, "apellidos");
        String dni = parametro(request, "dni");
        String fechaNacimiento = parametro(request, "fecha_nacimiento");

        if (nombre.isBlank() || apellidos.isBlank() || dni.isBlank()) {
            request.setAttribute("errorFormulario", "Nombre, apellidos y DNI son obligatorios.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            forward(request, response, "formularioPaciente.jsp");
            return;
        }

        try {
            LocalDate nacimiento = fechaNacimiento.isBlank() ? null : LocalDate.parse(fechaNacimiento);
            dao.crearPaciente(nombre, apellidos, dni, nacimiento,
                    parametro(request, "telefono"), parametro(request, "email"),
                    parametro(request, "direccion"), parametro(request, "alergias"));
            response.sendRedirect(request.getContextPath() + "/pacientes?registrado=1");
        } catch (DateTimeParseException error) {
            request.setAttribute("errorFormulario", "La fecha de nacimiento no es válida.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            forward(request, response, "formularioPaciente.jsp");
        }
    }

    private void borrarPaciente(HttpServletRequest request, HttpServletResponse response, DentistaDao dao)
            throws IOException, SQLException {
        try {
            dao.borrarPaciente(Integer.parseInt(request.getParameter("id_paciente")));
            response.sendRedirect(request.getContextPath() + "/pacientes?eliminado=1");
        } catch (NumberFormatException error) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identificador de paciente no válido.");
        }
    }

    private void guardarCita(HttpServletRequest request, HttpServletResponse response, DentistaDao dao)
            throws ServletException, IOException, SQLException {
        String fecha = parametro(request, "fecha");
        String hora = parametro(request, "hora");
        String idPaciente = parametro(request, "id_paciente");
        String idOdontologo = parametro(request, "id_odontologo");
        request.setAttribute("pacienteSeleccionado", idPaciente);

        if (fecha.isBlank() || hora.isBlank() || idPaciente.isBlank() || idOdontologo.isBlank()) {
            request.setAttribute("errorFormulario", "Fecha, hora, paciente y odontólogo son obligatorios.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mostrarFormularioCita(request, response, dao);
            return;
        }

        try {
            dao.crearCita(LocalDate.parse(fecha), LocalTime.parse(hora),
                    Integer.parseInt(idPaciente), Integer.parseInt(idOdontologo),
                    parametro(request, "motivo"));
            response.sendRedirect(request.getContextPath() + "/citas?registrada=1");
        } catch (DateTimeParseException | NumberFormatException error) {
            request.setAttribute("errorFormulario", "La fecha, hora o selección no son válidas.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mostrarFormularioCita(request, response, dao);
        }
    }

    private void borrarCita(HttpServletRequest request, HttpServletResponse response, DentistaDao dao)
            throws IOException, SQLException {
        try {
            dao.borrarCita(Integer.parseInt(request.getParameter("id_cita")));
            response.sendRedirect(request.getContextPath() + "/citas?anulada=1");
        } catch (NumberFormatException error) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identificador de cita no válido.");
        }
    }

    private void mostrarFormularioCita(HttpServletRequest request, HttpServletResponse response, DentistaDao dao)
            throws ServletException, IOException, SQLException {
        cargarOpcionesCita(request, dao);
        forward(request, response, "formularioCita.jsp");
    }

    private void cargarOpcionesCita(HttpServletRequest request, DentistaDao dao) throws SQLException {
        request.setAttribute("pacientes", dao.listarPacientesParaCita());
        request.setAttribute("odontologos", dao.listarOdontologos());
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String vista)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/vistas/" + vista).forward(request, response);
    }

    private void mostrarError(HttpServletRequest request, HttpServletResponse response, Exception error)
            throws ServletException, IOException {
        getServletContext().log("Error al procesar la solicitud de la clínica dental", error);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        request.setAttribute("error", "No se ha podido completar la operación. Inténtelo de nuevo más tarde.");
        forward(request, response, "error.jsp");
    }

    private String parametro(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        return valor == null ? "" : valor.trim();
    }
}
