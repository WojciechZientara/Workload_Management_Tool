<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<div id="menu" class="mainFrame">
    <ul>
        <li><a href="${pageContext.request.contextPath}/app/main">Strona Główna</a></li>

        <c:if test="${sessionScope.admin == 'true'}">
            <li><a href="${pageContext.request.contextPath}/app/users">Użytkownicy</a></li>
            <li><a href="${pageContext.request.contextPath}/app/clients">Klienci</a></li>
        </c:if>

        <li class="userProfile"> <a href="${pageContext.request.contextPath}/app/userProfile">
            <span id="userLogo">&#x1f464;</span> ${sessionScope.userName}
        </a></li>
    </ul>
</div>