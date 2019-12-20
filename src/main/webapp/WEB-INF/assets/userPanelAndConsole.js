<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

    $(function () {

    setActivateButton()
    $('#task').on("change", function () {
        setActivateButton()
    })
    var ajaxButtons = $('.ajax');
    ajaxButtons.on('click', function () {
        clickButton($(this).data('type'), $(this).data('action'));
    });

});

function setActivateButton() {
    var dropdown = $('#task').get(0);
    var selectedVal = dropdown.value;
    if (selectedVal === "") {
        selectedVal = 0;
    }
    var activateBtn = $('#activateTask').get(0)
    $(activateBtn).attr("data-action", "${pageContext.request.contextPath}" +
        "/app/console/activateTask/" + selectedVal)

}

function resetActivateButton() {
    var activateBtn = $('#activateTask').get(0)
    $(activateBtn).attr("data-action", "${pageContext.request.contextPath}" +
        "/app/console/activateTask/0")
}

function clickButton(type, action) {
    $.ajax({
        url: "http://localhost:8080" + action,
        data: {},
        type: "GET",
        dataType: "json"
    }).done(function(result) {
        if (result.type === 'assignTask') {
            assignTask(result.taskId, result.userName, result.dropdownTasks);
        } else if (result.type === 'unassignTask') {
            unassignTask(result.taskId, result.dropdownTasks);
        } else if (result.type === 'startWork') {
            startWork(result.startTime)
        } else if (result.type === 'endWork') {
            endWork(result.endTime)
        } else if (result.type === 'activateTask') {
            activateTask(result.taskName)
        } else if (result.type === 'stop') {
            stopTask()
        } else if (result.type === 'finish') {
            finishTask(result.taskId, result.taskName)
        }
    });
}

function assignTask(taskId, userName, dropdownTasks) {
    var clickedButton = $(".ajax").filter('[data-taskid="' + taskId + '"]')[0];
    var td = $(clickedButton).parent()
    td.empty();
    $(td).html(userName + ' ' + '<a class="btn ajax" data-taskid="' + taskId + '" data-type="unassignTask" data-action="${pageContext.request.contextPath}/app/userPanel/unassignTask/' + taskId + '">Anuluj</a>')
    $(td).find('a').on('click', function () {
        clickButton($(this).data('type'), $(this).data('action'));
    })
    refreshDropdown(dropdownTasks);

}

function unassignTask(taskId, dropdownTasks) {
    var clickedButton = $(".ajax").filter('[data-taskid="' + taskId + '"]')[0];
    var td = $(clickedButton).parent()
    td.empty();
    $(td).html('<a class="btn ajax" data-taskid="' + taskId + '" data-type="assignTask" data-action="${pageContext.request.contextPath}/app/userPanel/assignTask/' + taskId + '">Rezerwuj</a>')
    $(td).find('a').on('click', function () {
        clickButton($(this).data('type'), $(this).data('action'));
    })
    refreshDropdown(dropdownTasks);

}

function startWork(startTime) {
    var startTimeSpan = $('#startTime');
    $(startTimeSpan).text(startTime);
    var worktimeButtons = $('.worktime');
    $(worktimeButtons).toggle();
    activateTask("Inactive")

    var ahHocButtons = $('.adHoc');
    $(ahHocButtons).toggle();
}

function endWork(endTime) {
    var endTimeSpan = $('#endTime');
    $(endTimeSpan).text(endTime);
    var worktimeButtons = $('.worktime');
    $(worktimeButtons).toggle();
    activateTask("");

    var ahHocButtons = $('.adHoc');
    $(ahHocButtons).toggle();
}

function activateTask(taskName) {
    var activeTaskLabel = $('#activeTaskLabel');
    $(activeTaskLabel).text(taskName);
    var activateTask = $('#activateTask');
    $(activateTask).remove();
    var activateBtnTd = $('#activateBtnTd')
    $(activateBtnTd).append($('<button id="activateTask" class="mainBtn ajax" data-type="activateTask" data-action="${pageContext.request.contextPath}/app/console/activateTask/0">Aktywuj</button>'));
    $('#activateTask').on('click', function () {
        clickButton($(this).data('type'), $(this).data('action'));
    });
}

function stopTask() {
    var activeTaskLabel = $('#activeTaskLabel');
    $(activeTaskLabel).text("Bezczynność");
    setActivateButton()
}

function finishTask(taskId, taskName) {
    stopTask();
    var option = $('#task option[value="' + taskId +  '"]')
    $(option).remove();
    setActivateButton()

    var IdTr = $("td").filter(function() {
        return ( $(this).text() == taskId && $(this).nextAll().eq(1).text() == taskName )
    }).closest("tr");
    $(IdTr).remove()

}

function refreshDropdown(dropdownTasks) {
    var dropdown = $('#task')
    $(dropdown).children().remove()
    dropdownTasks.forEach(function (task) {
        $(dropdown).append($('<option value="' + task[0] + '">' + task[1] + '</option>'))
    })
    setActivateButton()
}

