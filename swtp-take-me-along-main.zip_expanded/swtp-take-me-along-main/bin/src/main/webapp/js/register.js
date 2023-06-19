export function checkPlz() {
    const dropdown = document.getElementById('vorschlag');

    const options = {
        headers: {
            "Content-Type": "text/plain; charset=utf-8"
        },
        method: "GET",
    }

    let response = fetch("http://escher.informatik.hs-kl.de:8080/PlzService/ort?plz=676", options).then(response => {
        console.log(response);
        return response.text();
    }).then(text => { 
        console.log(text);
        for (const o of text.trim().split("\n")) {
            console.log(o);
            var opt = document.createElement("option");
            opt.value = o;
            opt.innerHTML = o.trim().replace(/[\d;]/g, "");
            dropdown.appendChild(opt);
        }
    });
}
