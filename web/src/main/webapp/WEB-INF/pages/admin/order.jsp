<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<tags:master pageTitle="Order ${order.id}">
  <link rel="stylesheet" href="<c:url value="/resources/styles/adminOrder.css"/>">
  <div class="header-container">
      <tags:loginLinks/>
    <div class="flex-container">
      <h5>Order number: ${order.id}</h5>
      <p class="message">${errorMessage}</p>
      <h5>Order status: ${order.status}</h5>
    </div>
  </div>

  <div class="container">
    <table class="table order-table">
      <thead>
        <tr>
          <td>Model</td>
          <td>Color</td>
          <td>Brand</td>
          <td>Display size</td>
          <td>Quantity</td>
          <td>Price</td>
        </tr>
      </thead>
      <tbody>
      <c:forEach var="item" items="${order.orderItems}">
        <tr>
          <td>${item.phone.brand}</td>
          <td>${item.phone.model}</td>
          <td>
            <c:forEach var="color" items="${item.phone.colors}">
              <p>${color.code}</p>
            </c:forEach>
          </td>
          <td>${item.phone.displaySizeInches} ″</td>
          <td>${item.quantity}</td>
          <td>$ ${item.purchaseTimePrice}</td>
        </tr>
      </c:forEach>
      <tr>
        <td rowspan="3" colspan="4"></td>
        <td>Subtotal</td>
        <td>$ ${order.subtotal}</td>
      </tr>
      <tr>
        <td>Delivery</td>
        <td>$ ${order.deliveryPrice}</td>
      </tr>
      <tr>
        <td>TOTAL</td>
        <td>$ ${order.subtotal + order.deliveryPrice}</td>
      </tr>
      </tbody>
    </table>

    <table class="client-info-table">
      <tr>
        <td>First name</td>
        <td>${order.firstName}</td>
      </tr>
      <tr>
        <td>Last name</td>
        <td>${order.lastName}</td>
      </tr>
      <tr>
        <td>Address</td>
        <td>${order.deliveryAddress}</td>
      </tr>
      <tr>
        <td>Phone</td>
        <td>${order.contactPhoneNo}</td>
      </tr>
      <tr>
        <td colspan="2">
          ${order.additionalInfo}
        </td>
      </tr>
    </table>

      <div class="float-left">
        <a href="<c:url value="/admin/orders"/>">
          <button class="btn btn-primary control-button">Back</button>
        </a>
          <button type="submit" form="updateStatusForm" name="orderStatus" value="DELIVERED" class="btn btn-primary control-button" style="display: ${(order.status eq 'REJECTED') or (order.status eq 'DELIVERED') ? "none" : "inline-block"}">
            Delivered
          </button>
          <button type="submit" form="updateStatusForm" name="orderStatus" value="REJECTED" class="btn btn-primary control-button" style="display: ${(order.status eq 'REJECTED') or (order.status eq 'DELIVERED') ? "none" : "inline-block"}">
            Rejected
          </button>
          <form id="updateStatusForm" method="post" action="<c:url value="/admin/orders/${order.id}"/>">
            <security:csrfInput/>
          </form>
      </div>
  </div>
</tags:master>