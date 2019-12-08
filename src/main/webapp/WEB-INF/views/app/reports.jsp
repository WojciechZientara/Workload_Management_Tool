<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<%@include file="/WEB-INF/assets/header.jsp"%>
<%@include file="/WEB-INF/assets/menu.jsp"%>

    <div id="main" class="mainBtn">
        <br>
        <button class="mainBtn" onclick="window.location.href = '${pageContext.request.contextPath}/app/reports/add';">
            Dodaj raport</button>
        <br><br>

            <table class="mainTable">
                <thead>
                    <tr>
                        <th>Id</th>
                        <th>Nazwa</th>
                        <th>Klient</th>
                        <th>Suma czasu produkcji (s)</th>
                        <th>Dostarczone raporty</th>
                        <th>Średni czas produkcji (s)</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${bauReports}" var="report">
                        <tr>
                            <td>${report.id}</td>
                            <td>${report.name}</td>
                            <td>${report.client.name}</td>
                            <td>${report.sumOfDuration}</td>
                            <td>${report.numberOfRuns}</td>
                            <td>${report.averageDuration}</td>
                            <td>
                                <a class="btn" href="${pageContext.request.contextPath}/app/reports/edit/${report.id}">Edytuj</a>
                                <a class="btn" href="${pageContext.request.contextPath}/app/reports/delete/${report.id}" onclick="return confirm('Czy na pewno chcesz usunąć?')">Usuń</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>


        <br>

    </div>
<%@include file="/WEB-INF/assets/footer.jsp"%>

