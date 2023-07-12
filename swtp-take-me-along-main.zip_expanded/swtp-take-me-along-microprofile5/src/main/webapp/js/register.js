"using strict";

init = () => {
    document.querySelector("#submitBtn").addEventListener("click", submitRegistration);

    document.addEventListener("click", function (e) {
        closeAutocompleLists(e.target);
    });
};

init();

const isEmpty = (value) => value === undefined || value === null || value.length === 0;

//let lime = new Color("p3", [0, 1, 0]);
//let red = new Color("p3", [1, 0, 0]);
//const redgreenGradient = (v) => red.rannge(lime, v);

function submitRegistration(e) {
    e.preventDefault();

    if (!checkName()) return;
    if (!checkSurName()) return;
    if (!checkPlz()) return;
    if (!checkCity()) return;
    if (!checkStreet()) return;
    if (!checkStreetNum()) return;
    if (!checkUsername()) return;
    if (!checkPassword()) return;

    register();
    logoutToken();
}

function toggleRegisterModal() {
    if (document.querySelector("#registerModal").classList.contains("hidden")) {
        document.querySelector("#registerModal").classList.remove("hidden");
    } else {
        document.querySelector("#registerModal").classList.add("hidden");
    }
}

// validation functions
function checkName() {
    const nameElem = document.querySelector("#name");

    if (isEmpty(nameElem.value)) {
        showError(nameElem, "Vorname darf nicht leer sein.");
    } else if (!isValidName(nameElem.value)) {
        showError(
            nameElem,
            "Name muss mit Großbuchstaben beginnen, Kleinbuchstaben enden und keine Sonderzeichen enthalten."
        );
    } else {
        showSuccess(nameElem);
        return true;
    }

    return false;
}

function checkSurName() {
    const surnameElem = document.querySelector("#surname");

    if (isEmpty(surnameElem.value)) {
        showError(surnameElem, "Nachname darf nicht leer sein.");
    } else if (!isValidName(surnameElem.value)) {
        showError(
            surnameElem,
            "Name muss mit Großbuchstaben beginnen, Kleinbuchstaben enden und keine Sonderzeichen enthalten."
        );
    } else {
        showSuccess(surnameElem);
        return true;
    }

    return false;
}

const isValidName = (name) => {
    const re = /^[A-Z]+[^\d§$%&!?]*[a-z]$/;
    return re.test(name);
};

function checkPlz() {
    const plzElem = document.querySelector("#plz");

    if (isEmpty(plzElem.value)) {
        showError(plzElem, "PLZ darf nicht leer sein.");
    } else if (!isValidPlz(plzElem.value)) {
        showError(plzElem, "Keine gültige 5-stellige PLZ");
    } else {
        showSuccess(plzElem);
        return true;
    }

    return false;
}

const isValidPlz = (plz) => {
    console.log(plz);
    const re = /^[0-9]{5}$/;
    return re.test(plz);
};

function checkCity() {
    // TODO: check if city matches plz
    const cityElem = document.querySelector("#city");

    if (isEmpty(cityElem.value)) {
        showError(cityElem, "Stadt darf nicht leer sein.");
    } else {
        showSuccess(cityElem);
        return true;
    }

    return false;
}

function checkStreet() {
    const streetElem = document.querySelector("#street");

    if (isEmpty(streetElem.value)) {
        showError(streetElem, "Straße darf nicht leer sein.");
    } else {
        showSuccess(streetElem);
        return true;
    }

    return false;
}

const isValidHousenumber = (housenumber) => {
    const re = /^[0-9]{1,4}$/;
    return re.test(housenumber);
};

function checkStreetNum() {
    const housenumberElem = document.querySelector("#streetNum");

    if (isEmpty(housenumberElem.value)) {
        showError(housenumberElem, "Hausnummer darf nicht leer sein.");
    } else if (!isValidHousenumber(housenumberElem.value)) {
        showError(housenumberElem, "Keine gültige Hausnummer");
    } else {
        showSuccess(housenumberElem);
        return true;
    }

    return false;
}

// based on chromium tests https://jsfiddle.net/ghvj4gy9/
// TODO: replace with 4letters+4digits?
// [A-Za-z]{4}[0-9]{4}
const isValidEmail = (email) => {
    const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@(stud.hs-kl.de)$/;
    return re.test(String(email).toLowerCase());
};

function checkEmail() {
    const emailElem = document.querySelector("#email");

    if (isEmpty(emailElem.value)) {
        showError(emailElem, "Email darf nicht leer sein.");
    } else if (!isValidEmail(emailElem.value)) {
        showError(emailElem, "Email ungültig. Nur studentische Adressen erlaubt.");
    } else {
        showSuccess(emailElem);
        return true;
    }

    return false;
}

// TODO: mindestlänge?
async function checkUsername() {
    const usernameElem = document.querySelector("#username");

    if (isEmpty(usernameElem.value)) {
        showError(usernameElem, "Username darf nicht leer sein.");
    } else if ((await getUsernameExists(usernameElem.value.trim())) === true) {
        showError(usernameElem, "Username ist bereits vergeben.");
    } else {
        showSuccess(usernameElem);
        return true;
    }

    return false;
}

function checkPassword() {
    const passwordElem = document.querySelector("#password");

    if (passwordElem.value.length < 5) {
        showError(passwordElem, "Passwort ist zu kurz.");
    } else {
        showSuccess(passwordElem);
        return true;
    }

    return false;
}

const showError = (input, message) => {
    // get the form-field element
    const formField = input.parentElement;
    // add the error class
    formField.classList.remove("success");
    formField.classList.add("error");

    // show the error message
    const error = formField.querySelector("small");
    error.textContent = message;
};

const showSuccess = (input) => {
    // get the form-field element
    const formField = input.parentElement;

    // remove the error class
    formField.classList.remove("error");
    formField.classList.add("success");

    // hide the error message
    const error = formField.querySelector("small");
    error.textContent = "";
};

function checkPasswordStrength() {
    const passwordElem = document.querySelector("#password");
    const shit = document.querySelector(".shit");
    const weak = document.querySelector(".weak");
    const medium = document.querySelector(".medium");
    const good = document.querySelector(".good");
    const strong = document.querySelector(".strong");

    const mediumRegex = /(?=.*[a-z])(?=.*[A-Z])/;
    const goodRegex = /(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9])/;

    let strength = 0;

    if (passwordElem.value.length < 5) {
        strength = 1;
    } else if (
        passwordElem.value.length > 7 &&
        mediumRegex.test(passwordElem.value) &&
        goodRegex.test(passwordElem.value)
    ) {
        strength = 5;
    } else if (mediumRegex.test(passwordElem.value) && goodRegex.test(passwordElem.value)) {
        strength = 4;
    } else if (mediumRegex.test(passwordElem.value)) {
        strength = 3;
    } else {
        strength = 2;
    }

    switch (strength) {
        case 1:
            shit.classList.add("active");
            weak.classList.remove("active");
            medium.classList.remove("active");
            good.classList.remove("active");
            strong.classList.remove("active");
            break;
        case 2:
            shit.classList.add("active");
            weak.classList.add("active");
            medium.classList.remove("active");
            good.classList.remove("active");
            strong.classList.remove("active");
            break;
        case 3:
            shit.classList.add("active");
            weak.classList.add("active");
            medium.classList.add("active");
            good.classList.remove("active");
            strong.classList.remove("active");
            break;
        case 4:
            shit.classList.add("active");
            weak.classList.add("active");
            medium.classList.add("active");
            good.classList.add("active");
            strong.classList.remove("active");
            break;
        case 5:
            shit.classList.add("active");
            weak.classList.add("active");
            medium.classList.add("active");
            good.classList.add("active");
            strong.classList.add("active");
            break;
        default:
            shit.classList.remove("active");
            weak.classList.remove("active");
            medium.classList.remove("active");
            good.classList.remove("active");
            strong.classList.remove("active");
            break;
    }
}

function getPlzSuggestions(input) {
    const plzElem = document.querySelector("#plz");
    const cityElem = document.querySelector("#city");
    let suggestionsDiv = document.querySelector("#plzSuggestions");

    closeAutocompleLists();

    getPlzCities(input.trim())
        .then((json) => {
            //console.log(text);
            if (json.length > 0) {
                for (const o of json) {
                    let b = document.createElement("div");
                    b.innerHTML = "<strong>" + o.plz.substr(0, input.length) + "</strong>";
                    b.innerHTML += o.plz.substr(input.length);
                    b.innerHTML += ", " + o.ort;

                    // stash values in hidden inputs
                    b.innerHTML += "<input type='hidden' value='" + o.plz + "'>";
                    b.innerHTML += "<input type='hidden' value='" + o.ort + "'>";

                    b.addEventListener("click", function (e) {
                        plzElem.value = this.getElementsByTagName("input")[0].value;
                        cityElem.value = this.getElementsByTagName("input")[1].value;
                        closeAutocompleLists();

                        checkPlz();
                        checkCity();
                    });

                    suggestionsDiv.appendChild(b);
                }
            }

            //suggestionsDiv.innerHTML = "<ul>" + list + "</ul>";
        })
        .catch((error) => console.error(error));
}

function closeAutocompleLists(except) {
    var x = document.getElementsByClassName("autocomplete-list");
    for (var i = 0; i < x.length; i++) {
        if (x[i] != except) x[i].innerHTML = "";
    }
}

/*
 * old plaintext version
function getPlzSuggestions() {
    //const dropdown = document.getElementById("vorschlag");
    const plzElem = document.querySelector("#plz");

    if (plzElem.value.length < 1) return;
    let suggestionsDiv = document.querySelector("#plzSuggestions");
    let list = "";

    const options = {
        headers: {
            "Content-Type": "text/plain; charset=utf-8",
            Accept: "application/json",
        },
        method: "GET",
    };

    let response = fetch("http://escher.informatik.hs-kl.de:8080/PlzService/ort?plz=" + plz.value.trim(), options)
        .then((response) => {
            //console.log(response);
            return response.text();
        })
        .then((text) => {
            //console.log(text);
            for (const o of text.trim().split("\n")) {
                //console.log(o);
                //let opt = document.createElement("option");
                //opt.value = o;
                //opt.innerHTML = o.trim().replace(/[\d;]/g, "");
                //dropdown.appendChild(opt);

                list += "<li>" + o.trim().replace(/;/g, "") + "</li>";
            }

            suggestionsDiv.innerHTML = "<ul>" + list + "</ul>";
        })
        .catch((error) => console.error(error));
}
*/
