<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<div id="menu" class="mainFrame">
    <form action="<c:url value="/logout"/>" method="post">
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


            <li class="userProfile">
                <input id="logout" type="submit" value="Wyloguj">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </li>
            <li class="userProfile">
                <a href="${pageContext.request.contextPath}/app/userProfile">
                <span id="userLogo">&#x1f464;</span> ${email} ${sessionScope.userName}
                </a>
            </li>
    </ul>
    </form>
</div>