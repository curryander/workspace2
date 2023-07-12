"using strict";

let myMap;
let user;

let blackIcon;
let blueIcon;
let redIcon;

let mapMarkers = {
    home: {},
    search: [],
};

init = () => {
    initmaposm();

    initMarker();
};

init();

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
        attribution: 'Map data &copy;<a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
        maxZoom: 18, // max. possible 23
        id: "mapbox/streets-v11",
    }).addTo(myMap);
}

function initMarker() {
    blackIcon = new L.Icon({
        iconUrl: "./img/marker-icon-home.png",
        shadowUrl: "./img/marker-shadow.png",
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowSize: [41, 41],
    });

    redIcon = new L.Icon({
        iconUrl: "./img/marker-icon-red.png",
        shadowUrl: "./img/marker-shadow.png",
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowSize: [41, 41],
    });

    blueIcon = new L.Icon({
        iconUrl: "./img/marker-icon-blue.png",
        shadowUrl: "./img/marker-shadow.png",
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowSize: [41, 41],
    });
}

function removeAllMarkers() {
    removeMarker(mapMarkers.home);
    mapMarkers.search.forEach((marker) => removeMarker(marker));

    mapMarkers.home = {};
    mapMarkers.search = [];
}

function removeMarker(marker) {
    if (myMap.hasLayer(marker)) myMap.removeLayer(marker);
}

function setVisibility(elementId, visible) {
    const element = document.getElementById(elementId);
    if (visible === true) {
        if (element.classList.contains("hidden")) element.classList.remove("hidden");
    } else {
        element.classList.add("hidden");
    }
}

function setMarker() {
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

function setMarkerCoords(lat, long, icon) {
    let marker = new L.Marker([lat, long], { icon: icon });
    marker.addTo(myMap);

    return marker;
}

async function loggedIn(isLoggedIn) {
    setVisibility("loggedOut", isLoggedIn === false);
    setVisibility("loggedIn", isLoggedIn === true);

    setAsideContent(isLoggedIn === true ? 1 : 0);

    if (isLoggedIn === true) {
        user = await getUserByToken(sessionStorage.getItem("loginToken"));
        document.querySelector("#welcomeMessage").innerHTML = "Willkommen " + user.username + "!";
        let imgElem = document.querySelector("#profilePic");

        let img = await getImageForUser(user.username);
        imgElem.src = img;

        mapMarkers.home = setMarkerCoords(user.position.latitude, user.position.longitude, blackIcon);
    } else {
        removeAllMarkers();
        clearSearchResults();
    }
}

function setAsideContent(contentId) {
    console.log("setAsideContent: " + contentId);
    switch (contentId) {
        case 0:
            setVisibility("find-ride-container", false);
            setVisibility("timetable-container", false);
            break;
        case 1:
            setVisibility("find-ride-container", true);
            setVisibility("timetable-container", false);
            break;
        case 2:
            setVisibility("find-ride-container", false);
            setVisibility("timetable-container", true);
            setupTimetable();
            break;
    }
}
