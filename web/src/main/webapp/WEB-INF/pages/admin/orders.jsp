<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<tags:master pageTitle="Orders">
  <div class="container">
    <table class="table table-hover">
      <thead>
      <tr>
          <td>Order number</td>
          <td>Customer</td>
          <td>Phone</td>
          <td>Address</td>
          <td>Date</td>
          <td>Total price</td>
          <td>Status</td>
      </tr>
      </thead>
      <tbody>
        <c:forEach var="order" items="${orders}">
          <tr>
            <td><a href="<c:url value="/admin/orders/${order.id}"/>">${order.id}</a></td>
            <td>${order.firstName} ${order.lastName}</td>
            <td>${order.contactPhoneNo}</td>
            <td>${order.deliveryAddress}</td>
              <fmt:parseDate value="${order.date}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDateTime" type="both" />
            <td><fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${ parsedDateTime }" /></td>
            <td>$ ${order.totalPrice}</td>
            <td>${order.status}</td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </div>
</tags:master>