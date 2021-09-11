<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="login-header row justify-content-end">
  <div class="px-2">
    <security:authorize access="isAuthenticated()">
      <security:authentication property="principal.username"/>
    </security:authorize>
  </div>
  <div class="px-2">
    <security:authorize access="hasRole('ROLE_ADMIN')">
      <a href="<c:url value="/admin/orders"/>">Admin</a>
    </security:authorize>
  </div>
  <div class="px-2">
    <security:authorize access="isAuthenticated()">
      <form id="logoutForm" method="post" action="<c:url value="/logout"/>">
        <security:csrfInput/>
        <a href="javascript:{}" onclick="document.getElementById('logoutForm').submit(); return false;">Logout</a>
      </form>
    </security:authorize>
    <security:authorize access="!isAuthenticated()">
      <a href="<c:url value="/login"/>">Login</a>
    </security:authorize>
  </div>
</div>