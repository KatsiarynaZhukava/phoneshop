<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<body>
  <div class="float-left right">
    <button id="cartBtn" class="btn btn-primary">
      My cart: ${cart.totalQuantity} items $
      <c:choose>
        <c:when test="${cart.totalCost != null}">
          ${cart.totalCost}
        </c:when>
        <c:otherwise>
          0
        </c:otherwise>
      </c:choose>
    </button>
  </div>
  <p>
      ${product.description}
  </p>
  <form method="post" action="${pageContext.servletContext.contextPath}/products/${product.id}">
    <table>
      <thead>
      <tr>
        <td>Image</td>
        <td><img src="${product.imageUrl}"></td>
      </tr>
      <tr>
        <td>Code</td>
        <td class="detail">${product.code}</td>
      </tr>
      <tr>
        <td>Price</td>
        <td class="price">
          <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
        </td>
      </tr>
      <tr>
        <td>Stock</td>
        <td class="detail">${product.stock}</td>
      </tr>
      <tr>
        <td>Quantity</td>
        <td>
          <input class="quantity" name="quantity" value="${not empty error ? param.quantity : 1}">
          <c:if test="${not empty error}">
            <p class="error">
              ${error}
            </p>
          </c:if>
          <input type="hidden" name="productId" value="${product.id}"/>
        </td>
      </tr>
    </table>
    <div class="add-button">
      <button>Add to cart</button>
    </div>
  </form>
</body>