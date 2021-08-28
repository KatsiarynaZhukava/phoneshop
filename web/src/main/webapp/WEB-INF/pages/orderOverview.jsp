<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<tags:master pageTitle="Order overview">
  <link rel="stylesheet" href="<c:url value="/resources/styles/orderOverview.css"/>">
  <div class="header-container">
    <h3>Thank you for your order</h3>
    <h5>Order number: ${order.id}</h5>
  </div>

  <div class="container">
    <h4 class="message">${orderErrorMessage}</h4>
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
          <td>$ ${item.phone.price}</td>
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
      <a href="<c:url value="/productList"/>">
        <button class="btn btn-outline-primary">Back to shopping</button>
      </a>
    </div>
  </div>
</tags:master>