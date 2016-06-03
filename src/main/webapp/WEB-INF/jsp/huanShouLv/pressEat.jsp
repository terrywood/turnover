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
    <div class="row-fluid">
        <div class="span12">
            <form id="pagerForm" action="">
                <input type="hidden" id="_pageNum" name="page" value="${pageList.number+1}" />
                <input type="hidden" id="_pageSize" name="size" value="${pageList.size}" />
            </form>
            <table class="table table-condensed">
                <thead>
                <tr>
                    <th>时间</th>
                    <th>股票</th>
                    <th>收盘价</th>
                    <th>+1</th>
                    <th>+3</th>
                    <th>+5</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${pageList.content}" var="item">
                <tr >
                    <td><fmt:formatDate value="${item.date}" pattern="yyyy-MM-dd"></fmt:formatDate></td>
                    <td >${item.code}</td>
                    <td >${item.close}</td>
                    <td ><fmt:formatNumber  type="percent" value="${(item.greater[0].close - item.close )/item.close}"></fmt:formatNumber>
                        <br/>
                        <fmt:formatDate value="${item.greater[0].date}" pattern="yyyy-MM-dd"></fmt:formatDate>
                    </td>
                    <td ><fmt:formatNumber  type="percent" value="${(item.greater[2].close - item.close )/item.close}"></fmt:formatNumber>
                        <br/>
                        <fmt:formatDate value="${item.greater[2].date}" pattern="yyyy-MM-dd"></fmt:formatDate>
                    </td>
                    <td ><fmt:formatNumber  type="percent" value="${(item.greater[4].close - item.close )/item.close}"></fmt:formatNumber>
                        <br/>
                        <fmt:formatDate value="${item.greater[4].date}" pattern="yyyy-MM-dd"></fmt:formatDate>
                    </td>
                </tr>
                </c:forEach>
                </tbody>
            </table>
           <%-- <ul class="pagination">
                <li><a href="#">&laquo;</a></li>
                <li><a href="#">1</a></li>
                <li><a href="#">2</a></li>
                <li><a href="#">3</a></li>
                <li><a href="#">4</a></li>
                <li><a href="#">5</a></li>
                <li><a href="#">&raquo;</a></li>
            </ul>
         --%>
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