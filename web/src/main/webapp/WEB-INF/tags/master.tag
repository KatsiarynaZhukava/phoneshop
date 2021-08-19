<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag trimDirectiveWhitespaces="true" pageEncoding="utf-8" %>
<%@ attribute name="pageTitle" required="true" %>

<html>
    <head>
        <title>${pageTitle}</title>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
        <link rel="stylesheet" href="<c:url value="/resources/styles/common.css"/>">
        <script>let contextPath = "${pageContext.request.contextPath}"</script>
        <script src="<c:url value="/resources/scripts/cartOperations.js"/>"></script>
    </head>
    <body>
        <main>
            <jsp:doBody/>
        </main>
    </body>
</html>