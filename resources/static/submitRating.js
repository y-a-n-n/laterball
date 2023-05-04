function submitRating(url, fixtureId, csrf) {
    var rating = document.getElementById("rating-" + fixtureId).value;
    var data = {
        "fixtureId": fixtureId,
        "csrf": csrf,
        "rating": rating
    };
    var json = JSON.stringify(data);
    var xhr = new XMLHttpRequest();
    xhr.open("POST", url, true);
    xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
    xhr.onload = function () {
        if (xhr.status == "200") {
            document.querySelector("#button-" + fixtureId).disabled = true;
            document.querySelector("#rating-" + fixtureId).disabled = true;
        }
    }
    xhr.send(json);
}