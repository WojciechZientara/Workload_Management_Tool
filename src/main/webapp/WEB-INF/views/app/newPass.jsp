<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@include file="/WEB-INF/assets/header.jsp"%>

<c:if test="${user.passwordChanged}">
    <%@include file="/WEB-INF/assets/menu.jsp"%>
</c:if>

    <div id="main" class="mainFrame">
            <span id="login" class="landingPage">
            <form method="post">
                <input type="hidden" name="userId" value="${user.id}" />
                <table>
                    <tr><td>Nowe hasło: </td><td><input type="password" name="password1" /></td></tr>
                    <tr><td>Powtórz hasło: </td><td><input type="password" name="password2" /></td></tr>
                    <tr><td></td><td>
                        <c:if test="${incorrectCredentials == true}">
                            <span class="error">Wpisane hasła różnią się bądź pozostawiono puste pola.</span>
                        </c:if>
                    </td></tr>
                    <tr><td></td><td><input type="submit" value="Zapisz" class="submitBtn"></td></tr>
                </table>
            </form>
        </span>
    </div>
<%@include file="/WEB-INF/assets/footer.jsp"%>

