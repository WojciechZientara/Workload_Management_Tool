<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="select" uri="http://www.springframework.org/tags/form" %>

<%@include file="/WEB-INF/assets/header.jsp"%>
<%@include file="/WEB-INF/assets/menu.jsp"%>

<div id="main" class="mainFrame">

    <div class="activities">
        <div style="margin-bottom: 80px">
           <span id="startWork">
                <button class="mainBtn" id="startWorkBtn" onclick="window.location.href = '${pageContext.request.contextPath}/app/main/activity/workStart';">
                Rozpoczęcie pracy</button><br>
                ${startTime}
            </span>
            <span id="endWork">
                <button class="mainBtn" id="endWorkBtn" onclick="window.location.href = '${pageContext.request.contextPath}/app/main/activity/workEnd';">
                Zakończenie pracy</button><br>
                ${endTime}
            </span>
        </div>
        <div>
            <table>
                <tr><td style="text-align: right">Aktywne zadanie: </td>
                    <td>
                        <label style="width: 350px; display: inline-block; padding-left: 5px; color:red;"> ${presentActivity.name} </label>
                    </td>
                    <td><button class="mainBtn" id="stop" onclick="window.location.href = '${pageContext.request.contextPath}/app/main/activity/stop';">
                        Przerwij</button></td>
                    <td><button class="mainBtn" id="finish" onclick="window.location.href = '${pageContext.request.contextPath}/app/main/activity/finish';">
                        Ukończ</button></td>
                </tr>

                <c:url var="actionUrl" value="/app/main/activate" />
                <form:form method="post" modelAttribute="activity" action="${actionUrl}">
                    <tr><td style="text-align: right">Zadania: </td>
                        <td><form:select cssClass="dropdown" path="task" items="${reservedTasks}"
                                         itemValue="id" itemLabel="name" /></td>
                        <td></td></tr>
                    <tr>
                        <td></td>
                        <td style="text-align: right"> <input type="submit" id="activateTask" value="Aktywuj" class="mainBtn"> </td>
                        <td></td></tr>
                </form:form>
            </table>
        </div>
    </div>

    <div class="workload">
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
                    <td>${task.estimatedDuration}</td>
                    <td>${task.description}</td>

                    <c:choose>
                        <c:when test="${task.user == null}">
                            <td style="text-align: center">
                                <c:if test="${startTime != '' && endTime == null}">
                                    <a class="btn" href="${pageContext.request.contextPath}/app/main/assignTask/${task.id}">Rezerwuj</a>
                                </c:if>
                            </td>
                        </c:when>
                        <c:otherwise>
                            <td>${task.user.firstName} ${task.user.lastName}</td>
                        </c:otherwise>
                    </c:choose>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<%@include file="/WEB-INF/assets/footer.jsp"%>

