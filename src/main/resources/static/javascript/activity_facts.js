const factDescription = document.getElementById("factDesc");
const factTime = document.getElementById("factTime");
const factActId = document.getElementById("factActId");
const descError = document.getElementById("errorFactDesc");
const timeError = document.getElementById("errorFactTime");
const charCount = document.getElementById("charCount");

factDescription.addEventListener("input", function() {
    updateCount();
});

/**
 * Checks the fact to ensure that all inputs are correct.
 */
function checkFact() {
    const factDescValue = factDescription.value;
    const factTimeValue = factTime.value;
    const notEmptyOrWhitespaceFormat = /^\s*$/;
    const timeFormat = /^\d+$/;
    descError.textContent = ""; // Clear the error message
    timeError.textContent = ""; // Clear the error message
    let invalid = false;

    if (notEmptyOrWhitespaceFormat.test(factDescValue)) {
        descError.textContent = "Description is required.";
        invalid = true;
    }
    if (notEmptyOrWhitespaceFormat.test(factTimeValue)) {
        timeError.innerText = "Time fact took place is required.";
        invalid = true;
    }
    if (!timeFormat.test(factTimeValue)) {
        timeError.innerText = "Time is invalid.";
        invalid = true;
    }

    if (!invalid) {
        addFact();
    }
}

/**
 * Adds a fact to the current activity. This is done via REST controller.
 */
function addFact() {
    const url = `${getCorrectBaseUrl()}/activities/addFact`;
    let data = {
        "description": factDescription.value,
        "time": factTime.value,
        "actId" : factActId.value
    }
    fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'application/json',
        },
        body: JSON.stringify(data),
    }).then((response) => {
        let status = response.status;
        if (status === 200) {
            factDescription.value = '';
            factTime.value = '';
            getFacts();
            updateCount();
            return response;
        }
        if (status === 400) {
            timeError.textContent = "Fact is after activity ends.";
        }
        throw new Error('Bad Request');
    })
        .then((successResp) => {
        }).catch((error) => {
            console.log(error.toString());
    });
}

/**
 * Gets all the facts for the current activity.
 * Returned data is in JSON format.
 */
function getFacts() {
    const url = `${getCorrectBaseUrl()}/activities/getFacts/`+factActId.value;
    fetch(url, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'application/json',
        },
    }).then((response) => {
        let status = response.status;

        if (status === 200) {
            response.json().then((data) => {
                updateTable(data.data);
            });
            return;
        }
        throw new Error('Bad Request');
    }).then((successResp) => {
        }).catch((error) => {
            console.log(error.toString());
    });
}

/**
 * Deletes the fact with the given factId.
 * @param factId The id of the fact to be deleted.
 */
function deleteFact(factId) {
    const url = `${getCorrectBaseUrl()}/activities/deleteFact/`+factId;
    fetch(url, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'application/json',
        },
    }).then((response) => {
        let status = response.status;
        if (status === 200) {
            document.getElementById("factRow" + factId).remove();
            return;
        }
        throw new Error('Bad Request');
    }).then((successResp) => {
        }).catch((error) => {
            console.log(error.toString());
    });

}

/**
 * Updates the character count for the fact description.
 */
function updateCount() {
    charCount.textContent = (factDescription.value).length + "/150 chars";
}


/**
 * Updates the facts table with the given data. This data is retrieved from a REST controller.
 * @param data A JSON of all the facts for the current activity.
 */
function updateTable(data) {
    const factTable = document.getElementById("activityFacts");
    const factTableBody = document.getElementById("activityFactsTableBody");
    while (factTable.tBodies[0].rows.length != 0) {
        factTable.deleteRow(1);
    }

    if (data.length == 0) {
        let newRow = document.createElement("tr");
        let empty = document.createElement("td");
        empty.innerText = "No facts to display.";
        newRow.appendChild(empty);
    } else {
        for (let factKey of Object.keys(data)) {
            let newRow = document.createElement("tr");
            newRow.setAttribute("id", "factRow" + data[factKey].factId);
            let factText = document.createElement("td");
            let factTimeText = document.createElement("td");
            let deleteFactBtn = document.createElement("td");

            factText.innerText = data[factKey].desc;
            factTimeText.innerText = data[factKey].time + "'";
            deleteFactBtn.innerHTML = "<a id=\"deleteFact" + data[factKey].factId + "\" class=\"btn btn-primary btn-sm\" onclick=\"deleteFact(" + data[factKey].factId + ")\" > Delete </a>"

            newRow.appendChild(factText);
            newRow.appendChild(factTimeText);
            newRow.appendChild(deleteFactBtn);

            factTableBody.appendChild(newRow);
        }
    }
}