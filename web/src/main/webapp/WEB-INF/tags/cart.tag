<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="totalQuantity" required="true" %>
<%@ attribute name="totalCost" required="true" %>

<a href="<c:url value="/cart"/>">
    <button id="cartBtn" class="btn btn-primary">
        My cart: ${totalQuantity} items $
        <c:choose>
            <c:when test="${totalCost != null}">
                ${totalCost}
            </c:when>
            <c:otherwise>
                0
            </c:otherwise>
        </c:choose>
    </button>
</a>
