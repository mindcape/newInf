# Application intro and Installation Instructions #

The frontend is based on Angular, lodash and requirejs, and the backend is composed of JSON REST web services based on Spring MVC / JPA, secured with Spring Security. See a further description of the app on this.

### Dependencies ###

The following dependencies are necessary: 

 - Java 8
 - Node 0.12 or higher
 - bower
 - maven 3

### Installing frontend dependencies ###

	git clone <clone url>

After cloning the repository, the following command installs the Javascript dependencies:

    bower install
    
### Importing into eclipse ###

After installing the dependencies, import the project into eclipse as a maven project. 


### Building and starting the server ###

Start the server and deploy the app on tomcat. In eclipse go to servers.xml and modify the following line as follows:

<Context docBase="inferneonAng" path="/" reloadable="true" source="org.eclipse.jst.j2ee.server:inferneonAng"/>

Remove inferneonAng from the path as shown above.

After changing the above line, update the profiles on the server by going to:

Run -> Run Configurations 

    On the left side, server apache tomcat configuration is available 

Under Arguments add:

    -Dspring.profiles.active=test
 
The spring test profile will activate an in-memory database. After the server starts, the application is accessible at the following URL:

    http://localhost:8080/

To see a user with existing data , login with the following credentials:

    username: test123 / password: Password2
    
If you want to use development profile (a real mysql database, enter proper database details in DevelopmentConfiguration.java and change the profile on the server to:

    -Dspring.profiles.active=development




### Frontend Overview ###

This project is a web application with an AngularJs-based frontend and a Spring/Hibernate based backend. The application is responsive, as it adapts to different screen sizes.

On the frontend, these libraries where used (besides Angular):  [Yahoo PureCss](http://http://purecss.io/) (pure CSS baseline)  and [lodash](https://lodash.com/) for functional data manipulation. The module system [require.js](http://requirejs.org/) was used to load frontend dependencies. The dependencies where  obtained via [bower](http://bower.io/).

The angular module [angular-messages](https://egghead.io/lessons/angularjs-introduction-to-ng-messages-for-angularjs) was used for frontend form validation, and this [jQuery plugin](http://plugins.jquery.com/datetimepicker/) was used as the datetimepicker component. 

### Backend Overview ###

The backend is based on Java 8, Spring 4, JPA 2/ Hibernate 4. The Spring configuration is based on Java. The main Spring modules used where Spring MVC and Spring Security. The backend was built using the DDD approach, which includes a domain model, services, repositories and DTOs for frontend/backend data transfer. 

The REST web services are based on Spring MVC and JSON. The unit tests are made with spring test and the REST API functional tests where made using [Spring test MVC](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/testing.html#spring-mvc-test-framework).

#### Backend Security ####

The Spring Security module was used to secure the REST backend (these [guidelines](https://www.owasp.org/index.php/REST_Security_Cheat_Sheet) are in general applied). The application can be made to run in HTTPS-only mode via a server parameter, meaning no pages will be served if the user tries to access it via HTTP.

The Spring Security Form Login mode was used, with fallback to HTTP-Basic Authentication for non-browser based HTTP clients. Protection is in-place against CSRF ([cross-site request forgery](https://www.owasp.org/index.php/Cross-Site_Request_Forgery_%28CSRF%29)). 

Frontend validations are for user convenience only, and where also made on the backend. The use of Angular gives good protection against common problems like [cross-site scripting or HTML injection](https://docs.angularjs.org/misc/faq). The queries on the backend are made using either named queries or the criteria API, which gives good protection against SQL injection.

The password policy is of at least 6 characters with minimum one lower case, one upper case and one numeric. The passwords are not stored in the database in plain text but in a digested form, using the Spring Security [Bcrypt](http://docs.spring.io/autorepo/docs/spring-security/3.2.0.RELEASE/apidocs/org/springframework/security/crypto/bcrypt/BCryptPasswordEncoder.html) password encoder (transparently includes a salt).

#### REST API ####

The REST API of the backend is composed of 3 services:

##### Authentication Service #####

Url           |Verb          | Description
--------------|------------- | -------------
/authenticate |POST          | authenticates the user
/logout |POST          | ends the current session


##### User Service #####

Url           |Verb          | Description
--------------|------------- | -------------
/user         |GET          | retrieves info for the currently logged-in user (number of Projects of today, etc.) 
/user| PUT| Used to save the user max projects per day
/user|POST| creates a new user