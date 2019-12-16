<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@include file="/WEB-INF/assets/header.jsp"%>
<%@include file="/WEB-INF/assets/menu.jsp"%>

    <div id="main" class="mainFrame">
            <span id="login" class="landingPage">
            Dane klienta:<br><br>
            <form:form method="post" modelAttribute="client" >
                <table>
                    <tr><td></td><td>
                        <c:if test="${clientExists == true}">
                            <span class="error">Istnieje już klient o takich danych.</span>
                        </c:if>
                    </td></tr>
                    <tr><td>Nazwa: </td><td><form:input path="name" /></td></tr>
                    <tr><td>        </td><td><form:errors path="name" cssClass="error"/></td></tr>
                    <tr><td>Użytkownicy: </td><td><form:select path="users" items="${users}"
                                                       itemValue="id" itemLabel="fullName" multiple="true"/></td></tr>
                    <tr><td></td><td><input type="submit" value="Zapisz" class="submitBtn"></td></tr>
                </table>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form:form>
        </span>
    </div>
<%@include file="/WEB-INF/assets/footer.jsp"%>

