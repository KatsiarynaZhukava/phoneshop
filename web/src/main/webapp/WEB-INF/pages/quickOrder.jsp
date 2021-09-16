<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<tags:master pageTitle="Quick Order">
  <link rel="stylesheet" href="<c:url value="/resources/styles/quickOrder.css"/>">
  <div class="float-left">
    <div class="header-container">
      <tags:loginLinks/>
      <div class="flex-container">
        <tags:cart totalQuantity="${cart.totalQuantity}" totalCost="${cart.totalCost}"/>
        <div>
          <h5 class="message">${errorMessage}</h5>
          <h5 class="success-message">${successMessage}</h5>
        </div>
        <div class="float-right">
          <a href="<c:url value="/productList"/>">
            <button class="btn btn-outline-primary">Back to product list</button>
          </a>
        </div>
      </div>
    </div>
  </div>

  <div class="container quick-order-container">
    <table class="table table-hover">
      <thead>
        <tr>
          <td>Model</td>
          <td>Quantity</td>
        </tr>
      </thead>
      <tbody>
        <form:form method="post" action="${pageContext.request.contextPath}/quickOrder" modelAttribute="quickOrderInputDto">
          <c:forEach items="${quickOrderInputDto.items}" varStatus="i">
            <tr>
              <td>
                <form:input path="items[${i.index}].model" autocomplete="off" class="form-control"/>
                <form:errors class="message" path="items[${i.index}].model"/>
              </td>
              <td>
                <form:input path="items[${i.index}].requestedQuantity" autocomplete="off" class="form-control"/>
                <form:hidden path="items[${i.index}].rowNumber" value="${i.index}"/>
                <form:errors class="message" path="items[${i.index}].requestedQuantity"/>
              </td>
            </tr>
           </c:forEach>
            <tr>
              <td colspan="2"><button type="submit" class="btn btn-primary">Add to cart</button></td>
            </tr>
        </form:form>
      </tbody>
    </table>
  </div>
</tags:master>
