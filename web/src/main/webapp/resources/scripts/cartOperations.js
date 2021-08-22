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

window.addEventListener("load", function() {
    let updateForm = document.getElementById("updateCartForm");
    updateForm.addEventListener("submit", function(e) {
        let allInputs = document.getElementsByTagName("input");
        let inputs = [];
        for (let i = 0; i < allInputs.length; i++) {
            if(allInputs[i].id.indexOf(".requestedQuantity") === allInputs[i].id.length - ".requestedQuantity".length) {
                inputs.push( allInputs[i]);
                document.getElementById("errorMessage" + allInputs[i].id.slice("items".length, allInputs[i].id.length - ".requestedQuantity".length ) ).innerHTML = "";
            }
        }
        for(let i = 0; i < inputs.length; i++) {
            if (!isInt(inputs[i].value)) {
                e.preventDefault();
                document.getElementById("updateMessage").innerHTML = "Error updating the cart";
                document.getElementById("errorMessage" + inputs[i].id.slice("items".length, inputs[i].id.length - ".requestedQuantity".length ) ).innerHTML = "Quantity should be an integer number";
            }
        }
    })
});