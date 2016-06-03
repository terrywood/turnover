<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script type="text/javascript">
	$(function() {
		var opt = {
			callback: changePage,
			current_page:${pageList.number},
			items_per_page:${pageList.size},
			num_display_entries:10,
			num_edge_entries:2,
			prev_text:'PREV',
			next_text:'NEXT'
		};
		$("#paginate").pagination(${pageList.totalElements}, opt);
		
	});

	function changePage(page_index,jq) {
		var page = page_index+1;
		$("#_pageNum").attr('value', page);
		$("#pagerForm").submit();
		
	}
	
	function changePageSize(){
		var ps=$("#_pageSizeSel").val();
		$("#_pageSize").attr('value', ps);
		$("#pagerForm").submit();
	}

</script>
<div class="col-xs-12">
<div class="panelBar">
	<div class="pagination" style="float: right;">
	
		<ul id="paginate" class="pagination"></ul>
	</div>
	<div class="pager-total" style="float: left;">Total <span>${pageList.totalElements}</span> records | <span>
						<select id="_pageSizeSel" onchange="changePageSize()">
							<option value="5" <c:if test="${pageList.size eq 5}">selected</c:if>>5</option>														
							<option value="10" <c:if test="${pageList.size eq 10}">selected</c:if>>10</option>														
							<option value="20" <c:if test="${pageList.size eq 20}">selected</c:if>>20</option>														
							<option value="50" <c:if test="${pageList.size eq 50}">selected</c:if>>50</option>														
						</select>
						</span> records per page</div>
</div>
</div>