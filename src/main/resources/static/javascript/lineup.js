let substitutionArea = document.getElementById("substitutionBox");
const formationDisplay = document.getElementById("formationDisplay");
const displayingImages = [];
let oldNums = [];

substitutionArea.addEventListener("drop", function(event) {
    // Prevent default to avoid opening the dropped player as a URL
    event.preventDefault();

    // Get the player ID from the dropped data
    let playerId = event.dataTransfer.getData("text/plain");
    populateSubstitution(playerId);
});

/**
 * Sets up each section of the lineup page in preparation for being dragged around.
 * Icons are added to the bench and substitution areas, which are then set up to toggle
 * on drag.
 */
function setUpBoard() {
    document.addEventListener("dragover", function(event) {
        event.preventDefault();
    });

    // Load initial empty pitch
    let playerLayoutInput = document.getElementById("formationNumInput");
    updateFormationText(playerLayoutInput);

    // Get all draggable player elements
    let playerElements = document.querySelectorAll(".draggable_player_image");

    // Add dragstart event listener to each player element
    playerElements.forEach(function(playerElement) {
        playerElement.hidden = false;
        playerElement.addEventListener("dragstart", function(event) {
            // Set the data being dragged as the player's ID (on the webpage, not in the database)
            event.dataTransfer.setData("text/plain", playerElement.id);

            // Add a class to the dragged element for custom styling
            playerElement.classList.add("dragged");
        });
    });


    // Get all substitution elements (hidden by default)
    let substitutionElements = document.querySelectorAll(".substitution_element");

    //Add click event to each which puts them back on the bench
    substitutionElements.forEach(function(substitutionElement) {

        substitutionElement.hidden = true;

        substitutionElement.addEventListener("click", function() {
            let playerId = substitutionElement.id.split('_')[1];
            let benchDiv = document.getElementById("player_" + playerId);
            benchDiv.hidden = false;
            substitutionElement.hidden = true;

            //Updates substitution store
            let substitutionStore = document.getElementById("substitutionStore");
            let updatedValue = substitutionStore.value.split(",");
            let subIndex = updatedValue.indexOf(playerId);
            if (subIndex > -1) { // only splice array when item is found
                updatedValue.splice(subIndex, 1); // 2nd parameter means remove one item only
            }
            substitutionStore.value = updatedValue.join(",");
        });
    });

    let formationElements = document.querySelectorAll(".droppable_player_image");

    // Add dragover event listener to the formation display element
    formationElements.forEach(function(formationElement) {
        formationElement.addEventListener("dragover", function (event) {
            // Prevent default to allow drop
            event.preventDefault();
        });

        // Add drop event listener to the formation display element
        formationElement.addEventListener("drop", function (event) {
            // Prevent default to avoid opening the dropped player as a URL
            event.preventDefault();

            // Get the player ID from the dropped data
            let playerId = event.dataTransfer.getData("text/plain");

            populateSlot(playerId, formationElement);
        });

        // Remove player from lineup and put back on the bench if clicked
        formationElement.addEventListener("click", function() {
            let positionIndex = formationElement.querySelector("span").id.split("_")[2];
            let formationPlayerStore = document.getElementById("formationPlayerStore");
            let playerIds = formationPlayerStore.value.split(",");
            let playerId = playerIds[positionIndex];
            if (parseInt(playerId) !== -1) {
                let benchPlayer = document.getElementById("player_" + playerId);
                benchPlayer.hidden = false;
            }
            playerIds[positionIndex] = -1;
            formationPlayerStore.value = playerIds.join(",");

            formationElement.querySelector("img").setAttribute("src", "profilePics/default-img.png");
            formationElement.querySelector("span").innerText = "";
        });
    });
}

/**
 * Sets up the lineup area with default user images to be dragged and dropped into.
 * The layout of these is affected by the formation "nums".
 * @param nums A formation input string, e.g. "4-4-2"
 */
function showFormation(nums) {
    let playerStore = document.getElementById("formationPlayerStore");
    playerStore.value = Array(nums.reduce((a, b) => a + b, 0)).fill(-1).join(",");
    oldNums = nums;
    const fragment = document.createDocumentFragment();

    let current = 0;
    if (nums.length > 0) {
        nums.forEach((num, j) => {
            const divElement = document.createElement("div");
            divElement.classList.add("d-flex", "flex-column", "justify-content-evenly");
            divElement.setAttribute("style", "width:5%;")

            for (let i = 0; i < num; i++) {

                const imgDivElement = document.createElement("div");
                imgDivElement.classList.add("droppable_player_image", "position-container");

                const imgElement = document.createElement("img");
                imgElement.setAttribute("src", "images/default-img.png");
                imgElement.setAttribute("style", "max-width: 90px; max-height: 90px;");
                imgElement.setAttribute("id", `position_${i}_${j}`);
                imgElement.classList.add("w-100", "h-100");

                const spanElement = document.createElement("span");
                spanElement.setAttribute("id", `formation_player_${current}`);
                spanElement.setAttribute("style", "font-size: 0.8rem; text-align: center;");

                imgDivElement.appendChild(imgElement);
                imgDivElement.appendChild(spanElement);

                divElement.appendChild(imgDivElement);
                current++;
            }
            fragment.appendChild(divElement);
        });
    }

    // Clear existing content
    while (formationDisplay.firstChild) {
        formationDisplay.removeChild(formationDisplay.firstChild);
    }

    formationDisplay.appendChild(fragment);
}

/**
 * Checks that the input text is in the correct format and then runs showFormation()
 * @param input
 */
function updateFormationText(input) {
    const text = input.value;
    if (text === "") {
        showFormation([]);
        return;
    }
    const parts = text.split("-");
    let nums = [];
    let shouldDisplay = true;

    parts.forEach((str) => {
        const val = parseInt(str, 10);
        if (isNaN(val) || isNaN(Number(str))) {
            if (str !== "") {
                shouldDisplay = false;
            }
        } else {
            nums.push(val);
        }
    })
    showFormation(nums);
}

/**
 * Resets the lineup area and changes it to whatever formation has been selected.
 * @param nums The formation input string, e.g. "4-4-2"
 * @param id The formation ID
 * @param currentlySelected The formation element that has been clicked
 * @param pitchString The name of the pitch image to be used
 */
function updateFormationLayout(nums, id, currentlySelected, pitchString) {
    let formationStore = document.getElementById("formationStore");
    formationStore.value = id;

    const formationPlayerStore = document.getElementById("formationPlayerStore")
    formationPlayerStore.value = ""

    let formationContainer = document.getElementById("formationContainer");
    formationContainer.style.backgroundImage = "url('images/" + pitchString + ".png')";

    let previouslySelected = document.querySelector(".border-success");
    if (previouslySelected !== null) {
        previouslySelected.classList.remove("border-success");
    }
    currentlySelected.classList.add("border-success");

    let formationInput = document.getElementById("formationNumInput");
    formationInput.value = nums;
    setUpBoard()
}

/**
 * Resets the lineup area and changes it to whatever formation has been selected.
 * @param currentlySelected
 */
function clearSelectedFormation(currentlySelected) {
    let formationStore = document.getElementById("formationStore");
    formationStore.value = -1;

    const substitutionStore = document.getElementById("substitutionStore")
    substitutionStore.value = "";

    const formationPlayerStore = document.getElementById("formationPlayerStore")
    formationPlayerStore.value = ""

    let formationContainer = document.getElementById("formationContainer");
    formationContainer.style.backgroundImage = null;

    let previouslySelected = document.querySelector(".border-success");
    if (previouslySelected !== null) {
        previouslySelected.classList.remove("border-success");
    }
    currentlySelected.classList.add("border-success");
    let formationInput = document.getElementById("formationNumInput");
    formationInput.value = null;
    setUpBoard()
}

/**
 * Populate a slot on the lineup with a specific user that has been dragged and dropped.
 * @param playerId The ID of the player that has been dragged and dropped
 * @param formationElement The element that the player has been dropped on
 */
function populateSlot(playerId, formationElement) {
    // Get the dropped player element by ID
    let playerElement = document.getElementById(playerId);

    // Remove the custom dragged class from the dropped player
    playerElement.classList.remove("dragged");

    // Hide the dragged player from the bench since they are now on the field
    playerElement.hidden = true;

    //Set formation player image to that of the dragged player
    formationElement.querySelector("img").setAttribute("src", playerElement.querySelector("img").getAttribute("src"));
    formationElement.querySelector("span").innerText = playerElement.querySelector("span").innerText;

    //Unhide player if a swap is occurring
    let positionIndex = formationElement.querySelector("span").id.split("_")[2];
    let formationPlayerStore = document.getElementById("formationPlayerStore");
    let playerIds = formationPlayerStore.value.split(",");
    let oldPlayerId = playerIds[positionIndex];
    if (parseInt(oldPlayerId) !== -1) {
        let benchPlayer = document.getElementById("player_" + oldPlayerId);
        benchPlayer.hidden = false;
    }
    // Update the formationPlayerStore
    playerIds[positionIndex] = playerId.split("_")[1];
    formationPlayerStore.value = playerIds.join(",");
}

/**
 * Toggle the substitution and bench areas for a specific player.
 * @param playerId The ID of the player that has been dragged and dropped.
 */
function populateSubstitution(playerId) {
    // Get the dropped player element by ID
    let playerElement = document.getElementById(playerId);

    // Remove the custom dragged class from the dropped player
    playerElement.classList.remove("dragged");

    // Hide the dragged player from the bench since they are now on the field
    playerElement.hidden = true;

    //Unhides the equivalent player in the substitution box
    let substitutedPlayer = document.getElementById("substitution_" + playerId.split("_")[1]);
    substitutedPlayer.hidden = false;

    //Updates substitution store
    let substitutionStore = document.getElementById("substitutionStore");

    if (substitutionStore.value === "") {
        substitutionStore.value += playerId.split("_")[1];
    } else {
        substitutionStore.value += "," + playerId.split("_")[1];
    }
}

/**
 * takes in the position of the image being draged onto and returns a function that
 * when called up dates the display so that the image being draged onto has the dragged profile as it's
 * new display.
 * */
function drop(pos) {
    const onDrop = (ev) => {
        ev.preventDefault();
        const data = parseInt(ev.dataTransfer.getData("iter"),10);
        displayingImages.forEach((value, index) => {
            if (value == data) {
                displayingImages[index] = -1;
            }
        })
        if (pos > displayingImages.length) {
            for (let i = displayingImages.length; i<pos; i++) {
                displayingImages.push(-1);
            }
        }
        displayingImages[pos] = data;
        showFormation(oldNums);
    }
    return onDrop;
}

/**
 * sets up an element so that it can have stuff dragged onto it.
 * */
function allowDrop(ev) {
    ev.preventDefault();
}

/**
 * returns a function that when called sets the data value of "iter" to the input of the first function.
 * the function that is returned is supposed to be used for starting a drag of the player images.
 * */
function dragstart(iter) {
    const drag = (ev) => {
        ev.dataTransfer.setData("iter", iter);
    }
    return drag;
}