window.onload = function () {
    initmaposm();

    redIcon = new L.Icon({
        iconUrl: "./icon/marker-icon-red.png",
        shadowUrl: "./icon/marker-shadow.png",
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowSize: [41, 41],
    });

    blueIcon = new L.Icon({
        iconUrl: "../assets/marker-icon-blue.png",
        shadowUrl: "../assets/marker-shadow.png",
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowSize: [41, 41],
    });

    var button = document.querySelector("#showBtn");
    button.onclick = setMarker;
};

let myMap;

function initmapbox() {
    myMap = L.map("map-container").setView([49.250723, 7.377122], 13);

    L.tileLayer("https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}", {
        attribution:
            'Map data &copy;<a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
        maxZoom: 18, // max. possible 23
        id: "mapbox/streets-v11",
        tileSize: 512,
        zoomOffset: -1,
        accessToken: "pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw",
    }).addTo(myMap);
}

function initmaposm() {
    myMap = L.map("map-container").setView([49.250723, 7.377122], 13);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
        maxZoom: 18, // max. possible 23
        id: "mapbox/streets-v11",
    }).addTo(myMap);
}

function showSerach() {
    document.getElementById("ausblendeBtn").disabled = false;
    document.getElementById("einblendeBtn").disabled = true;
    setVisibility("find-ride-container", true);
}

function hideSearch() {
    document.getElementById("ausblendeBtn").disabled = true;
    document.getElementById("einblendeBtn").disabled = false;
    setVisibility("find-ride-container", false);
}

function setVisibility(elementId, visible) {
    const element = document.getElementById(elementId);
    if (visible === true) {
        element.classList.remove("hidden");
    } else {
        element.classList.add("hidden");
    }
}

function showRegistration() {
    document.querySelector("#registration").style.display = "block";
    //document.getElementById("registration").style.display = "block";
}

function hideRegistration() {
    document.querySelector("#registration").style.display = "none";
    //document.getElementById("registration").style.display = "none";
}

function setMarker() {
    var street = document.querySelector("#street").value;
    var streetNr = document.querySelector("#streetNr").value;
    var zip = document.querySelector("#zip").value;
    var city = document.querySelector("#city").value;

    var query = "streetNr=" + streetNr + "&";
    query += "street=" + street + "&";
    query += "postalcode=" + zip + "&";
    query += "country=Germany" + "&";
    query += "city=" + city;

    console.log(query);

    const myInit = {
        method: "GET",
        headers: {
            Accept: "application/json",
        },
    };

    const myRequest = new Request("locationconverter?" + query, myInit);

    fetch(myRequest)
        .then(function (response) {
            return response.json();
        })
        .then(function (data) {
            console.log(data.lat);
            console.log(data.lon);

            let marker = new L.Marker([data.lat, data.lon]);
            marker.addTo(myMap);
            marker.on("click", (event) => showCoordinates(event, data));
        })
        .catch(function (error) {
            console.log("EXCEPTION");
            console.error(error);
        });
}

function showCoordinates(event, data) {
    console.log(event);
    let div = document.querySelector("#out");
    div.innerHTML = data.name + "<br/> Lat: " + data.lat + " Lon: " + data.lon;
}
