<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<%@include file="/WEB-INF/assets/header.jsp"%>
<%@include file="/WEB-INF/assets/menu.jsp"%>

<div id="main" class="mainFrame">

    <br>
    <button class="mainBtn" onclick="window.location.href = '${pageContext.request.contextPath}/app/main/newAdHoc';">
        Dodaj ad-hoc request</button>
    <br><br>

    <table class="mainTable">
        <thead>
        <tr>
            <th>Klient</th>
            <th>Nazwa</th>
            <th>Typ</th>
            <th>Czas (s)</th>
            <th>Opis</th>
            <th>Użytkownik</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${tasks}" var="task">
            <tr>
                <td>${task.client.name}</td>
                <td>${task.name}</td>
                <td>${task.type}</td>
                <td>${task.duration}</td>
                <td>${task.description}</td>
                <td>${task.user.firstName} ${task.user.lastName}</td>

                <td>
                    <a class="btn" href="${pageContext.request.contextPath}/app/main/assignTask/${task.id}">Rezerwuj</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</div>

<%@include file="/WEB-INF/assets/footer.jsp"%>

