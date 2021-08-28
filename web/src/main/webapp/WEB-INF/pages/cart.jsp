<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<tags:master pageTitle="Cart">
  <link rel="stylesheet" href="<c:url value="/resources/styles/cart.css"/>">
  <div class="header-container">
    <div class="float-left">
      <tags:cart totalQuantity="${cart.totalQuantity}" totalCost="${cart.totalCost}"/>
    </div>
    <div>
      <h4 id="updateMessage" class="message">${updateMessage}</h4>
    </div>
    <div class="float-right">
      <a href="<c:url value="/productList"/>">
        <button class="btn btn-outline-primary">Back to product list</button>
      </a>
    </div>
  </div>

  <div class="container">
    <c:choose>
      <c:when test="${empty detailedCart.items}">
        <p class="no-items-message">No items in your cart</p>
      </c:when>
      <c:otherwise>
        <table class="table table-hover">
          <thead>
            <tr>
              <td>Brand</td>
              <td>Model</td>
              <td>Color</td>
              <td>Display size</td>
              <td>Price</td>
              <td>Quantity</td>
              <td>Action</td>
            </tr>
          </thead>
          <tbody>
            <form:form id="updateCartForm" method="put" action="${pageContext.request.contextPath}/cart" modelAttribute="cartInputDto">
              <c:forEach var="item" items="${detailedCart.items}" varStatus="i">
              <tr id="cartRow${item.key.id}">
                <td>${item.key.brand}</td>
                <td><a href="<c:url value="/productDetails/${item.key.id}"/>">${item.key.model}</a></td>
                <td>
                  <c:forEach var="color" items="${item.key.colors}">
                    <p>${color.code}</p>
                  </c:forEach>
                </td>
                <td>${item.key.displaySizeInches} ″</td>
                <td>$ ${item.key.price}</td>
                <td>
                  <form:input path="items[${i.index}].requestedQuantity" autocomplete="off" class="form-control"/>
                  <form:hidden path="items[${i.index}].phoneId"/>
                  <form:errors class="message" path="items[${i.index}].requestedQuantity"/>
                </td>
                <td>
                  <button form="deleteCartItemForm" formaction="${pageContext.request.contextPath}/cart/${item.key.id}" type="submit" class="btn btn-primary">
                    Delete
                  </button>
                </td>
              </tr>
              </c:forEach>
            </form:form>
          </tbody>
        </table>
      <form id="deleteCartItemForm" method="post">
        <input type="hidden" name="_method" value="delete">
      </form>
      <div id="cartButtonsContainer" class="float-right action-buttons-container">
          <div>
            <button form="updateCartForm" type="submit" class="btn btn-primary">Update</button>
          </div>
          <div>
            <a href="<c:url value="/order"/>">
              <button class="btn btn-primary">Order</button>
            </a>
          </div>
        </div>
      </c:otherwise>
    </c:choose>
  </div>
</tags:master>