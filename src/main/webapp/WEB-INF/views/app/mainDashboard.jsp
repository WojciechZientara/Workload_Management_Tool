<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<%@include file="/WEB-INF/assets/header.jsp"%>
<%@include file="/WEB-INF/assets/menu.jsp"%>



<div id="main" class="mainFrame">

    <div id="chart_div" style="margin: 30px;">
        <%-- Chart --%>
    </div>

    <table class="mainTable">
        <thead>
        <tr>
            <th>Użytkownik</th>
            <th>Aktywność</th>
            <th>Czas Trwania (m)</th>
            <th>Średni Czas (m)</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${activities}" var="activity">
            <tr
                    <c:if test="${activity[3].description == 'Active'}">style="text-shadow: -1px 0 gold, 0 1px gold,1px 0 gold, 0 -1px gold "</c:if>
            >
                <td>${activity[0].firstName} ${activity[0].lastName}</td>
                <td>${activity[1]}</td>
                <td>${activity[2]}</td>
                <td>${activity[3].estimatedDuration}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <br>
</div>

<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
<script type="text/javascript">




    google.load('visualization', 'current', { 'packages': ['bar'] });
    google.setOnLoadCallback(drawStuff);

    function drawStuff() {

        var data = new google.visualization.DataTable();
        data.addColumn('string', '');
        data.addColumn('number', 'Czas pracy');
        <c:forEach items="${activitiesDto.assignedTasks}" var="task">
        data.addColumn('number', '${task.name}');
        data.addColumn('number', '${task.name}');
        </c:forEach>


        data.addRows([
            <c:forEach items="${activitiesDto.timesMatrix}" var="record">
            ['${record[0]}', <c:forEach items="${record}" var="value" begin="1"> ${value}, </c:forEach> ],
            </c:forEach>
        ])


        var options = {
            title: 'Wykres aktywności',
            isStacked: true,
            width: window.innerWidth - 200,
            height: 500,
            chart: {},
            legend: {position: 'none'},
            vAxis: {
                viewWindow: {
                    min: 0,
                    max: 500
                }
            },

            series: {
                0: {targetAxisIndex: 0}, //working time

        <c:set var="counter" value="1"></c:set>
        <c:set var="axis" value="1"></c:set>

        <c:forEach items="${activitiesDto.assignedTasks}" var="task">
        <c:choose>
        <c:when test="${task.description == 'Active'}">
        ${counter}: {targetAxisIndex: ${axis}, color:'yellow'},
        </c:when>
        <c:when test="${task.completed == true || task.name == 'Bezczynność'}">
        ${counter}: {targetAxisIndex: ${axis}, color:'forestgreen'},
        </c:when>
        <c:otherwise>
        ${counter}: {targetAxisIndex: ${axis}, color:'#D80000'},
        </c:otherwise>
        </c:choose>
        <c:set var="counter" value="${counter+1}"></c:set>
        <c:set var="axis" value="${axis == 1 ? 2 : 1}"></c:set>
        </c:forEach>

        <c:forEach items="${activitiesDto.assignedTasks}" var="task">
        ${counter}: {targetAxisIndex: ${axis}, color:'forestgreen'},
        <c:set var="counter" value="${counter+1}"></c:set>
        <c:set var="axis" value="${axis == 1 ? 2 : 1}"></c:set>
        </c:forEach>

    },

        vAxes: {
            0: {title:'Minuty', textStyle: {color: 'gray'}, titleTextStyle: {color: 'gray'},},
            1: {title:'', textStyle: {color: 'gray'}, titleTextStyle: {color: 'gray'}},
            2: {title:'title', textStyle: {color: 'white'}, titleTextStyle: {color: 'white'} }
        }

    };

        var chart = new google.charts.Bar(document.getElementById('chart_div'));
        chart.draw(data, google.charts.Bar.convertOptions(options));
    };

</script>

<%@include file="/WEB-INF/assets/footer.jsp"%>

