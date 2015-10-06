
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html>
<head>
<title>Registration Here</title>
<style>
.error {
	color: #ff0000;
	font-weight: bold;
}
</style>
</head>
<body>
	<h2>
		<spring:message code="lbl.page" text="Register Here" />
	</h2>
	<br />
	<form:form name="myform" method="post" action="addNewProjects.html">
		<form:errors path="*" cssClass="error" />
		<table>
			<tr>
				<th>Project Name</th>
				<td><input type="text" name="project_name"
					placeholder="project_name" /></td>
			</tr>
			<tr>
				<td><div class="modal-body">
						<input id="btnAdd" type="button" value="Add Numeric" /> <input
							id="btn2Add" type="button" value="Add Nominal" /> <br /> <br />
						<div id="TextBoxContainer">
							<!--Textboxes will be added here -->
						</div>
					</div></td>
			</tr>
			<tr>
				<td colspan="3"><input type="submit" value="Add Project" /></td>
			</tr>
		</table>
	</form:form>

	<script type="text/javascript"
		src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
	<script type="text/javascript">
		$(function() {
			$("#btnAdd").bind("click", function() {
				var div = $("<div />");
				div.html(GetDynamicTextBox(""));
				$("#TextBoxContainer").append(div);
			});
			$("#btn2Add").bind("click", function() {
				var div = $("<div />");
				div.html(GetDynamicButton(""));
				$("#TextBoxContainer").append(div);
			});
			$("#btnGet").bind("click", function() {
				var values = "";
				$("input[name=DynamicTextBox]").each(function() {
					values += $(this).val() + "\n";
				});
				alert(values);
			});
			$("body").on("click", ".remove", function() {
				$(this).closest("div").remove();
			});
		});
		var id = 0;
		function GetDynamicTextBox(value) {
			id++;
			return '<input name = "numeric'+id+'" id="Numeric" type="text" value = "' + value + '" />&nbsp;'
					+ '<input type="button" value="Remove" class="remove" />'
		}

		var i = 0;
		function GetDynamicButton(value) {
			i++;
			return '<input name = "nominal'+i+'" id="Nominal" type="text"  value = "' + value + '" />&nbsp;'
					+ '<input name = "attribute_value'+i+'" type="text" value = "' + value + '" />&nbsp;'
					+ '<input type="button" value="Remove" class="remove" />'

		}
	</script>

</body>
</html>
