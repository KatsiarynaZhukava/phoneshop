<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Product details">
  <link rel="stylesheet" href="<c:url value="/resources/styles/productDetails.css"/>">
  <div class="header-container">
    <div class="float-left">
      <tags:cart totalQuantity="${cart.totalQuantity}" totalCost="${cart.totalCost}"/>
    </div>
    <div class="float-right">
      <a href="<c:url value="/productList"/>">
        <button class="btn btn-outline-primary">Back to product list</button>
      </a>
    </div>
  </div>

  <div class="container">
    <div class="container-element">
      <div>
        <h2>${phone.model}</h2>
      </div>
      <div>
        <img src="https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/${phone.imageUrl}">
      </div>
      <div>
        ${phone.description}
      </div>
      <form class="add-to-cart-form">
        <h2 class="bold">Price: </h2><h2>${phone.price} $</h2>
        <div class="form-flex-container">
          <div>
            <input id="quantity${phone.id}" name="requestedQuantity" autocomplete="off" class="form-control">
            <input id="phoneId" value="${phone.id}" type="hidden">
          </div>
          <div>
            <input value="Add to cart" onclick="addPhone(this.form)" type="button" class="btn btn-primary">
          </div>
        </div>
        <p id="message${phone.id}" class="message"></p>
      </form>
    </div>
    <div class="container-element">
      <h5 class="table-name">Display</h5>
      <table>
        <tr>
          <td>Size</td>
          <td>${phone.displaySizeInches} ″</td>
        </tr>
        <tr>
          <td>Resolution</td>
          <td>${phone.displayResolution} ″</td>
        </tr>
        <tr>
          <td>Technology</td>
          <td>${phone.displayTechnology} ″</td>
        </tr>
        <tr>
          <td>Pixel density</td>
          <td>${phone.pixelDensity}</td>
        </tr>
      </table>
      <h5 class="table-name">Dimensions & weight</h5>
      <table>
        <tr>
          <td>Length</td>
          <td>${phone.lengthMm}mm</td>
        </tr>
        <tr>
          <td>Width</td>
          <td>${phone.widthMm}mm</td>
        </tr>
        <tr>
          <td>Color</td>
          <td>
            ${phone.colors.toArray()[0]}
          </td>
        </tr>
        <tr>
          <td>Weight</td>
          <td>${phone.weightGr}</td>
        </tr>
      </table>
      <h5 class="table-name">Camera</h5>
      <table>
        <tr>
          <td>Front</td>
          <td>${phone.frontCameraMegapixels} megapixels</td>
        </tr>
        <tr>
          <td>Back</td>
          <td>${phone.backCameraMegapixels} megapixels</td>
        </tr>
      </table>
      <h5 class="table-name">Battery</h5>
      <table>
        <tr>
          <td>Talk time</td>
          <td>${phone.talkTimeHours} hours</td>
        </tr>
        <tr>
          <td>Stand by time</td>
          <td>${phone.standByTimeHours} hours</td>
        </tr>
        <tr>
          <td>Battery capacity</td>
          <td>${phone.batteryCapacityMah}mAh</td>
        </tr>
      </table>
      <h5 class="table-name">Other</h5>
      <table>
        <tr>
          <td>Colors</td>
          <td>
            <c:forEach var="color" items="${phone.colors}">
              <p>${color.code}</p>
            </c:forEach>
          </td>
        </tr>
        <tr>
          <td>Device type</td>
          <td>${phone.deviceType}</td>
        </tr>
        <tr>
          <td>Bluetooth</td>
          <td>${phone.bluetooth}</td>
        </tr>
      </table>
    </div>
  </div>
</tags:master>