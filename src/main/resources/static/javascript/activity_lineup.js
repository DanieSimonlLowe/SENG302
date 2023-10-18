const lineupActivity = document.getElementById("lineupActivityId");
const noLineupErrorMsg = "This activity does not include a lineup";

function clearLineup() {
    let formationContainer = document.getElementById("formationContainer");
    let formationDisplay = document.getElementById("formationDisplay");
    formationContainer.style.backgroundImage = "";

    // Remove the players
    while(formationDisplay.firstChild) {
        formationDisplay.removeChild(formationDisplay.firstChild);
    }
    document.getElementById("noLineupOnActivityText").textContent = "";

    const substitutionBox = document.getElementById("substitutionBox");
    while (substitutionBox.firstChild) {
        substitutionBox.removeChild(substitutionBox.firstChild)
    }
}

function getLineup() {

    // clear the lineup
    clearLineup()

    // A function to fetch the lineup for the activity and all its data.
    const url = `${getCorrectBaseUrl()}/activities/getLineup/`+lineupActivity.value;
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
                displayFormation(data);
                displaySubstitutionsBox(data);
            });
            return;
        }
        throw new Error('Bad Request');
    }).then((successResp) => {
    }).catch((error) => {
        document.getElementById("noLineupOnActivityText").textContent = noLineupErrorMsg;
    });


}

function createPlayerImageElement(current,players) {
    const imgDivElement = document.createElement("a");

    imgDivElement.classList.add("droppable_player_image", "btn", "p-0", "position-container");

    // Add the user's statistics to the popover content
    const popoverContent = document.createElement("div");
    if (individualScores[players[current].userId] === undefined && individualSubstitutions[players[current].userId] === undefined) {
        popoverContent.appendChild(document.createTextNode("No statistics available"));
    } else {
        if (individualScores[players[current].userId] !== undefined) {
            for (let k = 0; k < individualScores[players[current].userId].length; k++) {
                const score = individualScores[players[current].userId][k];
                popoverContent.appendChild(document.createTextNode(`Scored ${score[1]}pts at ${score[0]}'min`));
                if (k < individualScores[players[current].userId].length - 1) {
                    popoverContent.appendChild(document.createElement("br"));
                }
            }
            if (individualSubstitutions[players[current].userId] !== undefined) {
                popoverContent.appendChild(document.createElement("br"));
            }
        }


        if (individualSubstitutions[players[current].userId] !== undefined) {
            for (let k = 0; k < individualSubstitutions[players[current].userId].length; k++) {
                const sub = individualSubstitutions[players[current].userId][k];
                popoverContent.appendChild(document.createTextNode(`Substituted ${sub[0]} to ${sub[1]} at ${sub[2]}min`));
                if (k < individualSubstitutions[players[current].userId].length - 1) {
                    popoverContent.appendChild(document.createElement("br"));
                }
            }
        }
    }
    // Define popovers
    new bootstrap.Popover(imgDivElement, {
        container: '.modal-body',
        html: true,
        trigger: 'focus',
        content: popoverContent
    });

    const imgElement = document.createElement("img");
    imgElement.setAttribute("src", players[current].profilePicName);
    imgElement.setAttribute("style", "max-width: 90px; max-height: 90px;");

    imgElement.classList.add("w-100", "h-100");

    const spanElement = document.createElement("span");
    spanElement.setAttribute("id", `formation_player_${current}`);
    spanElement.setAttribute("style", "font-size: 0.8rem; text-align: center;");
    spanElement.textContent = players[current].firstName;

    imgDivElement.appendChild(imgElement);
    imgDivElement.appendChild(spanElement);

    return [imgElement, imgDivElement]
}

function displaySubstitutionsBox(lineupData) {
    const players = lineupData.players;

    const length = lineupData.formationString.split('-').length;
    let i = 0;
    lineupData.formationString.split('-').forEach((num) => {
        i += parseInt(num)
    })


    if (i === 0) {
        return;
    }


    const substitutionBox = document.getElementById("substitutionBox");

    for (; i < players.length; i++) {
        let temp = createPlayerImageElement(i,players)
        let imgElement = temp[0]
        let imgDivElement = temp[1]
        imgDivElement.tabIndex = length;
        imgElement.setAttribute("id", `bench_${i}`);
        substitutionBox.appendChild(temp[1])
    }
}

function displayFormation(lineupData) {
    console.log(lineupData);

    const pitchString = lineupData.pitchString;
    const formationStringList = lineupData.formationString.split('-');
    const players = lineupData.players;

    // Set the background image
    let formationContainer = document.getElementById("formationContainer");
    formationContainer.style.backgroundImage = "url('images/" + pitchString + ".png')";

    let current = 0;
    const fragment = document.createDocumentFragment();
    if (formationStringList.length > 0) {
        formationStringList.forEach((num, j) => {
            const divElement = document.createElement("div");
            divElement.classList.add("d-flex", "flex-column", "justify-content-evenly");
            divElement.setAttribute("style", "width:5%;")

            for (let i = 0; i < num; i++) {
                let temp = createPlayerImageElement(current,players)
                let imgElement = temp[0]
                let imgDivElement = temp[1]
                imgDivElement.tabIndex = i;
                imgElement.setAttribute("id", `position_${i}_${j}`);
                divElement.appendChild(imgDivElement);
                current++;
            }
            fragment.appendChild(divElement);
        });
    }

    const formationDisplay = document.getElementById("formationDisplay");

    // Clear existing content
    while (formationDisplay.firstChild) {
        formationDisplay.removeChild(formationDisplay.firstChild);
    }
    formationDisplay.appendChild(fragment);
}


