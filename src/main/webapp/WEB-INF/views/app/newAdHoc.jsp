<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@include file="/WEB-INF/assets/header.jsp"%>
    <div id="main" class="mainFrame">
            <span id="login" class="landingPage">
            Dane requestu:<br><br>
            <form:form method="post" modelAttribute="task" >
                <table>
                    <tr><td>Nazwa: </td><td><form:input path="name" /></td></tr>
                    <tr><td>        </td><td><form:errors path="name" cssClass="error"/></td></tr>
                    <tr><td>Klient: </td><td><form:select path="client" items="${clients}"
                                                          itemValue="id" itemLabel="name"/></td></tr>
                    <tr><td>        </td><td><form:errors path="client" cssClass="error"/></td></tr>
                    <tr><td>Opis: </td><td><form:textarea path="description" cols="80" rows="3" /></td></tr>
                    <tr><td>        </td><td><form:errors path="description" cssClass="error"/></td></tr>
                    <tr><td>Estymowany czas: </td><td><form:input path="estimatedDuration" /></td></tr>
                    <tr><td>        </td><td><form:errors path="estimatedDuration" cssClass="error"/></td></tr>
                    <tr><td></td><td><input type="submit" value="Zapisz" class="submitBtn"></td></tr>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                </table>
            </form:form>
        </span>
    </div>
<%@include file="/WEB-INF/assets/footer.jsp"%>

