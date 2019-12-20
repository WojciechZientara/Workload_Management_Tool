<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="select" uri="http://www.springframework.org/tags/form" %>

<%@include file="/WEB-INF/assets/header.jsp"%>
<%@include file="/WEB-INF/assets/menu.jsp"%>

<div id="main" class="mainFrame">

    <%@include file="/WEB-INF/views/app/displayUserConsole.jsp"%>

    <div class="workload">

        <span id="buttonSpan" >
            <c:choose>
                <c:when test="${startTime != null && endTime == null}">
                <button class="mainBtn adHoc" onclick="window.location.href = '${pageContext.request.contextPath}/app/userPanel/createAdHoc';">
                    Dodaj ad-hoc request</button> <button style="display: none" class="mainBtn adHoc"> Dodaj ad-hoc request</button>
                </c:when>
                <c:otherwise>
                <button style="display: none" class="mainBtn adHoc" onclick="window.location.href = '${pageContext.request.contextPath}/app/userPanel/createAdHoc';">
                    Dodaj ad-hoc request</button> <button class="mainBtn adHoc"> Dodaj ad-hoc request</button>
                </c:otherwise>
            </c:choose>
        </span>

        <br><br>

        <table class="mainTable">
            <thead>
            <tr>
                <th>Id</th>
                <th>Klient</th>
                <th>Nazwa</th>
                <th>Typ</th>
                <th>Czas (m)</th>
                <th>Opis</th>
                <th>Częstotliwość</th>
                <th>Dzień</th>
                <th>Użytkownik</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${tasks}" var="task">
                <tr>
                    <td>${task.id}</td>
                    <td>${task.client.name}</td>
                    <td>${task.name}</td>
                    <td>${task.type}</td>
                    <c:set var="estimatedMinutes" value="${fn:substringBefore(task.estimatedDuration div 60, '.')}"/>
                    <td>${estimatedMinutes}</td>
                    <td>${task.description}</td>
                    <td>${task.bauArchetype.frequency}</td>
                    <td>${task.bauArchetype.runDay}</td>

                    <c:choose>
                        <c:when test="${task.user == null}">
                            <td style="text-align: center">
                                <c:choose>
                                    <c:when test="${startTime != '' && endTime == null}">
                                        <a class="btn ajax worktime" data-taskid="${task.id}" data-type="assignTask" data-action="${pageContext.request.contextPath}/app/userPanel/assignTask/${task.id}">Rezerwuj</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a class="btn ajax worktime" style="display: none;" data-taskid="${task.id}" data-type="assignTask" data-action="${pageContext.request.contextPath}/app/userPanel/assignTask/${task.id}">Rezerwuj</a>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </c:when>
                        <c:otherwise>
                            <td style="text-align: center">
                                ${task.user.firstName} ${task.user.lastName}
                                <c:if test="${task.user.id == sessionScope.id}">
                                    <c:choose>
                                        <c:when test="${startTime != '' && endTime == null}">
                                            <a class="btn ajax worktime" data-taskid="${task.id}" data-type="unassignTask" data-action="${pageContext.request.contextPath}/app/userPanel/unassignTask/${task.id}">Anuluj</a>
                                        </c:when>
                                        <c:otherwise>
                                            <a class="btn ajax worktime" style="display: none;" data-taskid="${task.id}" data-type="unassignTask" data-action="${pageContext.request.contextPath}/app/userPanel/unassignTask/${task.id}">Anuluj</a>
                                        </c:otherwise>
                                    </c:choose>
                                        <c:if test="${startTime != '' && endTime == null}">
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

<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script><%@include file="../../assets/userPanelAndConsole.js"%></script>

<%@include file="/WEB-INF/assets/footer.jsp"%>

