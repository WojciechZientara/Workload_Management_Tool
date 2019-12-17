// <%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

    $(function () {
       var ajaxButtons = $('.ajax');
       ajaxButtons.on('click', function () {
           console.log($(this));
           clickButton($(this).data('type'), $(this).data('action'));
       });
});

function clickButton(type, action) {
    $.ajax({
        url: "http://localhost:8080" + action,
        data: {},
        type: "GET",
        dataType: "json"
    }).done(function(result) {
        console.log(result)
        if (result.type === 'assignTask') {
            assignTask(result.taskId, result.userName, result.dropdownTasks);
        } else if (result.type === 'unassignTask') {
            unassignTask(result.taskId, result.dropdownTasks);
        } else if (result.type === 'newAdHoc') {
            newAdHoc(result.redirect)
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
    console.log("UNASSIGN")
    var clickedButton = $(".ajax").filter('[data-taskid="' + taskId + '"]')[0];
    var td = $(clickedButton).parent()
    td.empty();
    $(td).html('<a class="btn ajax" data-taskid="' + taskId + '" data-type="assignTask" data-action="${pageContext.request.contextPath}/app/userPanel/assignTask/' + taskId + '">Rezerwuj</a>')
    $(td).find('a').on('click', function () {
        clickButton($(this).data('type'), $(this).data('action'));
    })
    refreshDropdown(dropdownTasks);

}

function newAdHoc(redirect) {
    var address = $('#newAdHoc').data('action');
    if (redirect === true) {
        var href = "http://localhost:8080" + address;
        href.replace("/createAdHoc", "/createAdHoc");
        console.log(href);
        window.location.href = href;
    }
}

function refreshDropdown(dropdownTasks) {
    var dropdown = $('#task')
    $(dropdown).children().remove()
    dropdownTasks.forEach(function (task) {
        $(dropdown).append($('<option value="' + task[0] + '">' + task[1] + '</option>'))
    })
    console.log($(dropdown).val())
}