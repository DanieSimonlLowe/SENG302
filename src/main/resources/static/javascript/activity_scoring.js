const scoreInputTeam1 = document.getElementById("scoreInputTeam1");
const errorTextTeam1 = document.getElementById("errorTextTeam1");
const scoreInputTeam2 = document.getElementById("scoreInputTeam2");
const errorTextTeam2 = document.getElementById("errorTextTeam2");
const scoreTable = document.getElementById('scoreTable');
const scorePointsInput = document.getElementById("scorePoints");
scoreTable.style.visibility = "hidden";

let individualScores = {}

let teamScoreImage;
let teamPlayers;
let scoreTime;
let scorePoints;
let totalScoreTeam1 = 0;
let totalScoreTeam2 = 0;

let currentSelectedTeam;
let currentSelectedTeamImage;
let currentSelectedPlayer;

// Validate the first input on every input change
scoreInputTeam1.addEventListener("input", function() {
    validateInput();
});

// Validate the second input on every input change
scoreInputTeam2.addEventListener("input", function() {
    validateInput();
});



let team;

function switchScoreTeam(id, img) {
    team = id;
    teamScoreImage.src = img;
    getScoringPlayers(id);
}

function validatePlayerScore() {
    const value = document.getElementById("scoreTime").value;
    const errorScoreInput = document.getElementById("errorScoreInput");
    const pointsButton = document.getElementById("addScoreBtn");
    pointsButton.style.visibility = "hidden";
    errorScoreInput.textContent = "";
    if (value === "") {
        errorScoreInput.textContent = "";
        errorScoreInput.textContent = "Score time must not be empty"
    }
    else if (isNaN(value)) {
        errorScoreInput.textContent = "";
        errorScoreInput.textContent = "Score time must be a number"
    } else if (value < 0) {
        errorScoreInput.textContent = "";
        errorScoreInput.textContent = "Score time must be a positive number"
    }
}


function scoredPointsCheck() {
    const pointsButton = document.getElementById("addScoreBtn");
    const errorScoreInput = document.getElementById("errorScoreInput");
    const scorePointsInputValue = document.getElementById("scorePoints").value;
    pointsButton.style.visibility = "hidden";
    errorScoreInput.textContent = "Score points must not be empty";
    const scoreInputTeam1Value = scoreInputTeam1.value;
    const scoreInputTeam2Value = scoreInputTeam2.value;
    if (scorePointsInputValue === "") {
        errorScoreInput.textContent = "";
        errorScoreInput.textContent = "Points input is greater than the total score for team 1";
    }
    else if (isNaN(scorePointsInputValue)) {
        errorScoreInput.textContent = "";
        errorScoreInput.textContent = "Score points must be a number";
    }
    else if (scorePointsInputValue <= 0) {
        errorScoreInput.textContent = "";
        errorScoreInput.textContent = "Score points must be a positive number and no zero";
    } else {
        if (team === team1Id) {
            if ((totalScoreTeam1 + parseInt(scorePointsInputValue)) > parseInt(scoreInputTeam1Value)) {
                errorScoreInput.textContent = "";
                errorScoreInput.textContent = "Points input is greater than the total score for team 1";

            } else {
                errorScoreInput.textContent = "";
                pointsButton.style.visibility = "visible";

            }
        } else if (team === team2Id) {
            if ((totalScoreTeam2 + parseInt(scorePointsInputValue)) > parseInt(scoreInputTeam2Value)) {
                errorScoreInput.textContent = "";
                errorScoreInput.textContent = "Points input is greater than the total score for team 2";
            } else {
                errorScoreInput.textContent = "";
                pointsButton.style.visibility = "visible";
            }
        }
    }
}


function validateInput() {
    const testRegex = /^\d+(-\d+)*$/;

    function has_one_sections(value) {
        for (let i = 0; i < value.length; i++) {
            if (value[i] === '-') {
                return false
            }
        }
        return true
    }

    const scoreInputTeam1Value = scoreInputTeam1.value;
    const scoreInputTeam2Value = scoreInputTeam2.value;
    const pointsButton = document.getElementById("addScoreBtn");
    pointsButton.style.visibility = "hidden";
    if (scoreInputTeam1Value === "") {
        errorTextTeam1.textContent = "Please enter in the format 'n'";
    } else if (scoreInputTeam2Value === "") {
        errorTextTeam1.textContent = ""
        errorTextTeam2.textContent = "Please enter in the format 'n'";
    } else if (isNaN(scoreInputTeam1Value)) {
        console.log(scoreInputTeam1Value);
        errorTextTeam2.textContent = ""
        errorTextTeam1.textContent = "Invalid score input. Please enter in the format 'n'";
    } else if (isNaN(scoreInputTeam2Value)) {
        console.log(scoreInputTeam2Value);
        errorTextTeam1.textContent = ""
        errorTextTeam2.textContent = "Invalid score input. Please enter in the format 'n'";
    } else if (has_one_sections(scoreInputTeam1Value) !== has_one_sections(scoreInputTeam2Value) ) {
        errorTextTeam1.textContent = ""
        errorTextTeam2.textContent = "Invalid score input. Please enter scores of the same format e.g '3' and '4'";
    } else {
        saveGameScores(scoreInputTeam1Value,scoreInputTeam2Value)
        errorTextTeam1.textContent = ""
        errorTextTeam2.textContent = ""
        scoreTable.style.visibility = "visible";
    }
}


function addScore() {
    const url = `${getCorrectBaseUrl()}/activities/addScore`;
    currentSelectedTeam = team;
    currentSelectedTeamImage = teamScoreImage.src;
    currentSelectedPlayer = teamPlayers.value;
    if (currentSelectedTeam === team1Id) {
        totalScoreTeam1 += parseInt(scorePoints.value);
    } else if (currentSelectedTeam === team2Id) {
        totalScoreTeam2 += parseInt(scorePoints.value);
    }
    console.log(scorePoints.value);

    if (scoreTime.value === "") {
        errorTextTeam1.textContent = ""
        errorTextTeam2.textContent = "Invalid time input. Please enter a number";
        return;
    }


    let data = {
        "activityId": activityId,
        "team": team,
        "player": teamPlayers.value,
        "minute": scoreTime.value,
        "points": scorePoints.value
    };
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

            return response
        }
        throw new Error('Bad Request');
    })
        .then((successResp) => {
            getScores();
        }).catch((error) => {
    });
}

function getScores() {
    document.getElementById("teamImageScoring").src = team1Img;
    document.getElementById("oppositionImageScoring").src = team2Img;
    
    const url = `${getCorrectBaseUrl()}/activities/getScores/` + activityId;
    resetScoreTable();
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
            // Clear the dictionary
            individualScores = {};
            for (const player in successResp) {
                // Add the player's score to the dictionary of all individual scores
                // If the player's score is already in the dictionary, add the new score to a list of scores
                if (individualScores[successResp[player]['playerId']] === undefined) {
                    individualScores[successResp[player]['playerId']] = [[successResp[player]['minute'], successResp[player]['score']]];
                } else {
                    individualScores[successResp[player]['playerId']].push([successResp[player]['minute'], successResp[player]['score']]);
                }

                const score = document.createElement('tr');
                const scoreTeam = document.createElement('td');
                const scorePlayer = document.createElement('td');
                const scoreTime = document.createElement('td');
                const scorePoints = document.createElement('td');

                const teamPic = document.createElement('img');
                teamPic.src = successResp[player]['teamPic'];
                teamPic.style.width = '50px';
                teamPic.style.height = '50px';

                scoreTeam.append(teamPic);


                scorePlayer.textContent = successResp[player]['playerName'];
                scoreTime.textContent = successResp[player]['minute'] + "'";
                scorePoints.textContent = successResp[player]['score'];

                score.append(scoreTeam);
                score.append(scorePlayer);
                score.append(scoreTime);
                score.append(scorePoints);

                scoreTable.append(score);

            }
            currentSelectedTeam = team1Id;
            currentSelectedTeamImage = team1Img;
            switchScoreTeam(currentSelectedTeam, currentSelectedTeamImage);
        })


//    const url2 = baseUrl+'/getGameScore/' + activityId;
    const url2 = `${getCorrectBaseUrl()}/activities/getGameScore/`+activityId;

    fetch(url2, {
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
            scoreInputTeam1.value = successResp["score1"];
            scoreInputTeam2.value = successResp["score2"];
            if (scoreInputTeam1.value.length > 0 && scoreInputTeam2.value.length > 0) {
                scoreTable.style.visibility = "visible";
            }
        })

}

function getScoringPlayers(teamId) {
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
            teamPlayers.innerText = '';
            // Populate dropdowns
            for (const player in successResp) {
                const teamPlayerButton = document.createElement('option');
                teamPlayerButton.value = successResp[player]['userId'];
                teamPlayerButton.textContent = successResp[player]['firstName'];
                teamPlayers.appendChild(teamPlayerButton);

                playerImages[successResp[player]['userId']] = successResp[player]['profilePicName'];
            }
            if (currentSelectedPlayer !== undefined && currentSelectedTeam === teamId) {
                teamPlayers.value = currentSelectedPlayer;
            }
        })
}

function resetScoreTable() {
    scoreTable.innerText = '';

    const newRow = document.createElement("tr");

    const teamCell = document.createElement("td");
    const teamDropdownButton = document.createElement("button");
    teamDropdownButton.className = "btn dropdown-toggle";
    teamDropdownButton.type = "button";
    teamDropdownButton.id = "teamDropdownScoreButton";
    teamDropdownButton.setAttribute("data-bs-toggle", "dropdown");
    teamDropdownButton.setAttribute("aria-expanded", "false");
    teamDropdownButton.setAttribute("aria-label", "teamDropdownMenuButton");

    const teamImage = document.createElement("img");
    teamImage.id = "teamScoreImage";
    teamImage.alt = "Team Profile Pic";
    teamImage.style.width = "45px";
    teamImage.style.height = "45px";

    teamDropdownButton.appendChild(teamImage);

    const teamDropdownMenu = document.createElement("ul");
    teamDropdownMenu.id = "teamScoreButton";
    teamDropdownMenu.className = "dropdown-menu";
    teamDropdownMenu.setAttribute("aria-labelledby", "teamDropdownMenuButton");

    const teamDropdownItem1 = document.createElement("li");
    const teamDropdownLink1 = document.createElement("a");
    teamDropdownLink1.className = "dropdown-item";
    teamDropdownLink1.text = team1Name;
    teamDropdownLink1.addEventListener("click", function() { switchScoreTeam(team1Id, team1Img) });
    teamDropdownItem1.appendChild(teamDropdownLink1);

    const teamDropdownItem2 = document.createElement("li");
    const teamDropdownLink2 = document.createElement("a");
    teamDropdownLink2.className = "dropdown-item";
    teamDropdownLink2.text = team2Name;
    teamDropdownLink2.addEventListener("click", function() { switchScoreTeam(team2Id, team2Img) });
    teamDropdownItem2.appendChild(teamDropdownLink2);

    teamDropdownMenu.appendChild(teamDropdownItem1);
    teamDropdownMenu.appendChild(teamDropdownItem2);

    teamCell.appendChild(teamDropdownButton);
    teamCell.appendChild(teamDropdownMenu);
    newRow.appendChild(teamCell);

    const playerCell = document.createElement("td");
    const playerSelect = document.createElement("select");
    playerSelect.className = "form-control";
    playerSelect.id = "teamPlayers";
    playerCell.appendChild(playerSelect);
    newRow.appendChild(playerCell);

    const scoreTimeCell = document.createElement("td");
    const scoreTimeInput = document.createElement("input");
    scoreTimeInput.id = "scoreTime";
    scoreTimeInput.type = "number";
    scoreTimeInput.placeholder = "minutes";

    scoreTimeInput.addEventListener("input", function() {
        validatePlayerScore(scoreTimeInput.value);
        scoredPointsCheck();

    });


    scoreTimeCell.appendChild(scoreTimeInput);
    newRow.appendChild(scoreTimeCell);

    const scorePointsCell = document.createElement("td");
    const scorePointsInput = document.createElement("input");
    scorePointsInput.id = "scorePoints";
    scorePointsInput.type = "text";
    scorePointsInput.placeholder = "Points";
    scorePointsInput.className = "form-control";

    scorePointsInput.addEventListener("input", function() {
        validatePlayerScore();
    });

    scorePointsInput.addEventListener("input", function() {
        scoredPointsCheck();
    });

    scorePointsCell.appendChild(scorePointsInput);
    newRow.appendChild(scorePointsCell);

    const addScoreCell = document.createElement("td");
    const addScoreButton = document.createElement("button");
    addScoreButton.className = "btn";
    addScoreButton.id = "addScoreBtn";
    addScoreButton.addEventListener("click", function() { addScore() });

    const addScoreImage = document.createElement("img");
    addScoreImage.src = "images/activityStatistics/addCircle.svg";
    addScoreImage.alt = "Add Icon";
    addScoreImage.style.width = "45px";

    addScoreButton.appendChild(addScoreImage);
    addScoreCell.appendChild(addScoreButton);
    newRow.appendChild(addScoreCell);

    scoreTable.appendChild(newRow);

    teamScoreImage = document.getElementById('teamScoreImage');
    teamPlayers = document.getElementById('teamPlayers');
    scoreTime = document.getElementById('scoreTime');
    scorePoints = document.getElementById('scorePoints');
}


function saveGameScores(value1, value2) {
    let baseUrl = getCorrectBaseUrl();
    const url = baseUrl + '/activities/saveGameScore/' + activityId ;
    const data = {
        "score1": value1,
        "score2": value2,
    };
    fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'application/json',
        },
        body: JSON.stringify(data),
    }).then((response) => {
        if(response.status !== 200) {
            errorTextTeam1.textContent = "some problem occurred when saving the game scores"
            errorTextTeam2.textContent = "error code: " + response.status;
            document.getElementById("addScoreBtn").style.visibility = "hidden";
        }
    })
}