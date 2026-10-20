<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>Listado de pacientes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilos.css">
  </head>
  <body>
    <h1>Listado de pacientes</h1>
    <c:if test="${registrado}"><p>Paciente dado de alta.</p></c:if>
    <c:if test="${eliminado}"><p>Paciente eliminado; sus citas asociadas también se han eliminado.</p></c:if>
    <table>
      <thead>
        <tr>
          <th>Id</th><th>Nombre</th><th>Apellidos</th><th>DNI</th>
          <th>Fecha nacimiento</th><th>Teléfono</th><th>Email</th>
          <th>Dirección</th><th>Alergias</th><th>Citas</th><th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="paciente" items="${pacientes}">
          <tr>
            <td><c:out value="${paciente.id}"/></td>
            <td><c:out value="${paciente.nombre}"/></td>
            <td><c:out value="${paciente.apellidos}"/></td>
            <td><c:out value="${paciente.dni}"/></td>
            <td><c:out value="${paciente.fechaNacimiento}"/></td>
            <td><c:out value="${paciente.telefono}"/></td>
            <td><c:out value="${paciente.email}"/></td>
            <td><c:out value="${paciente.direccion}"/></td>
            <td><c:out value="${paciente.alergias}"/></td>
            <td><c:out value="${paciente.numeroCitas}"/></td>
            <td>
              <a href="${pageContext.request.contextPath}/citas/nueva?paciente=${paciente.id}">Nueva cita</a>
              <form method="post" action="${pageContext.request.contextPath}/pacientes/borrar">
                <input type="hidden" name="id_paciente" value="${paciente.id}">
                <input type="submit" value="Borrar">
              </form>
            </td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
    <p>
      <a href="${pageContext.request.contextPath}/pacientes/nuevo">Nuevo paciente</a> |
      <a href="${pageContext.request.contextPath}/">Volver al inicio</a>
    </p>
  </body>
</html>
