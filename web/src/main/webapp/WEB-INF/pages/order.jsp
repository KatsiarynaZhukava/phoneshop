<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<tags:master pageTitle="Order">
  <link rel="stylesheet" href="<c:url value="/resources/styles/order.css"/>">
  <div class="header-container">
    <div class="float-left">
      <a href="<c:url value="/cart"/>">
        <button class="btn btn-outline-primary">Back to cart</button>
      </a>
    </div>
  </div>

  <div class="container">
    <h4 class="message">${orderErrorMessage}</h4>
    <table class="table">
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
      <form:form id="userInfoForm" method="post" action="${pageContext.request.contextPath}/order" modelAttribute="userInputDto" cssClass="user-info-form">
          <tags:userFormField fieldName="firstName" labelValue="First name*"/>
          <tags:userFormField fieldName="lastName" labelValue="Last name*"/>
          <tags:userFormField fieldName="deliveryAddress" labelValue="Address*"/>
          <tags:userFormField fieldName="contactPhoneNo" labelValue="Phone*"/>
          <div class="form-group row">
              <form:textarea autocomplete="off"  path="additionalInfo" placeholder="Additional information" class="form-control textarea-form-field"/>
          </div>
          <div class="form-group row">
              <button type="submit" class="btn btn-primary">Order</button>
          </div>
      </form:form>
  </div>
</tags:master>