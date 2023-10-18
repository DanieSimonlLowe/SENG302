let team1Id;
let team1Img;
let team1Name;
let team2Id;
let team2Img;
let team2Name;
let activityId;
let activityType;

/**
 * Updates the stats modal for the given activity.
 * This ensures there is only ever one modal loaded at a time.
 * @param id The id of the activity to be loaded.
 */

const lineupTabButton = document.getElementById("lineupTabButton");
lineupTabButton.addEventListener('show.bs.tab', function (event) {
    clearLineup();
    getLineup();
})

function changeModal(id) {
    const url =`${getCorrectBaseUrl()}/activities/getActivityDetails/`+id;
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
            activityId = successResp.activityId;
            activityType = successResp.activityType;

            team1Id = successResp.teamId;
            team1Img = successResp.teamImg;
            team1Name = successResp.teamName;
            team2Id = successResp.oppositionId;
            team2Img = successResp.oppositionImg;
            team2Name = successResp.oppositionName;

            if (team2Id === null) {
                team2Id = team1Id;
                team2Img = team1Img;
                team2Name = team1Name;
            }

            document.getElementById("lineupActivityId").value = activityId;
            document.getElementById("factActId").value = activityId;


            getFacts();

            if (activityType === 'COMPETITION') {
                document.getElementById("scoringButton").hidden = true;
                document.getElementById("substitutionButton").hidden = true;
            } else if (activityType === 'OTHER') {
                document.getElementById("scoringButton").hidden = true;
                document.getElementById("substitutionButton").hidden = true;
            } else {
                document.getElementById("teamSubsitutionButton").onclick = () => {switchSubstitutionTeam(team1Id, team1Img, team1Name)}
                document.getElementById("teamSubsitutionButton").text = team1Name;
                document.getElementById("oppositionSubstitutionButton").onclick = () => {switchSubstitutionTeam(team2Id, team2Img, team2Name)}
                document.getElementById("oppositionSubstitutionButton").text = team2Name;
                getScores();
                getSubstitutions(activityId);
            }
        })
}

