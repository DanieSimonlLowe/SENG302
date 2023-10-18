const deletePostButton = document.getElementById('deletePostButton');
let openReplies;

const isManager = document.getElementById("feed-is-manager").value === 'true'

document.addEventListener('DOMContentLoaded', () => {
    openReplies = {};
});

function deletePost(postId) {
    const url = `${getCorrectBaseUrl()}/feed/delete/` + postId;
    fetch(url, {
        method: 'DELETE',
        headers: {
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'application/json',
        },
    }).then((response) => {
        let status = response.status;
        if (status === 200) {
            return response;
        }
        throw new Error('Bad Request');
    })
        .then(() => {
            location.reload();
        })
        .catch((error) => {
            console.log(error);
        });
}


function deleteComment(commentId) {
    const url = `${getCorrectBaseUrl()}/feed/deleteComment/` + commentId;
    fetch(url, {
        method: 'DELETE',
        headers: {
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'application/json',
        },
    }).then((response) => {
        let status = response.status;
        if (status === 200) {
            return response;
        }
        throw new Error('Bad Request');
    })
        .then(() => {
            location.reload();
        })
        .catch((error) => {
            console.log(error);
        });
}

function createCommentElement(commentsDiv, comment, clubId, isReply, postId) {
    const commentDiv = document.createElement('div');
    commentDiv.classList.add('row', 'mb-5');
    if (isReply) {
        commentDiv.classList.add('w-70')
    }

    // Creation of profile picture
    const commentProfilePicDiv = document.createElement('div');
    commentProfilePicDiv.classList.add('col-2', 'd-flex', 'justify-content-center', 'profile-container-small');
    commentProfilePicDiv.style.height = "5rem";

    const commentProfileImage = document.createElement('img');
    commentProfileImage.classList.add('img-fluid', "rounded-circle");
    commentProfileImage.style.aspectRatio = "1/1";
    commentProfileImage.style.border = comment.customBorderStyle;
    commentProfileImage.setAttribute('src', comment.userImage);
    commentProfilePicDiv.appendChild(commentProfileImage);
    commentDiv.appendChild(commentProfilePicDiv);

    // Creation of comment content
    const commentContentDiv = document.createElement('div');
    commentContentDiv.classList.add('col-10', 'd-flex', 'flex-column');

    const commentAuthorAndTimeDiv = document.createElement('div');
    commentAuthorAndTimeDiv.classList.add('d-flex', 'justify-content-between');

    // Creation of author name
    const commentAuthor = document.createElement('span');
    commentAuthor.classList.add('h3', 'fw-bold');
    commentAuthor.textContent = comment.userName;
    commentAuthorAndTimeDiv.appendChild(commentAuthor);

    // Creation of time display
    const commentTime = document.createElement('p');
    commentTime.classList.add('text-muted', 'fst-italic', 'me-3');
    commentTime.textContent = formatRelativeTime(comment.commentDate);
    commentAuthorAndTimeDiv.appendChild(commentTime);
    commentContentDiv.appendChild(commentAuthorAndTimeDiv);


    // Creation of comment content
    const commentText = document.createElement('p');
    commentText.classList.add('text-start');
    commentText.textContent = comment.commentMessage;
    commentContentDiv.appendChild(commentText);

    commentDiv.appendChild(commentContentDiv);

    if (isManager) {
        const addCommentButtonDiv = document.createElement('div');
        addCommentButtonDiv.classList.add('col-12', 'd-flex', 'justify-content-between', 'align-items-between');
        const button = document.createElement('button')
        button.textContent = "Delete"
        button.type = 'button'
        button.classList.add('btn', 'btn-primary', 'btn-sm');
        button.onclick = () => {deleteComment(comment.commentId)}

        addCommentButtonDiv.appendChild(button)

        commentContentDiv.appendChild(addCommentButtonDiv);
    }

    if (!isReply) {

        // Create a div for reply button to sit in
        const replyButtonDiv = document.createElement("div");
        replyButtonDiv.classList.add("col-12", "d-flex", "justify-content-end");

        // Create a button element
        const button = document.createElement("button");

        // Set the class and style attributes
        button.classList.add("btn");
        button.style = "background: transparent !important";

        // Set the data attribute
        button.setAttribute("data-toggled", "false");

        // Set the id attribute using string interpolation
        button.id = "reply_button_" + postId + "_" + comment.commentId;

        // Create the SVG element
        const svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
        svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");
        svg.setAttribute("width", "24");
        svg.setAttribute("height", "24");
        svg.classList.add("bi", "bi-chat-square-text");
        svg.setAttribute("viewBox", "0 -960 960 960");

        // Create the path element and set it's 'd' attribute
        const path = document.createElementNS("http://www.w3.org/2000/svg", "path");
        path.setAttribute("d", "M760-220.001V-360q0-53.846-38.077-91.924-38.077-38.077-91.924-38.077H254.922l154 154.001-42.768 42.153L140.001-520l226.153-226.153L408.922-704l-154 154.001h375.077q78.769 0 134.384 55.615Q819.999-438.769 819.999-360v139.999H760Z");

        // Append the path element to the SVG element
        svg.appendChild(path);

        // Append the SVG element to the button
        button.appendChild(svg);

        // Create the Show Replies text
        const showRepliesText = document.createElement("p");
        showRepliesText.setAttribute("id", "show_replies_text_" + postId + "_" + comment.commentId);
        showRepliesText.classList.add("text-muted", "fst-italic", "me-3");
        if (openReplies[postId + "_" + comment.commentId]) {
            showRepliesText.textContent = "Hide Replies";
        } else {
            showRepliesText.textContent = "Show Replies";
        }

        // Append the Show Replies text to the button
        button.appendChild(showRepliesText);

        // Append the button to the replyButtonDiv
        replyButtonDiv.appendChild(button);

        // Append the replyButtonDiv to the commentDiv
        commentDiv.appendChild(replyButtonDiv);

        const replyDiv = document.createElement("div");
        let key = postId + "_" + comment.commentId;
        replyDiv.hidden = !openReplies[key];
        button.onclick = () => {
            if (replyDiv.hidden) {
                openReplies[key] = true;
                replyDiv.hidden = false;
                document.getElementById("show_replies_text_" + postId + "_" + comment.commentId).textContent = "Hide Replies";
            } else {
                delete openReplies[key];
                replyDiv.hidden = true;
                document.getElementById("show_replies_text_" + postId + "_" + comment.commentId).textContent = "Show Replies";
            }
        }

        const borderDiv = document.createElement("div");
        borderDiv.classList.add('bord')
        borderDiv.style.paddingLeft = '2%';
        borderDiv.style.marginLeft = '5%';
        borderDiv.style.borderStyle = 'none none solid solid';
        borderDiv.style.borderWidth = '1px';

        replyDiv.append(borderDiv)
        createAddCommentField(borderDiv, postId, comment.commentId, clubId,true)

        for (const reply of comment.replies) {
            createCommentElement(borderDiv, reply, clubId, true, postId);
        }
        commentDiv.appendChild(replyDiv);
    }

    commentsDiv.append(commentDiv);
}

function getComments(postId, clubId, isManualRefresh) {

    // Check if this function is being called by button click or other function
    console.log(isManualRefresh);
    if (isManualRefresh) {
        // Check if the button is toggled or not
        const button = document.getElementById(`comment_button_${postId}`);
        console.log(`${button.dataset.toggled}`);
        if (button.dataset.toggled === "true") {
            // Button is pressed so hide the comments and return
            let commentsDiv = document.getElementById(`comments_${postId}`);

            while (commentsDiv.firstChild) {
                commentsDiv.removeChild(commentsDiv.firstChild);
            }
            button.dataset.toggled = "false";
            return;
        } else {
            // Set button to be toggled
            button.dataset.toggled = "true";
        }
    }

    const url = getCorrectBaseUrl() + '/feed/getComments/' + postId + '&' + clubId;
    fetch(url, {
        method: 'GET',
        headers: {
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'application/json',
        },
    }).then((response) => {
        let status = response.status;
        if (status === 200) {
            return response.json();
        }
        throw new Error('Bad Request');
    })
        .then((data) => {

            const commentsDiv = document.getElementById('comments_' + postId);

            // Make sure to clear
            while (commentsDiv.firstChild) {
                commentsDiv.removeChild(commentsDiv.firstChild);
            }

            createAddCommentField(commentsDiv, postId, -1 , clubId, false);

            for (let comment of data) {
                createCommentElement(commentsDiv, comment, clubId, false, postId);
            }
        })
        .catch((error) => {
            console.log(error);
        });
}

function formatRelativeTime(dateString) {
    // Parse the input date string into a Date object
    const date = new Date(dateString);

    // Get the current date and time
    const currentDate = new Date();

    // Calculate the time difference in milliseconds
    const timeDifference = currentDate - date;

    // Convert milliseconds to seconds
    const secondsDifference = Math.floor(timeDifference / 1000);

    // Calculate the number of minutes, hours, days, and months
    const minutes = Math.floor(secondsDifference / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);
    const months = Math.floor(days / 30); // Assuming 30 days per month on average

    // Format the relative time
    if (minutes < 1) {
        return `${secondsDifference} seconds ago`;
    } else if (hours < 1) {
        return `${minutes} ${minutes === 1 ? "minute" : "minutes"} ago`;
    } else if (days < 1) {
        return `${hours} ${hours === 1 ? "hour" : "hours"} ago`;
    } else if (months < 1) {
        return `${days} ${days === 1 ? "day" : "days"} ago`;
    } else {
        return `${months} ${months === 1 ? "month" : "months"} ago`;
    }
}
function createAddCommentField(commentsDiv, postId, parentId, clubId, isReply) {

    const addCommentDiv = document.createElement('div');
    addCommentDiv.classList.add('row', 'mb-4', 'justify-content-center');
    if (isReply) {
        // addCommentDiv.style.paddingLeft = '10%';
    }

    // Text that states "Add a comment" at the top
    const addCommentTextDiv = document.createElement('div');
    addCommentTextDiv.classList.add('col-12');

    const addCommentText = document.createElement('p');
    addCommentText.classList.add('mb-0', 'text-muted', 'text-start')
    if (isReply) {
        addCommentText.textContent = 'Add a reply';
    } else {
        addCommentText.textContent = 'Add a comment';
    }

    addCommentTextDiv.appendChild(addCommentText);
    addCommentDiv.appendChild(addCommentTextDiv);


    const txtAreaDiv = document.createElement('div');
    txtAreaDiv.classList.add('col-12', 'justify-content-center');

    const txtArea = document.createElement('textarea');
    if (isReply) {
        txtArea.setAttribute('id', 'replyMessage' + postId + '_' + parentId );
    } else {
        txtArea.setAttribute('id', 'commentMessage' + postId);
    }

    txtArea.setAttribute('rows', '3');
    txtArea.setAttribute('cols', '50');
    txtArea.classList.add('form-control');
    txtArea.style.width = "98%";
    txtAreaDiv.appendChild(txtArea);

    addCommentDiv.appendChild(txtArea);

    const addCommentButtonDiv = document.createElement('div');
    addCommentButtonDiv.classList.add('col-12', 'd-flex', 'justify-content-between', 'align-items-between');

    const addCommentButton = document.createElement('button');
    addCommentButton.setAttribute('type', 'button');
    if (isReply) {
        addCommentButton.setAttribute('onclick', 'addComment(' + postId + ', ' + parentId + ', ' + clubId + ')');
    } else {
        addCommentButton.setAttribute('onclick', 'addComment(' + postId + ', -1, ' + clubId + ')');
    }

    addCommentButton.classList.add('btn', 'btn-primary', 'btn-sm');
    addCommentButton.style.marginTop = "5px";
    if (isReply) {
        addCommentButton.textContent = 'Post reply'
    } else {
        addCommentButton.textContent = 'Post comment';
    }

    const errorMessageText = document.createElement('p');
    errorMessageText.classList.add('fst-italic');
    errorMessageText.style.color = 'red';

    if (isReply) {
        errorMessageText.setAttribute('id', 'reply' + postId + '_' + parentId + 'CommentErrorMessage');
    } else {
        errorMessageText.setAttribute('id', 'post' + postId + 'CommentErrorMessage');
    }

    addCommentButtonDiv.appendChild(errorMessageText);
    addCommentButtonDiv.appendChild(addCommentButton);

    addCommentDiv.appendChild(addCommentButtonDiv);

    commentsDiv.appendChild(addCommentDiv);
}

function addComment(postId, parentId, clubId) {
    const url = getCorrectBaseUrl() + '/feed/addComment';
    let commentMessageField = 'commentMessage' + postId;
    let commentErrorMessageField = 'post' + postId + 'CommentErrorMessage';
    if (parentId !== -1) {
        commentMessageField = 'replyMessage' + postId + '_' + parentId;
        commentErrorMessageField = 'reply' + postId + '_' + parentId + 'CommentErrorMessage';
    }

    const commentMessage = document.getElementById(commentMessageField).value;

    const regexPattern = /^(?!\s+$).+/;



    if(!regexPattern.test(commentMessage)) {
        document.getElementById(commentErrorMessageField).textContent = 'Invalid comment format';
        return;
    } else if (commentMessage.length > 400) {
        document.getElementById(commentErrorMessageField).textContent = 'Comment can be at most 400 characters';
        return;
    } else {
        document.getElementById(commentErrorMessageField).textContent = '';
    }


    const data = {
        message: commentMessage,
        parentCommentId: parentId,
        postId: postId
    }
    fetch(url, {
        method: 'POST',
        headers: {
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
    }).then((response) => {
        let status = response.status;
        if (status === 201) {
            return response;
        }
        if (status === 403) {
            createFlagToast("Your comment was flagged, and could not be posted.");
            return response;
        }
        if (states = 400) {
            createFlagToast("Your comment was flagged, and will be reviewed by a manager before it is visible.");
            return response;
        }

        throw new Error('Bad Request');
    })
        .then(() => {
            getComments(postId, clubId, false);
        })
        .catch((error) => {
            console.log(error);
        });
}


function unflagPost(postId) {
    const url = `${getCorrectBaseUrl()}/feed/unflag/` + postId;
    fetch(url, {
        method: 'POST',
        headers: {
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'application/json',
        },
    }).then((response) => {
        let status = response.status;
        if (status === 200) {
            return response;
        }
        throw new Error('Bad Request');
    })
        .then(() => {
            location.reload();
        })
        .catch((error) => {
            console.log(error);
        });
}


function unflagComment(commentId) {
    const url = `${getCorrectBaseUrl()}/feed/unflagComment/` + commentId;
    fetch(url, {
        method: 'POST',
        headers: {
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'application/json',
        },
    }).then((response) => {
        let status = response.status;
        if (status === 200) {
            return response;
        }
        throw new Error('Bad Request');
    })
        .then(() => {
            location.reload();
        })
        .catch((error) => {
            console.log(error);
        });
}