<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${message}</title>
<!-- Bootstrap Core CSS -->
<link href="css/bootstrap.min.css" rel="stylesheet">

<!-- MetisMenu CSS -->
<link href="css/metisMenu.min.css" rel="stylesheet">

<!-- Timeline CSS -->
<link href="css/timeline.css" rel="stylesheet">

<!-- Custom CSS -->
<link href="css/sb-admin-2.css" rel="stylesheet">

<!-- Morris Charts CSS -->
<link href="css/morris.css" rel="stylesheet">

<!-- Custom Fonts -->
<link href="css/font-awesome.min.css" rel="stylesheet" type="text/css">

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
<script src="js/jquery-2.1.4.min.js"></script>
<script src="js/createProjectController.js"></script>
</head>

<style>
* {
	-webkit-box-sizing: border-box;
	-moz-box-sizing: border-box;
	box-sizing: border-box;
}

body {
	font-family: sans-serif;
}

/* ---- button ---- */
.button {
	display: inline-block;
	padding: 0.5em 1.0em;
	background: #EEE;
	border: none;
	border-radius: 7px;
	background-image: linear-gradient(to bottom, hsla(0, 0%, 0%, 0),
		hsla(0, 0%, 0%, 0.2));
	color: #222;
	font-family: sans-serif;
	font-size: 16px;
	text-shadow: 0 1px white;
	cursor: pointer;
}

.button:hover {
	background-color: #8CF;
	text-shadow: 0 1px hsla(0, 0%, 100%, 0.5);
	color: #222;
}

.button:active, .button.is-checked {
	background-color: #28F;
}

.button.is-checked {
	color: white;
	text-shadow: 0 -1px hsla(0, 0%, 0%, 0.8);
}

.button:active {
	box-shadow: inset 0 1px 10px hsla(0, 0%, 0%, 0.8);
}

/* ---- button-group ---- */
.button-group:after {
	content: '';
	display: block;
	clear: both;
}

.button-group .button {
	float: left;
	border-radius: 0;
	margin-left: 0;
	margin-right: 1px;
}

.button-group .button:first-child {
	border-radius: 0.5em 0 0 0.5em;
}

.button-group .button:last-child {
	border-radius: 0 0.5em 0.5em 0;
}

/* ---- isotope ---- */
.isotope {
	/* border: 1px solid #333; */
	
}

/* clear fix */
.isotope:after {
	content: '';
	display: block;
	clear: both;
}

/* ---- .element-item ---- */
.element-item {
	position: relative;
	float: left;
	width: 300px;
	height: 90px;
	margin: 5px;
	padding: 10px;
	background: #778899;
	color: #262524;
}

.element-item>* {
	margin: 0;
	padding: 0;
}

.element-item .name {
	position: absolute;
	right: 10px;
	top: 60px;
	text-transform: none;
	letter-spacing: 0;
	font-size: 12px;
	font-weight: normal;
}

.link {
	left: 3px;
	top: 55px;
	text-transform: none;
	letter-spacing: 0;
	font-size: 18px;
	position: inherit;
}

.element-item .symbol {
	position: absolute;
	left: 10px;
	top: 0px;
	font-size: 30px;
	font-weight: Italic;
	color: white;
}

.element-item .number {
	position: absolute;
	right: 5px;
	top: 5px;
	color: white
}

.element-item .weight {
	position: absolute;
	left: 10px;
	top: 70px;
	font-size: 12px;
}

.element-item.transition {
	background: #0F8;
	background: hsl(200, 31%, 53%);
}

.details {
	margin-top: 30px;
	text-align: left;
	color: #FFFFFF;
	font-family: #FFFFFF;
	font: italic;
}
</style>

<body bgcolor="#FFFFFF ">

	<nav class="navbar navbar-default navbar-static-top" role="navigation"
		style="margin-bottom: 0; background-color: #F0F8FF">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse"
				data-target=".navbar-collapse">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="login.html">Inferneon 1.0</a>
		</div>
		<!-- /.navbar-header -->

		<ul class="nav navbar-top-links navbar-right">
			<li class="dropdown">
				<!-- /.dropdown-messages -->
			</li>
			<!-- /.dropdown -->


			<li class="dropdown"><a class="dropdown-toggle"
				data-toggle="dropdown" href="#"> <i class="fa fa-tasks fa-fw"></i>
					<i class="fa fa-caret-down"></i>
			</a>

				<ul class="dropdown-menu dropdown-tasks">
					<li><a href="#">
							<div>
								<p>
									<strong>Task 1</strong> <span class="pull-right text-muted">40%
										Complete</span>
								</p>
								<div class="progress progress-striped active">
									<div class="progress-bar progress-bar-success"
										role="progressbar" aria-valuenow="40" aria-valuemin="0"
										aria-valuemax="100" style="width: 40%">
										<span class="sr-only">40% Complete (success)</span>
									</div>
								</div>
							</div>
					</a></li>
					<li class="divider"></li>
					<li><a href="#">
							<div>
								<p>
									<strong>Task 2</strong> <span class="pull-right text-muted">20%
										Complete</span>
								</p>
								<div class="progress progress-striped active">
									<div class="progress-bar progress-bar-info" role="progressbar"
										aria-valuenow="20" aria-valuemin="0" aria-valuemax="100"
										style="width: 20%">
										<span class="sr-only">20% Complete</span>
									</div>
								</div>
							</div>
					</a></li>
					<li class="divider"></li>
					<li><a href="#">
							<div>
								<p>
									<strong>Task 3</strong> <span class="pull-right text-muted">60%
										Complete</span>
								</p>
								<div class="progress progress-striped active">
									<div class="progress-bar progress-bar-warning"
										role="progressbar" aria-valuenow="60" aria-valuemin="0"
										aria-valuemax="100" style="width: 60%">
										<span class="sr-only">60% Complete (warning)</span>
									</div>
								</div>
							</div>
					</a></li>
					<li class="divider"></li>
					<li><a href="#">
							<div>
								<p>
									<strong>Task 4</strong> <span class="pull-right text-muted">80%
										Complete</span>
								</p>
								<div class="progress progress-striped active">
									<div class="progress-bar progress-bar-danger"
										role="progressbar" aria-valuenow="80" aria-valuemin="0"
										aria-valuemax="100" style="width: 80%">
										<span class="sr-only">80% Complete (danger)</span>
									</div>
								</div>
							</div>
					</a></li>
					<li class="divider"></li>
					<li><a class="text-center" href="#"> <strong>See
								All Tasks</strong> <i class="fa fa-angle-right"></i>
					</a></li>
				</ul> <!-- /.dropdown-tasks --></li>
			<!-- /.dropdown -->
			<li class="dropdown"><a class="dropdown-toggle"
				data-toggle="dropdown" href="#"> <i class="fa fa-bell fa-fw"></i>
					<i class="fa fa-caret-down"></i>
			</a>
				<ul class="dropdown-menu dropdown-alerts">
					<li><a href="#">
							<div>
								<i class="fa fa-comment fa-fw"></i> New Comment <span
									class="pull-right text-muted small">4 minutes ago</span>
							</div>
					</a></li>
					<li class="divider"></li>
					<li><a href="#">
							<div>
								<i class="fa fa-twitter fa-fw"></i> 3 New Followers <span
									class="pull-right text-muted small">12 minutes ago</span>
							</div>
					</a></li>
					<li class="divider"></li>
					<li><a href="#">
							<div>
								<i class="fa fa-envelope fa-fw"></i> Message Sent <span
									class="pull-right text-muted small">4 minutes ago</span>
							</div>
					</a></li>
					<li class="divider"></li>
					<li><a href="#">
							<div>
								<i class="fa fa-tasks fa-fw"></i> New Task <span
									class="pull-right text-muted small">4 minutes ago</span>
							</div>
					</a></li>
					<li class="divider"></li>
					<li><a href="#">
							<div>
								<i class="fa fa-upload fa-fw"></i> Server Rebooted <span
									class="pull-right text-muted small">4 minutes ago</span>
							</div>
					</a></li>
					<li class="divider"></li>

				</ul> <!-- /.dropdown-alerts --></li>
			<!-- /.dropdown -->
			<li class="dropdown">
				<!--  <i class="fa fa-user"></i> --> <a class="dropdown-toggle"
				data-toggle="dropdown" href="#"> <i class="fa fa-user fa-fw"></i>
					<i class="fa fa-caret-down"></i> <c:forEach items="${users}"
						var="results">

						<c:out value="${results[1]}" />
					</c:forEach>

			</a>
				<ul class="dropdown-menu dropdown-user">
					<li><a href="#"><i class="fa fa-user fa-fw"></i> User
							Profile</a></li>
					<li><a href="#"><i class="fa fa-gear fa-fw"></i> Settings</a>
					</li>
					<li class="divider"></li>
					<li><a href="login.html"><i class="fa fa-sign-out fa-fw"></i>
							Logout</a></li>
				</ul> <!-- /.dropdown-user -->
			</li>
			<!-- /.dropdown -->
		</ul>
		<!-- /.navbar-top-links -->

		<!-- /.sidebar-collapse -->
		</div>
		<!-- /.navbar-static-side -->
	</nav>

	<br></br>




	<table align="center">
		<tr>
		</tr>
		<tr>
			<td align="center">
				<button class="btn btn-primary btn-lg" data-toggle="modal"
					data-target="#myModal"
					style="height: 50px; width: 200px; text-align: centre; position: bottom">
					Create New Project</button>
			</td>
		</tr>

	</table>
	<br>
	</br>
	<br></br>

	<!-- <div name="mybutton" id="mybutton" class="button">
Add Project
</div> -->
	<script src="js/jquery.min.js"></script>

	<!-- Bootstrap Core JavaScript -->
	<script src="js/bootstrap.min.js"></script>

	<!-- Metis Menu Plugin JavaScript -->
	<script src="js/metisMenu.min.js"></script>

	<!-- Morris Charts JavaScript -->
	<script src="js/raphael-min.js"></script>
	<script src="js/morris.min.js"></script>
	<script src="../js/morris-data.js"></script>

	<!-- Custom Theme JavaScript -->
	<script src="js/sb-admin-2.js"></script>


	<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">

			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel">Project details</h4>
				</div>
				<form name="myform" method="post" action="addNewProjects.html">
					<div class="modal-body">

						<input placeholder='Project Name' type='text' name="project_name"
							required="required"> <br></br>


						<!-- <input type="text" name="projectName"><br> <br> -->
						<input id="btnAdd" type="button" value="Add Numeric" /> <input
							id="btn2Add" type="button" value="Add Nominal" /> <br /> <br />
						<div id="TextBoxContainer">
							<!--Textboxes will be added here -->
						</div>
						<br /> <input id="btnGet" type="button" value="Get Values" />

						<div class="modal-footer">
							<!-- <form id="projectForm"> -->
							<!--                                         	<input placeholder='Project Name' type='text' name="project_name">
 -->
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Close</button>
							<button type="submit" id="Save" class="btn btn-primary">Save</button>
							<!-- </form> -->
						</div>

					</div>
				</form>
			</div>
			<!-- /.modal-content -->

		</div>
		<!-- /.modal-dialog -->
	</div>

	<script type="text/javascript"
		src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
	<script type="text/javascript">
$(function () {
    $("#btnAdd").bind("click", function () {
        var div = $("<div />");
        div.html(GetDynamicTextBox(""));
        $("#TextBoxContainer").append(div);
    });
    $("#btn2Add").bind("click", function () {
        var div = $("<div />");
        div.html(GetDynamicButton(""));
        $("#TextBoxContainer").append(div);
    });
    $("#btnGet").bind("click", function () {
        var values = "";
        $("input[name=DynamicTextBox]").each(function () {
            values += $(this).val() + "\n";
        });
        alert(values);
    });
   $("body").on("click", ".remove", function () {
        $(this).closest("div").remove();
    });
});
var id=0;
function GetDynamicTextBox(value) {
	id++;
    return '<input name = "numeric'+id+'" type="text" value = "' + value + '" />&nbsp;' +
    '<input type="button" value="x" class="remove" />'
}
var i=0;
function GetDynamicButton(value) {
	i++;
    return  '<input name = "nominal'+i+'" type="text" value = "' + value + '" />&nbsp;' + '<input name = "attribute_value'+i+'" type="text" value = "' + value + '" />&nbsp;' +
    '<input type="button" value="x" class="remove" />'
}
</script>

	<div class="isotope" style="bottom: 0px; top: 200px" id="demo">
		<c:set var="count" value="0" scope="page" />
		<c:forEach items="${projects}" var="result" varStatus="loopStatus">
			<div class="element-item metalloid " data-category="transition">
				<p class="link">
					<a href="projectDetails.html?projectid=${result[0]}">View
						Details</a>
				</p>
				<c:set var="count" value="${count + 1}" scope="page" />
				<p class="number">
					<c:out value="${count}" />
				</p>
				<p class="symbol">

					<c:out value="${result[1]}" />
				</p>
				<p class="name">
					<c:out value="${result[2]}" />
				</p>
			</div>
		</c:forEach>
	</div>
</body>
</html>