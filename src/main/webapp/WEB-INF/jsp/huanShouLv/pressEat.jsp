<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html>
<head>
    <title>Cow</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="//cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="//cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap-theme.min.css"/>
    <script src="//cdn.bootcss.com/jquery/1.11.3/jquery.min.js"></script>
    <script src="//cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
    <link href="/js/pagination/pagination.css" rel="stylesheet"/>
    <script src="/js/pagination/jquery.pagination.js" ></script>
</head>
<body>


<div class="container-fluid">
  <%--  <ul class="nav nav-pills">
        <li role="presentation" class="active"><a href="#">Home</a></li>
        <li role="presentation"><a href="#">Profile</a></li>
        <li role="presentation"><a href="#">Messages</a></li>
    </ul>--%>
    <form class="navbar-form navbar-left" role="search" action="press">
        <div class="form-group">
            <input type="text" name="date" class="form-control" value="2016-06-02" placeholder="Date">
        </div>
        <div class="form-group">
            <input type="text" class="form-control" placeholder="Search">
        </div>
        <button type="submit" class="btn btn-default">Submit</button>
    </form>

    <div class="row-fluid">
        <div class="span12">
            <form id="pagerForm" action="">
                <input type="hidden" id="_pageNum" name="page" value="${pageList.number+1}" />
                <input type="hidden" id="_pageSize" name="size" value="${pageList.size}" />
                <input type="hidden" id="date" name="date" value="${param.date}" />
            </form>
            <table class="table table-condensed">
                <thead>
                <tr>
                    <th>时间</th>
                    <th>股票</th>
                    <th>收盘价</th>
                    <th>DDX</th>
                    <th>DDY</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${pageList.content}" var="item">
                <tr >
                    <td><fmt:formatDate value="${item.date}" pattern="yyyy-MM-dd"></fmt:formatDate></td>
                    <td >${item.stock.id}</td>
                    <td >${item.stock.name}</td>
                    <td >${item.ddx}</td>
                    <td >${item.ddy}</td>
                   <td ><fmt:formatNumber  type="percent" value="${(item.greater[0].close - item.stockDay.close )/item.stockDay.close}"></fmt:formatNumber>
                        <br/>
                        <fmt:formatDate value="${item.greater[0].date}" pattern="yyyy-MM-dd"></fmt:formatDate>
                    </td>
                    <td ><fmt:formatNumber  type="percent" value="${(item.greater[2].close - item.stockDay.close )/item.stockDay.close}"></fmt:formatNumber>
                        <br/>
                        <fmt:formatDate value="${item.greater[2].date}" pattern="yyyy-MM-dd"></fmt:formatDate>
                    </td>
                    <td ><fmt:formatNumber  type="percent" value="${(item.greater[4].close - item.stockDay.close )/item.stockDay.close}"></fmt:formatNumber>
                        <br/>
                        <fmt:formatDate value="${item.greater[4].date}" pattern="yyyy-MM-dd"></fmt:formatDate>
                    </td>
                </tr>
                </c:forEach>
                </tbody>
            </table>

            <c:import url="../common/pageBar.jsp"></c:import>
        </div>
    </div>
</div>

</body>
<script>
    $(".btn-info").bind("click", function () {
        window.location = 'delete?id=' + $(this).attr("id");
    });
</script>
</html>