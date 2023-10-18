window.onload = function () {
    getNewAlerts();
}
setInterval(getNewAlerts, 5000);

// variables for the alerts
let totalAlerts = 0;
let readAlerts = 0;


// function to set the read alerts
function setReadAlerts() {
    const url = getCorrectBaseUrl() + '/userAlertNumber/' + document.getElementById("AlertUserId").value + '/updateReadPosts'
    fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'application/json',
        },
        body: JSON.stringify({
            readAlerts: totalAlerts
        })
    }).then((response) => {
        let status = response.status;
        if (status === 200) {
            response.json().then(() => {
                readAlerts = totalAlerts;
            });
        }
    });
}

// function to get the new alerts
function getNewAlerts() {
    const alertNum = document.getElementById("numAlerts");

    // function to get the total notifications
    function totalNotifications() {
        const url = getCorrectBaseUrl() + '/userAlerts/' + document.getElementById("AlertUserId").value;
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
                    totalAlerts = data.length;
                    getReadAlerts();
                });
            }
        });
    }

    // function to get the read notifications
    function getReadAlerts() {
        const url = getCorrectBaseUrl() + '/userAlertNumber/' + document.getElementById("AlertUserId").value;
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
                    readAlerts = data.readAlerts;
                    updateNotifications();
                });
            }
        });
    }

    // function to update the notifications
    function updateNotifications() {
        if (totalAlerts > readAlerts) {
            let total = totalAlerts - readAlerts;
            let oldTotal = getCookie("oldTotal");
            if (oldTotal === undefined || oldTotal === null) {
                createCookie("oldTotal", "-1", 5);
                oldTotal = "0";
            }

            if (total !== parseInt(oldTotal)) {
                if (total > oldTotal) {
                    showToast("New Notifications", "You have " + (total - oldTotal).toString() + " new notifications!");
                }
                createCookie("oldTotal", total, 5);
            }
            alertNum.innerText = total.toString();

        } else if (totalAlerts < readAlerts){
            createCookie("oldTotal", "0", 5);
            alertNum.innerText = "0";
            setReadAlerts();
        }
        else {
            createCookie("oldTotal", "0", 5);
            alertNum.innerText = "0";
        }
    }
    totalNotifications();
}


// function to open the personal feed
function openAlerts() {
    const baseUrl = getCorrectBaseUrl()
    const alertNum = document.getElementById("numAlerts");
    alertNum.innerText = "0";
    setReadAlerts();

    // function to get the personal feed
    function load() {
        const url = baseUrl+'/userAlerts/' + document.getElementById("AlertUserId").value;
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
                    update(data);
                });
            }
        })
    }

    function displayElapsedTime(postDateTime) {
        const postDate = new Date(postDateTime);
        const currentDate = new Date();
        const timeDifference = currentDate - postDate;

        const seconds = Math.floor(timeDifference / 1000);
        const minutes = Math.floor(seconds / 60);
        const hours = Math.floor(minutes / 60);
        const days = Math.floor(hours / 24);

        let elapsed = '';
        if (days > 0) {
            elapsed = `${days} days ago`;
        } else if (hours > 0) {
            elapsed = `${hours} hours ago`;
        } else if (minutes > 0) {
            elapsed = `${minutes} minutes ago`;
        } else {
            elapsed = `${seconds} seconds ago`;
        }

        return elapsed;
    }

    // function to update the personal feed
    function update(data) {
        const alertBox = document.getElementById("notificationDropdownArea")
        alertBox.innerHTML = ""
        if (data.length === 0) {
            const dropdownHeader = document.createElement("h6")
            dropdownHeader.classList = ["dropdown-header"]
            dropdownHeader.innerText = "no alerts"
            alertBox.appendChild(dropdownHeader)
        } else {
            const title = document.createElement("h6");
            title.classList = ["dropdown-header"]
            title.classList = ['text-center']
            alertBox.appendChild(title)

            const dividerHead = document.createElement('hr')
            dividerHead.classList = ['hr']
            title.innerText = "notifications"
            alertBox.appendChild(dividerHead)

            data.forEach(feedPost => {
                const alert = document.createElement("a");
                alert.classList = ["content notificationLinkContainer"]
                alert.style = 'color: black'
                const header = document.createElement("h6");
                header.textContent = "by " + feedPost.ownerName;
                header.classList = ['text-center']

                const metaBody = document.createElement('div')
                metaBody.classList = ['container']
                const body = document.createElement("div");
                body.classList =  ['row']


                if (feedPost.attachment !== null && feedPost.attachment.length > 0) {
                    const col1 = document.createElement('div')
                    col1.classList = ['col-md-auto']
                    const img = document.createElement('img');
                    img.src = baseUrl + "/" + feedPost.attachment;
                    img.classList = ["rounded-circle pt-2"]
                    img.style = "width: 65px"
                    col1.appendChild(img)
                    body.appendChild(col1)
                }
                const text = document.createElement('span');
                text.classList = ["notificationMessage"]
                const col2 = document.createElement('div')
                col2.classList = ['col']
                text.innerText = feedPost.message
                col2.appendChild(text)
                body.appendChild(col2)

                const div = document.createElement('div')
                div.style = 'text-align: end;'
                const time = document.createElement('span')
                time.textContent = displayElapsedTime(feedPost.postDateTime);
                console.log(feedPost.postDateTime)
                div.appendChild(time)
                body.appendChild(div)

                const divider = document.createElement('hr')
                divider.classList = ['hr']

                metaBody.appendChild(body)
                alert.appendChild(header)
                alert.appendChild(metaBody)
                alert.appendChild(divider)
                alertBox.appendChild(alert)
            })
        }

        display();
    }

    // function to display the personal feed
    function display() {
        const alertBtn = document.getElementById("notificationsDropdownMenu");
        if (!alertBtn.classList.contains("show")) {
            Array.from(document.querySelectorAll('.notificationUnread')).forEach(function(el) {
                el.style.display = "none";
                el.classList.remove('notificationUnread');
            });

            Array.from(document.querySelectorAll('.notificationUnread')).forEach(
                (el) => el.classList.remove('notificationUnread')
            );
        }
    }

    load();
}

function showToast(title, content) {
    // Create a new toast element
    let toast = document.createElement('div');
    toast.className = 'toast';
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');

    // Create the toast header
    let toastHeader = document.createElement('div');
    toastHeader.className = 'toast-header';
    let strong = document.createElement('strong');
    strong.className = 'me-auto';
    strong.textContent = title;
    let closeButton = document.createElement('button');
    closeButton.type = 'button';
    closeButton.className = 'btn-close';
    closeButton.setAttribute('data-bs-dismiss', 'toast');
    closeButton.setAttribute('aria-label', 'Close');
    toastHeader.appendChild(strong);
    toastHeader.appendChild(closeButton);

    // Create the toast body
    let toastBody = document.createElement('div');
    toastBody.className = 'toast-body';
    toastBody.textContent = content;

    // Add header and body to the toast element
    toast.appendChild(toastHeader);
    toast.appendChild(toastBody);

    // Get the toast container
    let toastContainer = document.getElementById('toastContainer');

    // Append the toast to the container
    toastContainer.appendChild(toast);

    // Create a new Bootstrap Toast instance
    let toastInstance = new bootstrap.Toast(toast);

    // Show the toast
    toastInstance.show();

    // Automatically fade the toast after the specified duration (in milliseconds)
    setTimeout(function () {
        toastInstance.hide();
        // Remove the toast from the DOM after it's hidden
        toast.addEventListener('hidden.bs.toast', function () {
            toast.remove();
        });
    }, 10000);
}

// Function to create a cookie with an optional expiration date
function createCookie(name, value, daysToExpire) {
    const expirationDate = new Date();
    expirationDate.setDate(expirationDate.getDate() + daysToExpire);

    document.cookie = `${name}=${value}; expires=${expirationDate.toUTCString()}; path=/; SameSite=Strict`
}

// Function to get a cookie by name
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

