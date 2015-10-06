<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${message}</title>
    </head>
    <body>
        <h1>${message}</h1><br>
        <table>
            <tr>
            <th>Project Id</th>
                <th>project name</th>
                <th>date Created</th>
            </tr>
             <c:forEach items="${projects}" var="result">
            <tr>
                  <td><c:out value="${result[0]}"/></td>
                  <td><c:out value="${result[1]}"/></td>
                  <td><c:out value="${result[2]}"/></td>
            </tr>
            </c:forEach>
        </table>
    </body>
</html>