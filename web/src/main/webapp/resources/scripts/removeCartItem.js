function removeCartItem(phoneId) {
    let requestedQuantity = document.getElementById(`quantity${phoneId}`).value;

    $.ajax({
        url: contextPath + "/ajaxCart",
        type: "DELETE",
        data: JSON.stringify({requestedQuantity, phoneId}),
        dataType: "json",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        contentType: "application/json",
        success: function (data) {
            $("#cartBtn").text("My cart: " + data.totalQuantity + " items $ " + data.totalCost.toFixed(1));

            let tableRow = document.getElementById(`cartRow${phoneId}`);
            tableRow.remove();
            if (document.getElementsByTagName("tr").length === 1) {
                document.getElementsByTagName("table")[0].remove();
                document.getElementById("cartButtonsContainer").remove();

                p = document.createElement("p");
                p.className = "no-items-message";
                p.innerText = "No items in your cart";
                document.getElementById("cartItemsContainer").appendChild(p);
            }
        },
        error: function (data) {
            alert(JSON.parse(data.responseText).message);
            $(`#message${phoneId}`).text(JSON.parse(data.responseText).message);
        }
    });
}