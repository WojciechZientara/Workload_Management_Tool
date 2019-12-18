<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="activities">
    <div style="margin-bottom: 80px">
       <span id="startWork">
            <button class="mainBtn ajax" id="startWorkBtn" data-type="startWork" data-action="${pageContext.request.contextPath}/app/console/workStart">
            Rozpoczęcie pracy</button><br>
           <span id="startTime">${startTime}</span>
        </span>
        <span id="endWork">
            <button class="mainBtn ajax" id="endWorkBtn" data-type="endWork" data-action="${pageContext.request.contextPath}/app/console/workEnd">
            Zakończenie pracy</button><br>
            <span id="endTime">${endTime}</span>
        </span>
    </div>
    <div>
        <table>
            <tr><td style="text-align: right">Aktywne zadanie: </td>
                <td>
                    <label id="activeTaskLabel" style="width: 350px; display: inline-block; padding-left: 5px; color:red;"> ${presentActivity.name} </label>
                </td>
                <td><button class="mainBtn ajax" id="stop" data-type="stop" data-action="${pageContext.request.contextPath}/app/console/stopTask">
                    Przerwij</button></td>
                <td><button class="mainBtn ajax" id="finish" data-type="finish" data-action="${pageContext.request.contextPath}/app/console/finishTask">
                    Ukończ</button></td>
            </tr>

<%--            <c:url var="actionUrl" value="console/activateTask" />--%>
<%--            <form:form method="post" modelAttribute="activity" action="${actionUrl}">--%>
            <form:form method="post" modelAttribute="activity">
                <tr><td style="text-align: right">Zadania: </td>
                    <td><form:select cssClass="dropdown" path="task" items="${reservedTasks}"
                                     itemValue="id" itemLabel="name" /></td>
                    <td></td></tr>
            </form:form>
                <tr>
                    <td><span style="display: none" id="activatePath"></span></td>
                    <td id="activateBtnTd" style="text-align: right"> <button id="activateTask" class="mainBtn ajax" data-type="activateTask"
                       data-action="${pageContext.request.contextPath}/app/console/activateTask/0">Aktywuj</button> </td>
                    <td></td></tr>
        </table>
    </div>
</div>
