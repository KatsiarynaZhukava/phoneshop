<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<head>
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
  <link rel="stylesheet" href="<c:url value="/resources/styles/productList.css"/>">
  <script>let contextPath = "${pageContext.request.contextPath}"</script>
  <script src="<c:url value="/resources/scripts/addToCart.js"/>"></script>
</head>
<body>
  <div class="header-container">
    <div class="float-left">
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

    <div class="float-right">
      <form action = "${pageContext.request.contextPath}/productList">
        <div class="row">
          <div class="col">
            <input name="searchQuery" value="${searchQuery}" class="form-control search-input">
          </div>
          <div class="col">
            <button type="submit" class="btn btn-outline-primary">Search</button>
          </div>
        </div>
      </form>
    </div>
  </div>

  <div class="content">
    <table class="table table-hover">
      <thead>
        <tr>
          <td>Image</td>
          <td>
            Brand
            <tags:sortLink sort="brand" order="asc"/>
            <tags:sortLink sort="brand" order="desc"/>
          </td>
          <td>
            Model
            <tags:sortLink sort="model" order="asc"/>
            <tags:sortLink sort="model" order="desc"/>
          </td>
          <td>Color</td>
          <td>
            Display size
            <tags:sortLink sort="displaySizeInches" order="asc"/>
            <tags:sortLink sort="displaySizeInches" order="desc"/>
          </td>
          <td>
            Price
            <tags:sortLink sort="price" order="asc"/>
            <tags:sortLink sort="price" order="desc"/>
          </td>
          <td>Quantity</td>
          <td>Action</td>
        </tr>
      </thead>
      <c:forEach var="phone" items="${phones}">
        <tr>
          <td>
            <img src="https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/${phone.imageUrl}">
          </td>
          <td>${phone.brand}</td>
          <td>${phone.model}</td>
          <td>
            <c:forEach var="color" items="${phone.colors}">
              <p>${color.code}</p>
            </c:forEach>
          </td>
          <td>${phone.displaySizeInches} ″</td>
          <td>
            <c:choose>
              <c:when test="${phone.price != null}">
                <p>$ ${phone.price}</p>
              </c:when>
              <c:otherwise>
                <p>-</p>
              </c:otherwise>
            </c:choose>
          </td>
          <form>
            <td>
              <input id="quantity${phone.id}" name="requestedQuantity" class="form-control" style="${phone.price == null ? 'display: none' : ''}">
              <input id="phoneId" value="${phone.id}" type="hidden">
                <p id="message${phone.id}" class="message"></p>
            </td>
            <td>
              <input value="Add to cart" onclick="addToCart(this.form)"
                     type="button" class="btn btn-primary" style="${phone.price == null ? 'display: none' : ''}">
            </td>
          </form>
        </tr>
      </c:forEach>
    </table>
  </div>

  <c:choose>
    <c:when test="${phones == null || phones.size() == 0}">
      <p class="not-found">No items found</p>
    </c:when>
    <c:otherwise>
      <c:set var="displayedPagesNumber" value="8"/>
      <c:set var="leftPage" value="${page <= (displayedPagesNumber / 2) ? 1 : page - (displayedPagesNumber / 2)}"/>
      <c:set var="lastPage" value="${leftPage + displayedPagesNumber - 1 > totalPagesNumber ? totalPagesNumber : leftPage + displayedPagesNumber - 1}"/>
      <c:set var="startPage" value="${lastPage- displayedPagesNumber - 1 <= 0 ? 1 : displayedPagesNumber - 1}"/>

      <div class="float-right right">
        <ul class="pagination">
          <li class="page-item ${page eq 1 ? "disabled" : ""}">
            <a class="page-link" href="${pageContext.request.contextPath}/productList?sortField=${sortField}&sortOrder=${sortOrder}&searchQuery=${searchQuery}&page=${page - 1}">
              &laquo;
            </a>
          </li>

          <c:forEach var="i" begin="${leftPage}" end="${lastPage}">
            <li class="page-item ${i eq page ? "active" : ""}">
              <a class="page-link" href="${pageContext.request.contextPath}/productList?sortField=${sortField}&sortOrder=${sortOrder}&searchQuery=${searchQuery}&page=${i}">
                  ${i}
              </a>
            </li>
          </c:forEach>

          <li class="page-item ${page eq totalPagesNumber ? "disabled" : ""}">
            <a class="page-link" href="${pageContext.request.contextPath}/productList?sortField=${sortField}&sortOrder=${sortOrder}&searchQuery=${searchQuery}&page=${page + 1}">
              &raquo;
            </a>
          </li>
        </ul>
      </div>
    </c:otherwise>
  </c:choose>
</body>