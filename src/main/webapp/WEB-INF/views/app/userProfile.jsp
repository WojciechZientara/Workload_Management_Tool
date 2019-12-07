<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@include file="/WEB-INF/assets/header.jsp"%>
<%@include file="/WEB-INF/assets/menu.jsp"%>

    <div id="main" class="mainFrame">
        <table class="mainTable">
            <tr><td>Imię: </td><td>${user.firstName}</td></tr>
            <tr><td>Nazwisko: </td><td>${user.lastName}</td></tr>
            <tr><td>E-mail: </td><td>${user.email}</td></tr>
        </table>
        <br>
        <a class="btn" href="${pageContext.request.contextPath}/app/user/newPass">Zmień hasło</a>
    </div>
<%@include file="/WEB-INF/assets/footer.jsp"%>

