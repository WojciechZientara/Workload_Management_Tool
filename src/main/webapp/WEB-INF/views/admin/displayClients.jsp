<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<%@include file="/WEB-INF/assets/header.jsp"%>
<%@include file="/WEB-INF/assets/menu.jsp"%>

    <div id="main" class="mainFrame">
        <br>
        <button class="mainBtn" onclick="window.location.href = '${pageContext.request.contextPath}/admin/addClient';">
            Dodaj klienta</button>
        <br><br>

            <table class="mainTable">
                <thead>
                    <tr>
                        <th>Id</th>
                        <th>Nazwa</th>
                        <th>Użytkownicy</th>
                        <th>Lista Raportów BAU</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${clients}" var="client">
                        <tr>
                            <td>${client.id}</td>
                            <td>${client.name}</td>
                            <td>
                                <c:forEach items="${client.users}" var="user">
                                    ${user.firstName} ${user.lastName}<br>
                                </c:forEach>
                            </td>
                            <td>
                                <c:forEach items="${client.bauReportList}" var="report">
                                    ${report.id}: ${report.name}<br>
                                </c:forEach>
                            </td>
                            <td>
                                <a class="btn" href="${pageContext.request.contextPath}/admin/editClient/${client.id}">Edytuj</a>
                                <a class="btn" href="${pageContext.request.contextPath}/admin/deleteClient/${client.id}" onclick="return confirm('Czy na pewno chcesz usunąć?')">Usuń</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>


        <br>

    </div>
<%@include file="/WEB-INF/assets/footer.jsp"%>

