<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<%@include file="/WEB-INF/assets/header.jsp"%>
<%@include file="/WEB-INF/assets/menu.jsp"%>

    <div id="main" class="mainFrame">
        <br>
        <button class="mainBtn" onclick="window.location.href = '${pageContext.request.contextPath}/app/register';">
            Dodaj użytkownika</button>
        <br><br>

            <table class="mainTable">
                <thead>
                    <tr>
                        <th>Id</th>
                        <th>Imię i nazwisko</th>
                        <th>E-mail</th>
                        <th>Admin?</th>
                        <th>Klienci</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${users}" var="user">
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.firstName} ${user.lastName}</td>
                            <td>${user.email}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${user.admin}">Tak</c:when>
                                    <c:otherwise>Nie</c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:forEach items="${user.clients}" var="client">
                                    ${client.name}<br>
                                </c:forEach>
                            </td>
                            <td>
                                <a class="btn" href="${pageContext.request.contextPath}/app/users/edit/${user.id}">Edytuj</a>
                                <a class="btn" href="${pageContext.request.contextPath}/app/users/delete/${user.id}" onclick="return confirm('Czy na pewno chcesz usunąć?')">Usuń</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>


        <br>

    </div>
<%@include file="/WEB-INF/assets/footer.jsp"%>

