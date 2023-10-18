const followingTeamTableBody = document.getElementById('followingTeamTableBody');

function getFollowingTeams() {
    let url = `${getCorrectBaseUrl()}/users/getFollowingTeams`;
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

            if (successResp.length == 0) {
                const text = document.createTextNode('No teams to display.');
                followingTeamTableBody.appendChild(text);
            } else {
                for (const team in successResp) {
                    const followTeamTableRow = document.createElement('tr');
                    followTeamTableRow.onclick = function () {
                        window.location = currentUrl.origin + base + '/teamProfile?id=' + successResp[team]['teamId'];
                    }

                    const followTeamTableRowAvatar = document.createElement('td');
                    followTeamTableRowAvatar.classList.add('w-25')

                    const followTeamTableRowAvatarImg = document.createElement('img');
                    followTeamTableRowAvatarImg.src = successResp[team]['teamPicName'];
                    followTeamTableRowAvatarImg.classList.add('img-fluid', 'rounded-circle', 'shadow-4');
                    followTeamTableRowAvatarImg.style.width = '50px';

                    const followTeamTableRowName = document.createElement('td');
                    followTeamTableRowName.innerText = successResp[team]['teamName'];
                    followTeamTableRowName.classList.add('w-50', 'overflow-auto');
                    followTeamTableRowName.style.maxWidth = '20rem';

                    const followTeamTableRowFollowingIndication = document.createElement('td');
                    followTeamTableRowFollowingIndication.classList.add('w-25')

                    const followTeamTableRowFollowingIndicationButton = document.createElement('button');
                    followTeamTableRowFollowingIndicationButton.classList.add('btn', 'btn-primary');
                    followTeamTableRowFollowingIndicationButton.innerText = "Following";
                    followTeamTableRowFollowingIndicationButton.onmouseout = () => { followTeamTableRowFollowingIndicationButton.innerText = "Following" };
                    followTeamTableRowFollowingIndicationButton.onmouseover = () => { followTeamTableRowFollowingIndicationButton.innerText = "Unfollow"};


                    followTeamTableRow.appendChild(followTeamTableRowAvatar);
                    followTeamTableRowAvatar.appendChild(followTeamTableRowAvatarImg);
                    followTeamTableRow.appendChild(followTeamTableRowName);
                    followTeamTableRowFollowingIndication.appendChild(followTeamTableRowFollowingIndicationButton);
                    followTeamTableRow.appendChild(followTeamTableRowFollowingIndication);
                    followingTeamTableBody.appendChild(followTeamTableRow);
                }
            }
        })
}


getFollowingTeams();