const followingTableBody = document.getElementById('followingTableBody');
let stop_tr = 0;

function getFollowing() {
    let url = `${getCorrectBaseUrl()}/users/getFollowing`;
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

            followingTableBody.innerText = '';
            for (const user in successResp) {

                const followTableRow = document.createElement('tr');
                followTableRow.setAttribute('method','post');
                followTableRow.classList.add('form');
                followTableRow.onclick = function () {
                    if (stop_tr === 0) {
                        window.location = currentUrl.origin + base + '/otherProfile?email=' + successResp[user]['email'];
                    }
                }

                const followTableRowAvatar = document.createElement('td');
                followTableRowAvatar.classList.add('w-25');

                const followTableRowAvatarImg = document.createElement('img');
                followTableRowAvatarImg.src = successResp[user]['profilePicName'];
                followTableRowAvatarImg.classList.add('img-fluid', 'rounded-circle', 'shadow-4');
                followTableRowAvatarImg.style.width = '50px';

                const followTableRowFirstName = document.createElement('td');
                followTableRowFirstName.innerText = successResp[user]['firstName'];
                followTableRowFirstName.classList.add('w-25', 'overflow-auto');
                followTableRowFirstName.style.maxWidth = '20rem';
                followTableRowFirstName.setAttribute('th:fName', successResp[user]['firstName'])

                const followTableRowLastName = document.createElement('td');
                followTableRowLastName.innerText = successResp[user]['lastName'];
                followTableRowLastName.classList.add('w-25', 'overflow-auto');
                followTableRowLastName.style.maxWidth = '20rem';
                followTableRowLastName.setAttribute('th:lName', successResp[user]['lastName'])

                const followTableRowFollowingIndication = document.createElement('td');
                followTableRowFollowingIndication.classList.add('w-25')

                const followTableRowFollowingIndicationButton = document.createElement('button');
                followTableRowFollowingIndicationButton.classList.add('btn', 'btn-primary');
                followTableRowFollowingIndicationButton.setAttribute('type', 'submit');
                followTableRowFollowingIndicationButton.innerText = successResp[user]['areFriends'] ? "Friends" : "Following";
                followTableRowFollowingIndicationButton.onmouseout = () => { followTableRowFollowingIndicationButton.innerText = successResp[user]['areFriends'] ? "Friends" : "Following"; }
                followTableRowFollowingIndicationButton.onmouseover = () => { followTableRowFollowingIndicationButton.innerText = "Unfollow"};
                followTableRowFollowingIndicationButton.onclick = () => {
                    toggleFollow(successResp[user]['firstName'], successResp[user]['lastName']);
                };


                followTableRow.appendChild(followTableRowAvatar);
                followTableRowAvatar.appendChild(followTableRowAvatarImg);
                followTableRow.appendChild(followTableRowFirstName);
                followTableRow.appendChild(followTableRowLastName);
                followTableRowFollowingIndication.appendChild(followTableRowFollowingIndicationButton);
                followTableRow.appendChild(followTableRowFollowingIndication);
                followingTableBody.appendChild(followTableRow);
            }
        })
}



function toggleFollow(fName, lName) {
    stop_tr = 1;
    setTimeout("stop_tr=0;", 2000);

    let data = {
        "fName": fName,
        "lName": lName
    };

    let url = `${getCorrectBaseUrl()}/users/unfollow`;
    console.log(data)
    fetch(url, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'application/json',
        },
        body: JSON.stringify(data),
    })
        .then((response) => {
            let status = response.status;
            if (status === 200) {
                getFollowing();
                return response;
            } else {
                throw new Error('Request failed with status ' + status);
            }
        })
        .then((jsonData) => {
            // Handle successful response here
            console.log(jsonData);
        })
        .catch((error) => {
            // Handle fetch or other errors here
            console.error('Fetch error:', error);
        });

    // window.location = currentUrl.origin + base;
}

getFollowing();