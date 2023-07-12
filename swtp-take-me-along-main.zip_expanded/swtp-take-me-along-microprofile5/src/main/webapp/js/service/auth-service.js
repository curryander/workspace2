"using strict";

window.addEventListener("load", () => {
    document.querySelector("#loginBtn").addEventListener("click", login);
    document.querySelector("#logoutBtn").addEventListener("click", logout);

    /*
    document.getElementById("loginBtn").addEventListener("click", login);
    document.getElementById("logoutBtn").addEventListener("click", logout);
    document.getElementById("serachBtn").addEventListener("click", search);

    enableLogin();
    deleteSearchResult();
    */
    let token = sessionStorage.getItem("loginToken");
    // TODO: check if token is still valid
    if (token != null) {
        console.log("already logged in");
        loggedIn(true);
    }
});

function register() {
    let user = {
        firstname: document.querySelector("#name").value,
        lastname: document.querySelector("#surname").value,
        street: document.querySelector("#street").value,
        streetNumber: document.querySelector("#streetNum").value,
        zip: document.querySelector("#plz").value,
        city: document.querySelector("#city").value,
        email: document.querySelector("#email").value,
        username: document.querySelector("#username").value,
        password: document.querySelector("#password").value,
    };

    console.log("user for signup: " + JSON.stringify(user));

    fetch("api/user", {
        method: "post",
        headers: {
            "Content-type": "application/json",
        },
        body: JSON.stringify(user),
    })
        .then((response) => {
            return response.json();
        })
        .then((data) => {
            console.log("Login Token " + JSON.stringify(data));

            let file = document.querySelector("#ppicture").files[0];
            if (!saveImage(file, user.username, data.token)) console.log("Error uploading image");

            //logoutToken(data.token);
            alert("Registrierung erfolgreich!");
            toggleRegisterModal();
        })
        .catch((error) => {
            console.error("Error:", error);
        });

    // TODO: handle Position not found
    // TODO: handle user already exists
}

function logout() {
    let id = sessionStorage.getItem("loginToken");

    logoutToken(id);
}

function logoutToken(uuid) {
    fetch("api/auth" + "?token=" + uuid, {
        method: "delete",
    })
        .then((response) => {
            if (response.ok) {
                // TODO: remove markers
            } else {
                console.error("Error logging out:", response.text());
            }
        })
        .catch((error) => {
            console.error("Error logging out:", error);
            sessionStorage.removeItem("loginToken");
        })
        .finally(() => {
            sessionStorage.removeItem("loginToken");
            loggedIn(false);
        });
}

function login() {
    let data = {
        username: document.querySelector("#loginUsername").value,
        password: document.querySelector("#loginPassword").value,
    };

    fetch("api/auth", {
        method: "post",
        headers: {
            "Content-type": "application/json",
        },
        body: JSON.stringify(data),
    })
        .then((response) => response.json())
        .then((data) => {
            console.log("Login Token " + data.token);
            sessionStorage.setItem("loginToken", data.token);

            loggedIn(true);
        })
        .catch((error) => {
            // TODO: handle user not found
            console.error("Error:", error);
            sessionStorage.removeItem("loginToken");
            document.querySelector("#loginError").innerHTML = "Es ist ein Fehler aufgetreten!";
            document.querySelector("#loginPassword").value = "";
        });
}
