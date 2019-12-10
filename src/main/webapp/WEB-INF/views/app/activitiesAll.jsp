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

['Bazyli Kopytko', 60, 48, 28, 20, 20, 60, 48, 15, 20, 20, 11]<br><br>


<c:set var = "noUsers" value = "${fn:length(activitiesDto.timesMatrix)}"/>
<c:forEach items="${activitiesDto.timesMatrix}" var="record" varStatus="i">
    <c:set var = "length" value = "${fn:length(record)}"/>
    ['${record[0]}',
    <c:forEach items="${record}" var="value" begin="1" varStatus="step">
        ${value}<c:if test="${step.index < length-1}">,</c:if><c:if test="${step.index == length-1}">]</c:if>
    </c:forEach><c:if test="${i.index < noUsers-1}">,</c:if>
    <br>
</c:forEach>

<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
<script type="text/javascript">

    google.load('visualization', '1.1', { 'packages': ['bar'] });
    google.setOnLoadCallback(drawStuff);

    function drawStuff() {
        var data = new google.visualization.DataTable();
        data.addColumn('string', '');
        data.addColumn('number', 'Czas pracy');

        <c:forEach items="${activitiesDto.activities}" var="entry">
        data.addColumn('number', '${entry.key}');
        data.addColumn('number', '${entry.key}');
        </c:forEach>


        // data.addColumn('number', 'Inactive - model');
        // data.addColumn('number', 'Inactive');
        // data.addColumn('number', '123 - model');
        // data.addColumn('number', '123');

        data.addRows([

            <c:set var = "noUsers" value = "${fn:length(activitiesDto.timesMatrix)}"/>
            <c:forEach items="${activitiesDto.timesMatrix}" var="record" varStatus="i">
            <c:set var = "length" value = "${fn:length(record)}"/>
                ['${record[0]}'
                <c:forEach items="${record}" var="value" begin="1" varStatus="step">
                ,${value}</c:forEach>],
            </c:forEach>
            ['Bazyli Kopytko', 60, , 28, 20, 20, , 48, 15, , 20, 11, , 15, 20, 20, 60, , 15, , 20, 11]
        ]);

        // Set chart options
        var options = {
            isStacked: true,
            width: window.innerWidth - 200,
            height: 300,
            chart: {},
            vAxis: {
                viewWindow: {
                    min: 0,
                    max: 500
                }
            },
            series: {
                0: {targetAxisIndex: 0},
                1: {targetAxisIndex: 1},
                2: {targetAxisIndex: 2},
                3: {targetAxisIndex: 1},
                4: {targetAxisIndex: 2},
            },
            vAxes: {
                0: {
                    title:'Minutes',
                    textStyle: {color: 'gray'},
                    titleTextStyle: {color: 'gray'},
                },
                1: {
                    title:'',
                    textStyle: {color: 'gray'},
                    titleTextStyle: {color: 'gray'}
                },
                2: {
                    title:'title',
                    textStyle: {color: 'white'},
                    titleTextStyle: {color: 'white'}
                }
            }

        };

        // Instantiate and draw our chart, passing in some options.
        var chart = new google.charts.Bar(document.getElementById('chart_div'));
        chart.draw(data, google.charts.Bar.convertOptions(options));
    };

</script>

<%@include file="/WEB-INF/assets/footer.jsp"%>

