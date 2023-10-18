const teamLabel = document.getElementById('teamLabel');
const onPlayerLabel = document.getElementById('onPlayerLabel');
const offPlayerLabel = document.getElementById('offPlayerLabel');

const teamImage = document.getElementById('teamImage');
const onPlayerImage = document.getElementById('onPlayerImage');
const offPlayerImage = document.getElementById('offPlayerImage');

const onPlayersButtons = document.querySelector('#onPlayersButtons');
const offPlayersButtons = document.querySelector('#offPlayersButtons');

const substitutionTimeInput = document.getElementById("substitutionTimeInput");
const errorSubMinute = document.getElementById("errorSubMinute");
const errorSubstitute = document.getElementById("errorSubstitute");

let playerImages = {};

let individualSubstitutions = {};

let onPlayerId = -1;
let offPlayerId = -1;

onPlayersButtons.addEventListener('click', selectOnPlayer, false);
offPlayersButtons.addEventListener('click', selectOffPlayer, false);

let currentTeamId = -1;
function switchSubstitutionTeam(id, img, name) {
    currentTeamId = id;
    teamLabel.textContent = name;
    teamImage.src = img;
    onPlayerId = -1;
    offPlayerId = -1;
    onPlayerLabel.textContent = "On";
    offPlayerLabel.textContent = "Off";
    onPlayerImage.src = "images/activityStatistics/person.svg";
    offPlayerImage.src = "images/activityStatistics/person.svg";

    getSubstitutionsPlayers(id);
}
function selectOnPlayer(e) {
    console.log("Player on: " + e.target.value);
    onPlayerId = e.target.value;
    onPlayerLabel.textContent = e.target.innerText;
    onPlayerImage.src = playerImages[e.target.value];
}
function selectOffPlayer(e) {
    console.log("Player off: " + e.target.value)
    offPlayerId = e.target.value;
    offPlayerLabel.textContent = e.target.innerText;
    offPlayerImage.src = playerImages[e.target.value];
}

function checkInput() {
    const minuteFormat = /^\d+$/;
    var invalid = false;
    errorSubMinute.textContent = ""; // Clear the error message
    errorSubstitute.textContent = "";
    if (!minuteFormat.test(substitutionTimeInput.value)) {
        errorSubMinute.innerText = "Time must be a positive number.";
        invalid = true;
    }
    if (onPlayerId === offPlayerId) {
        errorSubstitute.innerText = "A player cannot be substituted with themself."
        invalid = true;
    }
    if (!invalid) {
        addSubstitution();
    }
}

function addSubstitution() {
    console.log("Add substitution");
    console.log("Activity Id: " + activityId);
    console.log("Player on: " + onPlayerLabel.textContent);
    console.log("Player off: " + offPlayerLabel.textContent);
    console.log("Team: " + teamLabel.textContent);
    console.log("Minute: " + substitutionTimeInput.value);


    const url = `${getCorrectBaseUrl()}/activities/addSubstitution`;
    let data = {
        "activityId": activityId,
        "playerOff": offPlayerId,
        "playerOn": onPlayerId,
        "team": currentTeamId,
        "minute": (parseInt(substitutionTimeInput.value, 10)).toString()
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
            getSubstitutions(activityId);
            return response
        }
        if (status === 403) {
            errorSubMinute.textContent = "Time is after activity ends.";
        }
        throw new Error('Bad Request');
    })
        .then((successResp) => {

        }).catch((error) => {
        console.log(error.toString());
    });
}

function getSubstitutions(activityId) {
    const url = `${getCorrectBaseUrl()}/activities/getSubstitutions/`+activityId;
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
                individualSubstitutions = {}
                for (const s in data) {
                    const sub = data[s];
                    const saved = [sub.playerOffName, sub.playerOnName, sub.minute];
                    if (individualSubstitutions[sub.playerOff] == undefined) {
                        individualSubstitutions[sub.playerOff] = [saved];
                    } else {
                        individualSubstitutions[sub.playerOff].push(saved);
                    }
                    if (individualSubstitutions[sub.playerOn] == undefined) {
                        individualSubstitutions[sub.playerOn] = [saved];
                    } else {
                        individualSubstitutions[sub.playerOn].push(saved);
                    }
                }
                updateSubTable(data);
            });
            return;
        }
        throw new Error('Bad Request');
    }).then((successResp) => {
    }).catch((error) => {
        console.log(error.toString());
    });
}

function getSubstitutionsPlayers(teamId) {
    const url = `${getCorrectBaseUrl()}/activities/getPlayers/`+teamId;

    fetch(url, {
        headers: {
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'application/json',
        },
    }).then((response) => {
        let status = response.status;
        if (status === 200) {
            return response.json()
        }
        throw new Error('Bad Request');
    })
        .then((successResp) => {
            const onPlayerButtons = document.getElementById('onPlayersButtons');
            const offPlayerButtons = document.getElementById('offPlayersButtons');
            onPlayerButtons.innerText = '';
            offPlayerButtons.innerText = '';
            // Populate dropdowns
            for (const player in successResp) {
                const onPlayerButton = document.createElement('button');
                onPlayerButton.classList.add('dropdown-item');
                onPlayerButton.value = successResp[player]['userId'];
                onPlayerButton.textContent = successResp[player]['firstName'];
                onPlayerButtons.appendChild(onPlayerButton);

                const offPlayerButton = document.createElement('button');
                offPlayerButton.classList.add('dropdown-item');
                offPlayerButton.value = successResp[player]['userId'];
                offPlayerButton.textContent = successResp[player]['firstName'];
                offPlayerButtons.appendChild(offPlayerButton);

                playerImages[successResp[player]['userId']] = successResp[player]['profilePicName'];
            }
        })
}


function updateSubTable(data) {
    console.log("update table");
    console.log(data);

    const subTable = document.getElementById("activitySubstitutions");
    const subTableBody = document.getElementById("activitySubstitutionsTableBody");
    while (subTable.tBodies[0].rows.length != 0) {
        subTable.deleteRow(1);
    }
    var oldTable = document.querySelectorAll('input[type=tbody]');
    var newTable = document.createElement('tbody');

    for (subKey in data) {
        console.log(subKey)
        let newRow = document.createElement("tr");
        newRow.setAttribute("id", "subRow" + data[subKey].id);

        let subTeam = document.createElement("td");
        let subOn = document.createElement("td");
        let subOff = document.createElement("td");
        let subTime = document.createElement("td");

        subTeam.innerHTML = "<img src=\"" + data[subKey].teamPic + "\" class=\"rounded-circle\" style=\"width: 35px;\" alt=\"Team Profile Picture\" />";
        subOn.innerHTML = "<div><img src=\"" + data[subKey].playerOnPic + "\" class=\"rounded-circle me-2\" style=\"width: 35px;\" alt=\"On Profile Picture\">" + data[subKey].playerOnName + "</div>"
        subOff.innerHTML = "<div><img src=\"" + data[subKey].playerOffPic + "\" class=\"rounded-circle me-2\" style=\"width: 35px;\" alt=\"On Profile Picture\">" + data[subKey].playerOffName + "</div>"
        subTime.innerText = data[subKey].minute + "'";

        newRow.appendChild(subTeam);
        newRow.appendChild(subOn);
        newRow.appendChild(subOff);
        newRow.appendChild(subTime);

        subTableBody.appendChild(newRow);
    }
}


