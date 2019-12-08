<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@include file="/WEB-INF/assets/header.jsp"%>
    <div id="main" class="mainFrame">
            <span id="login" class="landingPage">
            Szczegóły raportu:<br><br>
            <form:form method="post" modelAttribute="bauReport" >
                <table>
                    <tr><td></td><td>
                        <c:if test="${reportExists == true}">
                            <span class="error">Istnieje już raport o takich danych.</span>
                        </c:if>
                    </td></tr>
                    <tr><td>Nazwa: </td><td><form:input path="name" /></td></tr>
                    <tr><td>        </td><td><form:errors path="name" cssClass="error"/></td></tr>
                    <tr><td>Klient: </td><td><form:select path="client" items="${clients}"
                                                       itemValue="id" itemLabel="name" /></td></tr>
                    <tr><td>        </td><td><form:errors path="client" cssClass="error"/></td></tr>
                    <tr><td></td><td><input type="submit" value="Zapisz" class="submitBtn"></td></tr>
                </table>
            </form:form>
        </span>
    </div>
<%@include file="/WEB-INF/assets/footer.jsp"%>

