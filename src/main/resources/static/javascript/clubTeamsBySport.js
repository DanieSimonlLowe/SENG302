// Calls checkCheckboxes() if when editing, to disable any checkboxes if needed.
document.addEventListener("DOMContentLoaded", function () {
    const checkboxes = document.querySelectorAll('.form-check-input');
    function checkCheckboxes() {
        checkboxes.forEach(function (checkbox) {
            if (checkbox.checked) {
                getTeamsBySport(checkbox.value)
            }
        });
    }
    checkCheckboxes();
});

// Will disable the different checkboxes based on sport when a checkbox is checked.
function getTeamsBySport(teamId) {

    // Select all checkbox input elements on the page
    const team = document.querySelectorAll('input[type="checkbox"]')
    // Get the checkbox element with a specific ID based on the value of 'teamId'
    let checkBoxList = document.getElementById('clubTeam' + teamId)
    // Check if the targeted checkbox is not checked and none of the checkboxes in 'team' are checked
    if (checkBoxList != null) {
        if (!checkBoxList.checked && !Array.from(team).some(checkBox => checkBox.checked)) {
            let checkboxesList = document.getElementsByName('clubTeam');
            // Enable all checkboxes
            for (let checkboxIndex in checkboxesList) {
                checkboxesList[checkboxIndex].disabled = false;
            }
            return
        }
    }

    let url = `${getCorrectBaseUrl()}/clubs/getTeams/` + teamId;

    // Gets the teams based on that sport
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
                // Disables the checkboxes
                let checkboxesList = document.getElementsByName('clubTeam');
                console.log(checkboxesList)
                for (let checkboxIndex in checkboxesList) {
                    checkboxesList[checkboxIndex].disabled = true;
                }
                // Ables the checkboxes bases off the received jason data
                for (let checkboxIndex in data) {
                    let teamId = data[checkboxIndex].id;
                    const element = document.getElementById('clubTeam' + teamId);
                    if (element != null) {
                        element.disabled = false;
                    }
                }
            });
            return;
        }
        throw new Error('Bad Request');
    }).then((successResp) => {
    }).catch((error) => {
        console.log(error.toString());
    });
}