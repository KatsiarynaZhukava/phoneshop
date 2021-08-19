let previousPhoneId;
function isInt(value) {
    return !isNaN(value) && (function(x) { return (x | 0) === x; })(parseFloat(value))
}

function cartOperations(form) {
    let requestedQuantity = form.elements["requestedQuantity"].value;
    let phoneId = form.elements["phoneId"].value;

    if (previousPhoneId !== undefined && previousPhoneId !== phoneId) {
        document.getElementById(`message${previousPhoneId}`).innerHTML = "";
        document.getElementById(`quantity${previousPhoneId}`).value = "";
    }

    if(isInt(requestedQuantity)) {
        $.ajax({
            url: contextPath + "/ajaxCart",
            type: "POST",
            data: JSON.stringify({ requestedQuantity,phoneId }),
            dataType: "json",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            contentType: "application/json",
            success: function(data) {
                $("#cartBtn").text("My cart: " + data.totalQuantity + " items $ " + data.totalCost.toFixed(1));
                $(`#message${phoneId}`).text("");
                form.elements["requestedQuantity"].value = "";
            },
            error: function(data) {
                previousPhoneId = phoneId;
                $(`#message${phoneId}`).text(JSON.parse(data.responseText).message);
            }
        });
    } else {
        previousPhoneId = phoneId;
        document.getElementById(`message${phoneId}`).innerHTML = "Quantity should be an integer number";
    }
}

function removeCartItem(phoneId) {
    let requestedQuantity = document.getElementById(`quantity${phoneId}`).value;
    if(!isInt(requestedQuantity)) {
        requestedQuantity = 0;
    }
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
        }
    });
}