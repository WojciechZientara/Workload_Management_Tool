<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<html>
<head>
    <title>WMT</title>
    <style> <%@include file="/WEB-INF/assets/style.css"%> </style>
</head>
<body>
<div id="logo" class="mainFrame">
    <span>
       <c:choose>
           <c:when test="${sessionScope.logged == 'true'}">

               <c:choose>
                   <c:when test="${sessionScope.admin == 'true'}">
                       <a href="${pageContext.request.contextPath}/admin/dashboard">Workload Management Tool</a>
                   </c:when>
                   <c:otherwise>
                       <a href="${pageContext.request.contextPath}/app/userPanel">Workload Management Tool</a>
                   </c:otherwise>
               </c:choose>

           </c:when>
           <c:otherwise>
               <a href="${pageContext.request.contextPath}/login">Workload Management Tool</a>
           </c:otherwise>
       </c:choose>
    </span>
</div>
