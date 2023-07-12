"using strict";

async function getPlzCities(plz) {
    const url = "http://escher.informatik.hs-kl.de:8080/PlzService/ort?plz=";
    const options = {
        headers: {
            "Content-Type": "text/plain; charset=utf-8",
            Accept: "application/json",
        },
        method: "GET",
    };

    if (plz == null || plz.length < 1) return {};

    return fetch(url + plz, options)
        .then((response) => {
            if (response.ok) return response.json();
            return {};
        })
        .catch((error) => {
            console.error("Error retrieving plz:", error);
        });
}
