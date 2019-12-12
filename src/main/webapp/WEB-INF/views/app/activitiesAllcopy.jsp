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
            <th>Czas Trwania</th>
            <th>Średni Czas</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${activities}" var="activity">
            <tr>
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
            <c:set var = "maxTime" value = "0"/>
            <c:forEach items="${activitiesDto.timesMatrix}" var="record">
            <c:if test="${record[1] > maxTime}"><c:set var = "maxTime" value = "${record[1]}"/></c:if>
            <c:set var="sumUser" value="0"></c:set>
            <c:forEach items="${record}" var="taskTime" begin="2">

            </c:forEach>
            ['${record[0]}', <c:forEach items="${record}" var="value" begin="1"> ${value}, </c:forEach> ],
            </c:forEach>
        ]);




        var options = {
            title: 'Wykres aktywności',
            isStacked: true,
            width: window.innerWidth - 200,
            height: 300,
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

        <%--        <c:forEach items="${activitiesDto.users}">--%>
        <%--        ${counter}: {targetAxisIndex: ${axis}, color:'black'},--%>
        <%--        <c:set var="counter" value="${counter+1}"></c:set>--%>
        <%--        <c:set var="axis" value="${axis == 1 ? 2 : 1}"></c:set>--%>
        <%--        </c:forEach>--%>

        // 1: {targetAxisIndex: 1, color:'green'},
        // 2: {targetAxisIndex: 2, color:'green'},
        // 3: {targetAxisIndex: 1, color:'green'}, //bezczynnosc

        <c:forEach items="${activitiesDto.assignedTasks}" var="task">
        ${counter}: {targetAxisIndex: ${axis}, color:'forestgreen'},
        <c:set var="counter" value="${counter+1}"></c:set>
        <c:set var="axis" value="${axis == 1 ? 2 : 1}"></c:set>
        </c:forEach>


        // 4: {targetAxisIndex: 2, color:'green'},
        // 5: {targetAxisIndex: 1, color:'green'},
        // 6: {targetAxisIndex: 2, color:'green'},
        // 7: {targetAxisIndex: 1, color:'green'}, // pozostale taski aktywne


        <c:forEach items="${activitiesDto.assignedTasks}" var="task">
        ${counter}: {targetAxisIndex: ${axis}, color:'maroon'},
        <c:set var="counter" value="${counter+1}"></c:set>
        <c:set var="axis" value="${axis == 1 ? 2 : 1}"></c:set>
        </c:forEach>


//                8: {targetAxisIndex: 2, color:'green'},
//                9: {targetAxisIndex: 1, color:'green'},
//                10: {targetAxisIndex: 2, color:'maroon'},
//                11: {targetAxisIndex: 1, color:'maroon'},
//                12: {targetAxisIndex: 2, color:'maroon'},
//                13: {targetAxisIndex: 1, color:'maroon'},
//                14: {targetAxisIndex: 2, color:'maroon'},
//                15: {targetAxisIndex: 1, color:'maroon'},
//                16: {targetAxisIndex: 2, color:'maroon'},
//                17: {targetAxisIndex: 1, color:'maroon'},
//                18: {targetAxisIndex: 2, color:'maroon'},







        <%--                <c:set var="counter" value="1"></c:set>--%>
        <%--                <c:forEach items="${activitiesDto.assignedTasks}" var="task">--%>
        <%--                    <c:choose>--%>
        <%--                        <c:when test="${task.duration > 0}">--%>
        <%--                            ${counter}: {targetAxisIndex: 1, color: 'forestgreen'},--%>
        <%--                            ${counter+1}: {targetAxisIndex: 2, color: 'maroon'},--%>
        <%--                            <c:set var="counter" value="${counter + 2}"></c:set>--%>
        <%--                        </c:when>--%>
        <%--                        <c:otherwise>--%>
        <%--                            ${counter}: {targetAxisIndex: 2, color: 'maroon'},--%>
        <%--                            <c:set var="counter" value="${counter + 1}"></c:set>--%>
        <%--                        </c:otherwise>--%>
        <%--                    </c:choose>--%>
        <%--                </c:forEach>--%>
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

