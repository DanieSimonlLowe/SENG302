function createFlagToast(message) {
    // Create a new toast element
    let toast = document.createElement('div');
    toast.className = 'toast bg-warning';
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');

    // Create the toast header
    let toastHeader = document.createElement('div');
    toastHeader.className = 'toast-header';
    let strong = document.createElement('strong');
    strong.className = 'me-auto';
    strong.textContent = "Flagged Comment";
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
    toastBody.textContent = message;

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
    }, 20000);
}

