<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style>
body {
     background-color: #B3B3B3; 
 }
table{
font: 100;
}
.isotope {
 border: 1px solid #333; 
 padding: 10px 10px 10px 10px;
 margin-left: 10px;
 margin-bottom: 10px;
 position: relative;
 top: 160px;
 left:50px;
}

</style>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<h1>values display here</h1>
	<table border=2px>
	<div class="element-item metalloid " data-category="transition">
	
		<tr>
			<td>id</td>
			<td>Activity Name</td>
			<td>Run Date</td>
		</tr>
		<c:forEach items="${activiti}" var="result">
			<div class="element-item metalloid " data-category="transition">
				<tr>
					<td><c:out value="${result[0]}" />
						<p class="symbol"></td>

					<td><c:out value="${result[1]}" />
						</td>
					<td><p class="name">
							<c:out value="${result[2]}" />
						</p></td>
				</tr>
			</div>
		</c:forEach>
		</div>
	</table>
	<div style="width: 50%; float:right;" align="center">
	
	<center>
		<br />
		<form:form method="post" enctype="multipart/form-data"
			modelAttribute="uploadedFile" action="fileUpload.html">
			<table>
				<tr>
					<td>Upload File:&nbsp;</td>
					<td><input type="file" name="file" /></td>
					<td style="color: red; font-style: italic;"><form:errors
							path="file" /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td><input type="submit" value="Upload" /></td>
					<td>&nbsp;</td>
				</tr>
			</table>
		</form:form>
	</center>
	</div>
</body>
</html>
