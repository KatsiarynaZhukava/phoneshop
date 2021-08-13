function addToCart(form) {
    let requestedQuantity = form.elements["requestedQuantity"].value;
    let phoneId = form.elements["phoneId"].value;

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
        success: (function(data) {
            $("#cartBtn").text("My cart: " + data.totalQuantity + " items $ " + data.totalCost.toFixed(1));
        })
    });
}