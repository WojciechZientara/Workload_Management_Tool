<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="activities">
    <div style="margin-bottom: 80px">
       <span id="startWork">
            <button class="mainBtn" id="startWorkBtn" onclick="window.location.href = '${pageContext.request.contextPath}/app/console/workStart';">
            Rozpoczęcie pracy</button><br>
            ${startTime}
        </span>
        <span id="endWork">
            <button class="mainBtn" id="endWorkBtn" onclick="window.location.href = '${pageContext.request.contextPath}/app/console/workEnd';">
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
                <td><button class="mainBtn" id="stop" onclick="window.location.href = '${pageContext.request.contextPath}/app/console/stopTask';">
                    Przerwij</button></td>
                <td><button class="mainBtn" id="finish" onclick="window.location.href = '${pageContext.request.contextPath}/app/console/finishTask';">
                    Ukończ</button></td>
            </tr>

            <c:url var="actionUrl" value="console/activateTask" />
            <form:form method="post" modelAttribute="activity" action="${actionUrl}">
                <tr><td style="text-align: right">Zadania: </td>
                    <td><form:select cssClass="dropdown" path="task" items="${reservedTasks}"
                                     itemValue="id" itemLabel="name" /></td>
                    <td></td></tr>
                <tr>
                    <td></td>
                    <td style="text-align: right"> <input type="submit" id="activateTask" value="Aktywuj" class="mainBtn"> </td>
                    <td></td></tr>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form:form>
        </table>
    </div>
</div>
