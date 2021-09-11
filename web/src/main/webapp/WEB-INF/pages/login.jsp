<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Login">
  <link rel="stylesheet" href="<c:url value="/resources/styles/login.css"/>">
  <div class="login-form row justify-content-center">
    <form method="post">
      <div class="form-group">
        <label for="username">Username</label>
        <input name="username" id="username" class="form-control" autocomplete="off" readonly onfocus="this.removeAttribute('readonly');" />
      </div>
      <div class="form-group">
        <label for="password">Password</label>
        <input type="password" name="password" id="password" class="form-control" autocomplete="off" readonly onfocus="this.removeAttribute('readonly');" />
      </div>
      <security:csrfInput/>
      <c:if test="${not empty SPRING_SECURITY_LAST_EXCEPTION.message}">
        <div class="message">
          <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>
        </div>
      </c:if>
      <div class="text-center mt-4">
        <button type="submit" class="btn btn-primary login-button">Log in</button>
      </div>
    </form>
  </div>
</tags:master>