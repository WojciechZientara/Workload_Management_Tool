<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

    $(function () {
        displayDashboard()
        var interval = setInterval(function (){
            displayDashboard();
        }, 60 * 1000);
});

function displayDashboard() {
    $.ajax({
        url: "http://localhost:8080/WMT_war_exploded/app/activities/getData",
        data: {},
        type: "GET",
        dataType: "json"
    }).done(function(result) {
        googleChartdisplay(result);
        fillTable(result.allActivities)
    });
}


function googleChartdisplay(result) {

    google.load('visualization', 'current', { 'packages': ['bar'] });
    google.setOnLoadCallback(drawStuff);

    function drawStuff() {

        // 1)columns generation
        var data = new google.visualization.DataTable();
        data.addColumn('string', '');
        data.addColumn('number', 'Czas pracy');

        result.assignedTasks.forEach(function (task) {
            if (task.client !== null) {
                data.addColumn('number', task.client.name + " - " + task.name);
                data.addColumn('number', task.client.name + " - " + task.name);
            } else {
                data.addColumn('number', task.name);
                data.addColumn('number', task.name);
            }
        })

        // 2)rows population
        var rows = []
        result.timesMatrix.forEach(function (user) {
            var line = []
            user.forEach(function (record) {
                line.push(record)
            })
            rows.push(line)
        })
        data.addRows(rows)

        // 3) assigning data to particular columns in the chart
        var axis = 1;
        var counter = 1;
        var series = {0: {targetAxisIndex: 0, color: 'lightblue'}};

        // 3.1 actuals
        result.assignedTasks.forEach(function (task) {
            var color = "silver";
            if (task.description === 'Active') {
                color = "gold"
            } else if (task.completed === true || task.name === 'Bezczynność') {
                color = "forestgreen"
            }

            series[counter] = {targetAxisIndex: axis, color: color};
            counter++;
            if (axis === 1) {
                axis = 2;
            } else {
                axis = 1;
            }
        })

        // 3.2 estimated
        result.assignedTasks.forEach(function (task) {
            series[counter] = {targetAxisIndex: axis, color:'forestgreen'};
            counter++;
            if (axis === 1) {
                axis = 2;
            } else {
                axis = 1;
            }
        })

        // 4) chart options
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

            series: series,
            vAxes: {
                0: {title:'Minuty', textStyle: {color: 'gray'}, titleTextStyle: {color: 'gray'},},
                1: {title:'', textStyle: {color: 'gray'}, titleTextStyle: {color: 'gray'}},
                2: {title:'', textStyle: {color: 'white'}, titleTextStyle: {color: 'white'} }
            }

        };

        // 5) clear div and generate new chart
        $('#chart_div').clear
        var chart = new google.charts.Bar(document.getElementById('chart_div'));
        chart.draw(data, google.charts.Bar.convertOptions(options));
    };
}


function fillTable(result) {
    $('#table_body').empty();
    result.forEach(function (activity) {

        var newTr;
        if (activity[3].description === 'Active') {
            newTr = $("<tr style='text-shadow: -1px 0 gold, 0 1px gold,1px 0 gold, 0 -1px gold'>");
        } else if (activity[3].completed === true) {
            newTr = $("<tr style='text-shadow: 0px 0 limegreen, 0 0px limegreen, 1px 0 limegreen, 0 -1px limegreen'>");
        } else {
            newTr = $("<tr>");
        }

        newTr.append($("<td>" + activity[0].firstName + " " + activity[0].lastName + "</td>"))

        if (activity[3].client !== null) {
            newTr.append($("<td>" + activity[3].client.name + "</td>"))
        } else {
            newTr.append($("<td>n/a</td>"))
        }

        if (activity[3].type !== null) {
            newTr.append($("<td>" + activity[3].type + "</td>"))
        } else {
            newTr.append($("<td>n/a</td>"))
        }

        if (activity[1] === 'Inactive') {
            newTr.append($("<td>" + "Bezczynność" + "</td>"))
        } else {
            newTr.append($("<td>" + activity[1] + "</td>"))
        }
        newTr.append($("<td>" + activity[2] + "</td>"))
        newTr.append($("<td>" + activity[3].estimatedDuration + "</td>"))

        $('#table_body').append(newTr)
    })
}
