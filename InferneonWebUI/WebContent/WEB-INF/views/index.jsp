<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta charset="UTF-8">
<title>Inferneon - Machine Learning With Analytics</title>
<link
	href='http://fonts.googleapis.com/css?family=Titillium+Web:400,300,600'
	rel='stylesheet' type='text/css'>

<link rel="stylesheet" href="css/normalize.css" />
<link type="text/css" rel="stylesheet" href="css/style.css" />

</head>
<body>
	<div class="form">

		<ul class="tab-group">
			<li class="tab active"><a href="#login">Log In</a></li>
			<li class="tab"><a href="#signup">Sign Up</a></li>
		</ul>

		<div class="tab-content">
			<div id="login">
				<h1>Welcome To Inferneon!</h1>

				<form:form modelAttribute="userlogin" method="post"
					action="login.html">

					<div class="field-wrap">
						<label> Email Address<span class="req">*</span>
						</label> <input type="email" name="email" value="email" required
							autocomplete="off" />
					</div>

					<div class="field-wrap">
						<label> Password<span class="req">*</span>
						</label> <input type="password" name="password" value="password" required
							autocomplete="off" />
					</div>

					<p class="forgot">
						<a href="#">Forgot Password?</a>
					</p>

					<button type="submit" class="button button-block" name="Login"
						value="Login">Log In</button>
				</form:form>
			</div>
			<div id="signup">
				<h1>Sign Up for Free</h1>

				<form:form modelAttribute="register" method="post"
					action="signup.html">

					<div class="top-row">
						<div class="field-wrap">
							<label> First Name<span class="req">*</span>
							</label> <input type="text" name="Firstname" required autocomplete="off" />
						</div>

						<div class="field-wrap">
							<label> Last Name<span class="req">*</span>
							</label> <input type="text" name="Lastname" required autocomplete="off" />
						</div>
					</div>

					<div class="field-wrap">
						<label> Email Address<span class="req">*</span>
						</label> <input type="email" name="Email" required autocomplete="off" />
					</div>

					<div class="field-wrap">
						<label> Set A Password<span class="req">*</span>
						</label> <input type="password" name="password" required
							autocomplete="off" />
					</div>

					<button type="submit" class="button button-block" name="SignUp"
						value="SignUp">Get Started</button>

				</form:form>
			</div>

		</div>
		<!-- tab-content -->
	</div>
	<!-- /form -->
	<script
		src='http://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script>
	<script src="js/index.js"></script>
</body>
</html>
