<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<div id="menu" class="mainFrame">
    <ul>
        <c:choose>
            <c:when test="${sessionScope.admin == 'false'}" >
                <li><a href="${pageContext.request.contextPath}/app/main">Panel Użytkownika</a></li>
                <li><a href="${pageContext.request.contextPath}/app/activities">Moja Aktywność</a></li>
            </c:when>
            <c:otherwise>
                <li><a href="${pageContext.request.contextPath}/app/main">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/app/users">Użytkownicy</a></li>
                <li><a href="${pageContext.request.contextPath}/app/clients">Klienci</a></li>
                <li><a href="${pageContext.request.contextPath}/app/reports">Raporty</a></li>
            </c:otherwise>
        </c:choose>

        <li class="userProfile"> <a href="${pageContext.request.contextPath}/app/userProfile">
            <span id="userLogo">&#x1f464;</span> ${sessionScope.userName}
        </a></li>
    </ul>
</div>