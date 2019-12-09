<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<%@include file="/WEB-INF/assets/header.jsp"%>
<%@include file="/WEB-INF/assets/menu.jsp"%>

    <div id="main" class="mainFrame">

            <table class="mainTable">
                <thead>
                    <tr>
                        <th>Data</th>
                        <th>Start</th>
                        <th>Koniec</th>
                        <th>Czas</th>
                        <th>Nazwa</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${activities}" var="activity">
                        <tr>
                            <td>${activity.date}</td>
                            <td>${activity.startTime}</td>
                            <td>${activity.endTime}</td>

                            <c:set var="Hours" value="${fn:substringBefore(activity.duration.getSeconds() div 3600, '.')}"/>
                            <c:set var="SecondsLeft" value="${activity.duration.getSeconds() - (3600 * Hours)}"/>
                            <c:set var="Minutes" value="${fn:substringBefore(SecondsLeft div 60, '.')}"/>
                            <c:set var="SecondsLeft" value="${activity.duration.getSeconds() - (3600 * Hours) - (60 * Minutes)}"/>
                            <td>
                                <c:choose>
                                    <c:when test="${activity.duration.getSeconds() == null}"></c:when>
                                    <c:otherwise>
                                        <c:if test="${Hours < 10}">0</c:if>${Hours}:<c:if test="${Minutes < 10}">0</c:if>${Minutes}:<c:if test="${SecondsLeft < 10}">0</c:if>${SecondsLeft}
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${activity.name == 'Working Hours'}">
                                        <b>Working Hours</b>
                                    </c:when>
                                    <c:when test="${activity.name == 'Inactive'}">
                                        Inactive
                                    </c:when>
                                    <c:otherwise>
                                        ${activity.task.name}
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        <br>
    </div>
<%@include file="/WEB-INF/assets/footer.jsp"%>

