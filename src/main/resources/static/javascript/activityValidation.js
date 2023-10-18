const description = document.getElementById('actDesc')
const activityStart = document.getElementById('actStart')
const activityEnd = document.getElementById('actEnd')
const team = document.getElementById('actTeam')
const opposition = document.getElementById('actOpposition')
const type = document.getElementById('actType')
const address1 = document.getElementById('form-address1')
const address2 = document.getElementById('form-address2')
const postcode = document.getElementById('form-postcode')
const suburb = document.getElementById('form-suburb')
const city = document.getElementById('form-city')
const country = document.getElementById('form-country')

const nextButton = document.getElementById("nextButton")
const prevButton = document.getElementById("prevButton")
const overviewTabButton = document.getElementById("overviewTabButton")
const locationTabButton = document.getElementById("locationTabButton")
const lineupTabButton = document.getElementById("lineupTabButton")

const formationStore = document.getElementById("formationStore")

const allTabsIds = Array.from(document.getElementsByClassName('nav-link')).map(element => element.getAttribute('id'));

const overviewTab = new bootstrap.Tab(overviewTabButton)
const locationTab = new bootstrap.Tab(locationTabButton)
const lineupTab = new bootstrap.Tab(lineupTabButton)

// If there is a server-side error, switch the active tab to the first appropriate one
lineupTabButton.addEventListener('show.bs.tab', getFormations)
if (serverLineupError != null) {
    lineupTab.show()
} else if (serverAddress1Error != null ||
    serverAddress2Error != null ||
    serverPostcodeError != null ||
    serverSuburbError != null ||
    serverCityError != null ||
    serverCountryError != null) {
    locationTab.show()
} else if (serverDescriptionError != null ||
    serverStartTimeError != null ||
    serverEndTimeError != null ||
    serverTeamError != null ||
    serverTypeError != null ) {
    overviewTab.show()
}

function setNavigationButtons() {
    let activeTabId = document.getElementsByClassName('nav-link active')[0].id
    switch (activeTabId) {
        case allTabsIds[0]:
            prevButton.disabled = true
            nextButton.textContent = "Next"
            locationTabButton.classList.remove("highlight")
            lineupTabButton.classList.remove("highlight")
            break;
        case allTabsIds[1]:
            prevButton.disabled = false
            nextButton.textContent = (lineupTabButton.classList.contains('disabled')) ? "Submit" : "Next"
            overviewTabButton.classList.add("highlight")
            lineupTabButton.classList.remove("highlight")
            break;
        case allTabsIds[2]:
            prevButton.disabled = false
            nextButton.textContent = "Submit"
            overviewTabButton.classList.add("highlight")
            locationTabButton.classList.add("highlight")
            break;
        default:
            throw new Error('Active tab is not defined');
    }
}

setNavigationButtons()

overviewTabButton.addEventListener('click', setNavigationButtons)
locationTabButton.addEventListener('click', setNavigationButtons)
lineupTabButton.addEventListener('click', setNavigationButtons)

const tabs = document.querySelectorAll('button[data-bs-toggle="pill"]')
tabs.forEach(tab => {
    tab.addEventListener('hide.bs.tab', function (event) {
        // If the next tab is after the current one, then the current one must be validated
        if (allTabsIds.indexOf(event.target.id) < allTabsIds.indexOf(event.relatedTarget.id) && !validate(event.target.id)) {
            event.preventDefault()
        }
    })
})

nextButton.addEventListener('click', function(){
    let activeTabId = document.getElementsByClassName('nav-link active')[0].id
    switch (activeTabId) {
        case allTabsIds[0]:
            locationTab.show()
            break;
        case allTabsIds[1]:
            if (lineupTabButton.classList.contains('disabled')) {
                // Submit form if there is no lineup
                document.getElementById("activityForm").submit();
            } else {
                lineupTab.show()
            }
            break;
        case allTabsIds[2]:
            // Submit form
            document.getElementById("activityForm").submit();
            break;
        default:
            throw new Error('Active tab is not defined');
    }
    setNavigationButtons()
})

prevButton.addEventListener('click', function(){
    let activeTabId = document.getElementsByClassName('nav-link active')[0].id
    switch (activeTabId) {
        case allTabsIds[0]:
            // No previous button
            break;
        case allTabsIds[1]:
            overviewTab.show()
            break;
        case allTabsIds[2]:
            locationTab.show()
            break;
        default:
            throw new Error('Active tab is not defined');
    }
    setNavigationButtons()
})

const descriptionError = document.getElementById('descError')
const activityStartError = document.getElementById('startTimeError')
const activityEndError = document.getElementById('endTimeError')
const teamError = document.getElementById('teamError')
const typeError = document.getElementById('typeError')
const address1Error = document.getElementById('address1Error')
const address2Error = document.getElementById('address2Error')
const postcodeError = document.getElementById('postcodeError')
const suburbError = document.getElementById('suburbError')
const cityError = document.getElementById('cityError')
const countryError = document.getElementById('countryError')

const lineupTabArea = document.getElementById('lineupTabArea')
const oppositionInputDisplay = document.getElementById('actOppositionDisplay')

type.addEventListener('change', toggleLineupTab)
team.addEventListener('change', toggleLineupTab)

document.addEventListener('DOMContentLoaded', toggleLineupTab)

function toggleLineupTab() {
    if ((type.value === "game" || type.value === "friendly")) {
        if (team.value !== '-1' && team.value !== '0') {
            lineupTabArea.hidden = false
            lineupTabButton.classList.remove('disabled')
        }
        oppositionInputDisplay.classList.remove('d-none')
    } else {
        lineupTabArea.hidden = true
        lineupTabButton.classList.add('disabled')
        oppositionInputDisplay.classList.add('d-none')
        opposition.value = '0'
    }
}

function validate(activeTabId) {
    let valid = true
    switch (activeTabId) {
        case allTabsIds[0]:
            const descriptionFormat = /^(?!$|\d+$|[^\p{L}]+$|^.{151,}$).*$/u
            if (description.value === '' || description.value === null || !description.value.match(descriptionFormat)) {
                descriptionError.innerText = 'Please enter a description made up of alphabetical characters and is no longer than 150 characters.'
                description.classList.add('is-invalid')
                valid = false
            } else {
                description.classList.remove('is-invalid')
            }
            const dateFormat = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/;
            if (Date.parse(activityEnd.value) <= Date.parse(activityStart.value)) {
                activityEndError.innerText = 'The end time of an activity cannot be before the start time.'
                activityStart.classList.add('is-invalid')
                activityEnd.classList.add('is-invalid')
                valid = false
            } else {
                activityStart.classList.remove('is-invalid')
                activityEnd.classList.remove('is-invalid')
                if (!activityStart.value.match(dateFormat)) {
                    activityStartError.innerText = 'Please enter a valid date.'
                    activityStart.classList.add('is-invalid')
                    valid = false
                } else {
                    activityStart.classList.remove('is-invalid')
                }

                if (!activityEnd.value.match(dateFormat)) {
                    activityEndError.innerText = 'Please enter a valid date.'
                    activityEnd.classList.add('is-invalid')
                    valid = false
                } else {
                    activityEnd.classList.remove('is-invalid')
                }
            }

            if (type.value === "None") {
                typeError.innerText = "Please select an option for type."
                type.classList.add('is-invalid')
                valid = false
            } else {
                type.classList.remove('is-invalid')
            }

            if (type.value !== "None") {
                if ((type.value === "game" || type.value === "friendly") && (team.value === "0")) {
                    teamError.innerText = "A team must be selected for this activity type."
                    team.classList.add('is-invalid')
                    valid = false
                } else {
                    team.classList.remove('is-invalid')
                }
            }

            break
        case allTabsIds[1]:
            const addressFormat = /^(?!.*!)(?![\d\s'\/,.\\-]*$)[\p{L}\d\s'\/,.\\-]+$/u;
            const postcodeFormat = /^(?:[\p{L}\p{N}]+-[\p{L}\p{N}]+)+|[\p{L}\p{N}]+$/u;
            const suburbFormat = /^(?!.*!)(?=.*\p{L})[\p{L}\d\s'\/,.\\-]+$/u;
            const cityFormat = /^(?=.*\p{L})[\p{L}\d\s'-\/,.]+$/u;
            const countryFormat = /^(?!.*[\d!])(?=.*\p{L})[ \p{L}\s',.-]+$/u;

            if (!address1.value.match(addressFormat)) {
                address1Error.innerText = "Please enter an address 1 containing at least one digit alongside any letters. Hyphens and apostrophes are also acceptable."
                address1.classList.add('is-invalid')
                valid = false
            } else {
                address1.classList.remove('is-invalid')
            }

            if (!address2.value.match(addressFormat) && !(address2.value === "") && !(address2.value === null)) {
                address2Error.innerText = "Address 2 must contain at least one digit alongside any letters. Hyphens and apostrophes are also acceptable."
                address2.classList.add('is-invalid')
                valid = false
            } else {
                address2.classList.remove('is-invalid')
            }

            if (!postcode.value.match(postcodeFormat)) {
                postcodeError.innerText = "Please enter a postcode which only contains numbers, letters or hyphens."
                postcode.classList.add('is-invalid')
                valid = false
            } else {
                postcode.classList.remove('is-invalid')
            }

            if (!suburb.value.match(suburbFormat) && !(suburb.value === "") && !(suburb.value === null)) {
                suburbError.innerText = "Suburb must contain only letters."
                suburb.classList.add('is-invalid')
                valid = false
            } else {
                suburb.classList.remove('is-invalid')
            }

            if (!city.value.match(cityFormat)) {
                cityError.innerText = "Please enter a city containing only letters and hyphens."
                city.classList.add('is-invalid')
                valid = false
            } else {
                city.classList.remove('is-invalid')
            }

            if (!country.value.match(countryFormat)) {
                countryError.innerText = "Please enter a country containing only letters and hyphens."
                country.classList.add('is-invalid')
                valid = false
            } else {
                country.classList.remove('is-invalid')
            }
            break
    }
    return valid
}

function getFormations() {
    let url = `${getCorrectBaseUrl()}/formations/` + team.value;
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
            const availablePlayers = document.getElementById("availablePlayers")
            const substitutionBox = document.getElementById("substitutionBox")
            const formationArea = document.getElementById("formationArea")
            const clearSelectedFormationParent = document.getElementById("clearSelectedFormationParent")

            // Clear any existing children
            availablePlayers.textContent = ''
            substitutionBox.textContent = ''
            formationArea.textContent =''

            formationArea.appendChild(clearSelectedFormationParent)


            for (const player in successResp.teamPlayerList){
                // Available Players Box
                const availablePlayersDiv = document.createElement('div')
                availablePlayersDiv.classList.add("col-md-4")
                availablePlayersDiv.classList.add("draggable_player_image")
                availablePlayersDiv.id = 'player_' + successResp.teamPlayerList[player]['userId']
                availablePlayersDiv.draggable = true

                const availablePlayersImg = document.createElement('img')
                availablePlayersImg.src = successResp.teamPlayerList[player]['profilePicName']
                availablePlayersImg.width = 60

                const availablePlayersSpan = document.createElement('span')
                availablePlayersSpan.textContent = successResp.teamPlayerList[player]['firstName']

                availablePlayersDiv.appendChild(availablePlayersImg)
                availablePlayersDiv.appendChild(availablePlayersSpan)
                availablePlayers.appendChild(availablePlayersDiv)


                // Substitution Box
                const substitutionBoxDiv = document.createElement('div')
                substitutionBoxDiv.classList.add("col-md-4")
                substitutionBoxDiv.classList.add("draggable_player_image")
                substitutionBoxDiv.classList.add("substitution_element")
                substitutionBoxDiv.id = 'substitution_' + successResp.teamPlayerList[player]['userId']
                substitutionBoxDiv.hidden = true

                const substitutionBoxImg = document.createElement('img')
                substitutionBoxImg.src = successResp.teamPlayerList[player]['profilePicName']
                substitutionBoxImg.width = 60

                const substitutionBoxSpan = document.createElement('span')
                substitutionBoxSpan.textContent = successResp.teamPlayerList[player]['firstName']

                substitutionBoxDiv.appendChild(substitutionBoxImg)
                substitutionBoxDiv.appendChild(substitutionBoxSpan)
                substitutionBox.appendChild(substitutionBoxDiv)
            }

            for (const formation in successResp.teamFormationList) {
                const formationCardDiv = document.createElement('div')
                formationCardDiv.classList.add("col")

                const formationCard = document.createElement('div')
                formationCard.classList.add("card")
                formationCard.classList.add("text-center")
                formationCard.id = "formationCard" + successResp.teamFormationList[formation]['id']
                formationCard.addEventListener("click", function() {updateFormationLayout(successResp.teamFormationList[formation]['playersPerSectionString'],successResp.teamFormationList[formation]['id'], formationCard, successResp.teamFormationList[formation]['pitchString']);})

                const formationCardHeader = document.createElement("h5")
                formationCardHeader.classList.add("card-header")

                const formationCardHeaderSpan = document.createElement("span")
                formationCardHeaderSpan.id = "formationCardText" + successResp.teamFormationList[formation]['id']
                formationCardHeaderSpan.textContent = successResp.teamFormationList[formation]['playersPerSectionString']

                const formationCardBody = document.createElement("div")
                formationCardBody.classList.add("card-body")

                const formationCardText = document.createElement("p")
                formationCardText.classList.add("card-text")
                formationCardText.classList.add("my-auto")
                formationCardText.id = "formationCardSport" + successResp.teamFormationList[formation]['id']
                formationCardText.textContent = successResp.teamFormationList[formation]['sportName']

                formationCardHeader.appendChild(formationCardHeaderSpan)
                formationCardBody.appendChild(formationCardText)
                formationCard.appendChild(formationCardHeader)
                formationCard.appendChild(formationCardBody)
                formationCardDiv.appendChild(formationCard)
                formationArea.appendChild(formationCardDiv)

                if (successResp.teamFormationList[formation]['id'].toString() === formationStore.value.toString()) {
                    let playersPerSection = successResp.teamFormationList[formation]['playersPerSection']
                    let formationPlayerList = formationPlayerStore.value.split(",")
                    formationCard.click()
                    let playerIndex = 0
                    for (const row in playersPerSection) {
                        for (let i = 0; i < parseInt(playersPerSection[row]); i++) {
                            const formationSlot = document.getElementById("position_"+i+"_"+row).parentElement
                            const playerId = "player_" + formationPlayerList[playerIndex]
                            populateSlot(playerId, formationSlot)
                            playerIndex++;
                        }
                    }
                    for (let i = playerIndex; i < formationPlayerList.length; i++) {
                        populateSubstitution("player_" + formationPlayerList[i])
                    }
                }
            }
        })
}