let previousPhoneId;
function isInt(value) {
    return !isNaN(value) && (function(x) { return (x | 0) === x; })(parseFloat(value))
}

function addPhone(form) {
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

$(function () {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
});
