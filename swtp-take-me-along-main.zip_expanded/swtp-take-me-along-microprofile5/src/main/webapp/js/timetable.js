"using strict";

window.addEventListener("load", () => {
    const saveBtn = document.querySelector("#saveTimetableBtn");
    const resetBtn = document.querySelector("#resetTimetableBtn");

    saveBtn.addEventListener("click", () => {
        saveTimetable();
    });
    resetBtn.addEventListener("click", () => {
        resetTimetable();
    });
});

async function setupTimetable() {
    const addressElem = document.querySelector("#myAddress");

    addressElem.innerHTML = user.street + " " + user.streetNumber + ", " + user.zip + " " + user.city;
    let tt = await getFullTimetable();

    clearTimetable();

    for (let i = 0; i < tt.length; i++) {
        let entry = tt[i];

        document.getElementById("begin" + entry.weekday).value = entry.startTime.slice(0, 5);
        document.getElementById("end" + entry.weekday).value = entry.endTime.slice(0, 5);
    }
}

async function saveTimetable() {
    let token = sessionStorage.getItem("loginToken");

    let timetable = [];

    for (let i = 1; i <= 7; i++) {
        if (!document.getElementById("begin" + i).value == "" && !document.getElementById("end" + i).value == "") {
            let ttEntry = {
                weekday: i,
                startTime: document.getElementById("begin" + i).value,
                endTime: document.getElementById("end" + i).value,
            };

            timetable.push(ttEntry);
        }
    }

    let errorElem = document.getElementById("timetable-error");

    let response = await uploadTimetable(timetable);
    if (!(response === true)) {
        console.log("Error uploading timetable" + response);

        errorElem.innerHTML = response;
    } else {
        errorElem.innerHTML = "";
    }

    console.log("saving timetable: " + JSON.stringify(timetable));
}

function checkTimetable() {
    // TODO: check start date < end date
}

function resetTimetable() {
    // TODO: reset to last downloaded timetable instead
    clearTimetable();
}

function clearTimetable() {
    for (let i = 1; i <= 7; i++) {
        document.getElementById("begin" + i).value = "";
        document.getElementById("end" + i).value = "";
    }
}
