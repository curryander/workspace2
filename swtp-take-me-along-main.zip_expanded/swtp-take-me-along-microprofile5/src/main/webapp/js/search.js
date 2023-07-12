"using strict";

// TODO: hashmap for markers
//let searchResults = new Map();

window.addEventListener("load", () => {
    const radiusSlider = document.querySelector("#radius");
    const searchBtn = document.querySelector("#searchBtn");

    radiusSlider.addEventListener("input", (e) => {
        radiusSlider.parentElement.querySelector("small").innerHTML = e.target.value + "km";
    });

    searchBtn.addEventListener("click", searchNearbyUsers);

    clearSearchResults();
});

async function searchNearbyUsers() {
    console.log("searching nearby users");

    clearSearchResults();

    const distance = document.querySelector("#radius").value;
    const journeyType = document.querySelector('input[name="journey"]:checked').value;
    const startTime = document.querySelector("#latestArrival").value;
    const endTime = document.querySelector("#earliestReturn").value;
    const weekday = document.querySelector("#weekday").value;

    //const journeyType = radioButtons.((radioButton) => radioButton.checked).value;

    let res = await getSearchResults(distance, startTime, endTime, journeyType, weekday);
    console.log("res: " + JSON.stringify(res));

    if (res.length == 0) {
        document.querySelector("#searchResults").innerHTML = "Keine Ergebnisse";
        return;
    }

    for (const u of res) {
        let tt = await getTimetableForUserAndDay(u.username, weekday);
        console.log("tt: " + JSON.stringify(tt));
        u.startTime = tt.startTime;
        u.endTime = tt.endTime;

        let c = await addSearchResultCard(u);
        c.addEventListener("click", (e) => cardClicked(e.currentTarget));

        let m = setMarkerCoords(u.position.latitude, u.position.longitude, blueIcon);
        m.addEventListener("click", (e) => markerClicked(e.target));
        mapMarkers.search.push(m);
    }
}

async function addSearchResultCard(userData) {
    const searchResults = document.querySelector("#searchResults");

    let card = document.createElement("div");
    card.classList.add("card");
    card.innerHTML = `
        <div class="card-header">
            <img src="${await getImageForUser(userData.username)}" class="material-icons" alt="Profilbild" />
            <div>${userData.username}</div>
        </div>
        <div class="card-main">
            <div class="main-description">${userData.firstname} ${userData.lastname}</div>
            <div>${userData.email}</div>
            <div>${userData.startTime} - ${userData.endTime}</div>
            <div>${userData.street} ${userData.streetNumber}, ${userData.zip} ${userData.city}</div>
            <span>${distanceBetweenPositions(user.position, userData.position).toFixed(1)}km entfernt</span>    
        </div>
    `;

    searchResults.appendChild(card);

    return searchResults.lastChild;
}

function distanceBetweenPositions(pos1, pos2) {
    if (pos1.latitude == pos2.latitude && pos1.longitude == pos2.longitude) {
        return 0;
    } else {
        var radlat1 = (Math.PI * pos1.latitude) / 180;
        var radlat2 = (Math.PI * pos2.latitude) / 180;
        var theta = pos1.longitude - pos2.longitude;
        var radtheta = (Math.PI * theta) / 180;
        var dist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
        if (dist > 1) dist = 1;
        dist = Math.acos(dist);
        dist = (dist * 180) / Math.PI;
        dist = dist * 60 * 1.1515;

        dist = dist * 1.609344;

        return dist;
    }
}

function cardClicked(card) {
    let i = Array.prototype.indexOf.call(card.parentElement.children, card);
    updateSelectedSearchResult(i);
}

function markerClicked(marker) {
    let i = Array.prototype.indexOf.call(mapMarkers.search, marker);
    updateSelectedSearchResult(i);
}

function updateSelectedSearchResult(selectedIndex) {
    const searchResults = document.querySelector("#searchResults");
    const cards = searchResults.querySelectorAll(".card");
    for (const card of cards) {
        card.classList.remove("selected-card");
    }
    cards[selectedIndex].classList.add("selected-card");

    mapMarkers.search.forEach((marker) => {
        marker.setIcon(blueIcon);
    });

    mapMarkers.search[selectedIndex].setIcon(redIcon);
}

function journeyTypeChanged() {
    const journeyType = document.querySelector('input[name="journey"]:checked').value;
    if (journeyType == 0) {
        document.querySelector("#latestArrival").disabled = false;
        document.querySelector("#earliestReturn").disabled = true;
    } else if (journeyType == 1) {
        document.querySelector("#latestArrival").disabled = true;
        document.querySelector("#earliestReturn").disabled = false;
    } else {
        document.querySelector("#latestArrival").disabled = false;
        document.querySelector("#earliestReturn").disabled = false;
    }
}

function clearSearchResults() {
    document.querySelector("#searchResults").innerHTML = "";
    mapMarkers.search.forEach((m) => removeMarker(m));

    mapMarkers.search = [];
}
