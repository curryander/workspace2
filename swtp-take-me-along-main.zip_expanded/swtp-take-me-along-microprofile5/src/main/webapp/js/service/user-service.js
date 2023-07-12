"using strict";

async function getUserByName(username) {
    const options = {
        method: "get",
    };

    return fetch("/api/user/" + username, options)
        .then((response) => response.json())
        .then((data) => {
            if (response.ok) return data;
        })
        .catch((error) => {
            console.error("Error retrieving user:", error);
        });
}

async function getUsernameExists(username) {
    return fetch("/api/user/" + username + "?checkExists=true", {
        method: "get",
    })
        .then((response) => {
            if (response.ok) return true;
            return false;
        })
        .catch((error) => {
            console.error("Error checking user exists: ", error);
        });
}

async function getUserByToken(token) {
    if (token === null) return null;

    const options = {
        method: "get",
    };

    return fetch("/api/user" + "?token=" + token, options)
        .then((response) => {
            if (response.ok) return response.json();
        })
        .then((data) => {
            console.log("retrieved user data for: " + data.username);
            console.log("data: " + JSON.stringify(data, null, 4));
            return data;
        })
        .catch((error) => {
            console.error("Error retrieving user:", error);
        });
}

async function getImageForUser(username) {
    let token = sessionStorage.getItem("loginToken");

    const options = {
        method: "get",
        headers: {},
    };

    return await fetch("/api/user/" + username + "/image?" + "token=" + token, options)
        .then((response) => response.arrayBuffer())
        .then((imageData) => {
            return { imageContent: imageData };
        })
        .then((data) => {
            /*
            let div = document.getElementById("containerPicture");
            div.innerHTML = "";
            let image = document.createElement("img");

            image.src = URL.createObjectURL(new Blob([data.imageContent], { type: "image/jpeg" }));
            image.height = "60";
            image.width = "60";
            image.style.borderRadius = "100%";
            div.append(image);
            */
            // replace type with header
            return URL.createObjectURL(new Blob([data.imageContent], { type: "image/jpeg" }));
        })
        .catch((error) => {
            console.err("Error getting image for " + username, error);
        });
}

async function saveImage(imageData, username, token) {
    if (imageData) {
        return fetch("/api/user/" + username + "/image/?token=" + token, {
            method: "post",
            headers: { "Content-type": "image/jpeg" },
            body: imageData,
        })
            .then((response) => {
                if (!response.ok) {
                    console.error("Ein Fehler ist aufgetreten!");
                    throw Error(response.statusText);
                }
                return true;
            })
            .catch((error) => {
                console.error("Error:", error);
            });
    }

    return false;
}

async function getFullTimetable() {
    let token = sessionStorage.getItem("loginToken");

    const options = {
        method: "get",
    };

    return await fetch("/api/user/" + user.username + "/timetable/" + "0" + "?token=" + token, options)
        .then((response) => {
            if (response.ok) return response.json();
            else throw Error(response.statusText);
        })
        .then((data) => {
            console.log("data: " + JSON.stringify(data, null, 4));
            return data;
        })
        .catch((error) => {
            console.error("Error getting timetable:", error);
        });
}

async function getTimetableForUserAndDay(username, day) {
    let token = sessionStorage.getItem("loginToken");

    const options = {
        method: "get",
    };

    return fetch("/api/user/" + username + "/timetable/" + day + "?token=" + token, options)
        .then((response) => {
            if (response.ok) return response.json();
            else throw Error(response.statusText);
        })
        .then((data) => {
            console.log("data: " + JSON.stringify(data, null, 4));
            return data;
        })
        .catch((error) => {
            console.error("Error getting timetable:", error);
        });
}

async function uploadTimetable(data) {
    let token = sessionStorage.getItem("loginToken");

    return fetch("/api/user/" + user.username + "/timetable/0?token=" + token, {
        method: "post",
        headers: {
            "Content-type": "application/json",
        },

        body: JSON.stringify(data),
    })
        .then((response) => {
            if (response.ok) return true;
            return response.text();
        })
        .catch((error) => {
            console.error("Error:", error);
        });
}

async function getSearchResults(distance, startTime, endTime, journeyType, weekday) {
    let token = sessionStorage.getItem("loginToken");

    let queryParams =
        "?distance=" +
        distance +
        "&starttime=" +
        startTime +
        "&endtime=" +
        endTime +
        "&journeyType=" +
        journeyType +
        "&weekday=" +
        weekday +
        "&token=" +
        token;

    return await fetch("/api/user/search" + queryParams, {
        method: "get",
    })
        .then((response) => {
            if (response.ok) return response.json();
            throw Error(response.statusText);
        })
        .catch((error) => {
            console.error("Error retrieving search results: ", error);
            logout();
        });
}
