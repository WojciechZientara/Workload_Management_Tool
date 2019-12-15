<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<%@include file="/WEB-INF/assets/header.jsp"%>
<%@include file="/WEB-INF/assets/menu.jsp"%>



<div id="main" class="mainFrame">

    <div id="chart_div" style="margin: 30px;">
        <%--chart--%>
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
        <tbody id="table_body">
            <%--Json data--%>
        </tbody>
    </table>
    <br>
</div>

<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
<script><%@include file="../../assets/dashboard.js"%></script>

<%@include file="/WEB-INF/assets/footer.jsp"%>

