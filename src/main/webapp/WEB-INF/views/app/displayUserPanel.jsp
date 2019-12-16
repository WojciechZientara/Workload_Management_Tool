<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="select" uri="http://www.springframework.org/tags/form" %>

<%@include file="/WEB-INF/assets/header.jsp"%>
<%@include file="/WEB-INF/assets/menu.jsp"%>

<div id="main" class="mainFrame">

    <%@include file="/WEB-INF/views/app/displayUserConsole.jsp"%>

    <div class="workload">
        <button class="mainBtn" onclick="window.location.href = '${pageContext.request.contextPath}/app/userPanel/createAdHoc';">
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
                <th>Częstotliwość</th>
                <th>Dzień</th>
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
                    <td>${task.bauArchetype.frequency}</td>
                    <td>${task.bauArchetype.runDay}</td>

                    <c:choose>
                        <c:when test="${task.user == null}">
                            <td style="text-align: center">
                                <c:if test="${startTime != '' && endTime == null}">
                                    <a class="btn" href="${pageContext.request.contextPath}/app/userPanel/assignTask/${task.id}">Rezerwuj</a>
                                </c:if>
                            </td>
                        </c:when>
                        <c:otherwise>
                            <td>
                                ${task.user.firstName} ${task.user.lastName}
                                <c:if test="${task.user.id == sessionScope.id}">
                                        <c:if test="${startTime != '' && endTime == null}">
                                             <a class="btn" href="${pageContext.request.contextPath}/app/userPanel/unassignTask/${task.id}">Anuluj</a>
                                        </c:if>
                                </c:if>
                            </td>
                        </c:otherwise>
                    </c:choose>

                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

</div>

<%@include file="/WEB-INF/assets/footer.jsp"%>

