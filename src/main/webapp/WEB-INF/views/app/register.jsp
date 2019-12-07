<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@include file="/WEB-INF/assets/header.jsp"%>
    <div id="main" class="mainFrame">
            <span id="login" class="landingPage">
            Dane użytkownika:<br><br>
            <form:form method="post" modelAttribute="user" >
                <table>
                    <tr><td></td><td>
                        <c:if test="${userExists == true}">
                            <span class="error">Istnieje już użytkownik o takim e-mailu</span>
                        </c:if>
                    </td></tr>
                    <tr><td>Imię: </td><td><form:input path="firstName" /></td></tr>
                    <tr><td>        </td><td><form:errors path="firstName" cssClass="error"/></td></tr>
                    <tr><td>Nazwisko: </td><td><form:input path="lastName" /></td></tr>
                    <tr><td>        </td><td><form:errors path="lastName" cssClass="error"/></td></tr>
                    <tr><td>E-mail: </td><td><form:input path="email" /></td></tr>
                    <tr><td>        </td><td><form:errors path="email" cssClass="error"/></td></tr>
                    <tr><td>Admin: </td><td><form:checkbox path="admin" /></td></tr>
                    <tr><td>Klienci: </td><td><form:select path="clients" items="${clients}"
                                                       itemValue="id" itemLabel="name" multiple="true"/></td></tr>

                    <c:choose>
                        <c:when test="${!edit}">
                            <tr><td>Hasło: </td><td><form:password path="password" /></td></tr>
                        </c:when>
                        <c:otherwise>
                            <form:hidden path="password" value="123"/>
                        </c:otherwise>
                    </c:choose>

                    <tr><td></td><td><input type="submit" value="Zapisz" class="submitBtn"></td></tr>
                </table>
            </form:form>
        </span>
    </div>
<%@include file="/WEB-INF/assets/footer.jsp"%>

