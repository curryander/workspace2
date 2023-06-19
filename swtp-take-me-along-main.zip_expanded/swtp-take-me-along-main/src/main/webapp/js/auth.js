"using strict";

window.onload = function () {
    let token = sessionStorage.getItem("loginToken");

    if (token != null) {
        showNotesView();
        setUserLabel();
        getNotes();
    } else {
        showLoginView();
    }
};

window.addEventListener("load", () => {
    document.getElementById("loginBtn").addEventListener("click", login);
    document.getElementById("logoutBtn").addEventListener("click", logout);
    document.getElementById("serachBtn").addEventListener("click", search);

    enableLogin();
    deleteSearchResult();

    let token = sessionStorage.getItem("uuidToken");
    if (token != null) {
        disableLogin();
    }
});

function login() {
    let data = {
        username: document.querySelector("#loginUsername").value,
        password: document.querySelector("#loginPassword").value,
    };

    fetch("app/access", {
        method: "post",
        headers: {
            "Content-type": "application/json",
        },
        body: JSON.stringify(data),
    })
        .then((response) => response.json())
        .then((data) => {
            console.log("Login Token " + data);
            sessionStorage.setItem("loginToken", data.token);
            showNotesView();
            setUserLabel();
            getNotes();
        })
        .catch((error) => {
            console.error("Error:", error);
            sessionStorage.removeItem("loginToken");
            document.querySelector("#loginError").innerHTML = "Es ist ein Fehler aufgetreten!";
        });
}

function logout(uuid) {
    fetch("demo/access" + "/?token=" + uuid, {
        method: "delete",
    }).catch((error) => {
        console.error("Error:", error);
    });
}

function logout() {
    let id = sessionStorage.getItem("loginToken");

    fetch("app/access" + "/" + id, {
        method: "delete",
    })
        .then((response) => {
            if (response.ok) {
                sessionStorage.removeItem("loginToken");
                showLoginView();
            }
        })
        .catch((error) => console.error("Error:", error));
}

function register() {
    let file = document.getElementById("profilImage").files[0];
    var reader = new FileReader();

    if (file) {
        reader.readAsDataURL(file);
        reader.onload = function () {
            let data = {
                username: document.querySelector("#registerUsername").value,
                password: document.querySelector("#registerPassword").value,
                profileImage: reader.result,
            };

            registerUser(data);
        };
        reader.onerror = function (error) {
            console.log("Error: ", error);
        };
    } else {
        let data = {
            username: document.querySelector("#registerUsername").value,
            password: document.querySelector("#registerPassword").value,
            profileImage: "",
        };
        registerUser(data);
    }
}

function registerUser(data) {
    fetch("app/users", {
        method: "post",
        headers: {
            "Content-type": "application/json",
        },
        body: JSON.stringify(data),
    })
        .then((response) => {
            if (!response.ok) {
                document.querySelector("#registerError").innerHTML = "Ein Fehler ist aufgetreten!";
                throw Error(response.statusText);
            }
            return response.json();
        })
        .then((data) => {
            console.log("Login Token " + data);
            sessionStorage.setItem("loginToken", data.token);
            showNotesView();
            setUserLabel();
            getNotes();
        })
        .catch((error) => {
            sessionStorage.removeItem("loginToken");
            console.error("Error:", error);
        });
}

function clearLoginErrorMessage() {
    document.querySelector("#loginError").innerHTML = "";
}

function clearRegisterErrorMessage() {
    document.querySelector("#registerError").innerHTML = "";
}

function setUserLabel() {
    let userId = sessionStorage.getItem("loginToken");
    if (userId != null) {
        fetch("app/users" + "/" + userId)
            .then((response) => response.json())
            .then((data) => {
                console.log("Data = " + data.username);
                document.querySelector("#labelUsername").innerHTML = data.username;
                if (data.profileImage.length > 10) {
                    document.querySelector("#labelImage").setAttribute("src", data.profileImage);
                } else {
                    document.querySelector("#labelImage").setAttribute("src", "profil.jpg");
                }
            })
            .catch((error) => console.error("Error:", error));
    }
}

function showLoginView() {
    clearLoginErrorMessage();
    let divLogin = document.querySelector("#login");
    let divRegister = document.querySelector("#register");
    let divNotes = document.querySelector("#notes");

    divLogin.style.display = "inline";
    divRegister.style.display = "none";
    divNotes.style.display = "none";

    document.querySelector("#loginUsername").value = "";
    document.querySelector("#loginPassword").value = "";
}

function showRegisterView() {
    let divLogin = document.querySelector("#login");
    let divRegister = document.querySelector("#register");
    let divNotes = document.querySelector("#notes");

    divLogin.style.display = "none";
    divRegister.style.display = "inline";
    divNotes.style.display = "none";

    document.querySelector("#registerUsername").value = "";
    document.querySelector("#registerPassword").value = "";
    document.querySelector("#profilImage").value = null;
}
