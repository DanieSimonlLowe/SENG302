document.getElementById("errorDiv").hidden = document.getElementById("error").textContent.length < 1;
document.getElementById("fileSizeErrorDiv").hidden = document.getElementById("fileSizeError").textContent.length < 1;
console.log(document.getElementById("fileSizeError").textContent.length);
const maxFileSize = 10500000; //By default, this is set to 10mb (10500000)

function profilePicTrigger() {
    //Allows clicking of the hidden file browser through clicking the profile pic
    let picker = document.getElementById("file");
    picker.value = null; //Ensures onchange event is fired each time
    picker.click();
}

function isInvalidType(file) {
    //This function checks to make sure it's a valid file type
    const validExtensions = ["jpg", "jpeg", "png", "svg"]
    const fileName = file.name;
    const ext = fileName.split('.').pop();
    return !validExtensions.includes(ext.toLowerCase());
}

function changeFile() {
    const fileList = document.getElementById("file").files;
    let isValid = true;
    const file = fileList[0];
    if (isInvalidType(file)) {
        //Shows type error on bad type
        isValid = false;
        document.getElementById("error").textContent = "Only JPG, PNG and SVG are allowed"
        document.getElementById("errorDiv").hidden = false;
    } else {
        document.getElementById("errorDiv").hidden = true;
    }
    if (file.size > maxFileSize) {
        //Shows size error on too large a file
        isValid = false;
        document.getElementById("fileSizeError").textContent = "Exceeded max file size of 10MB"
        document.getElementById("fileSizeErrorDiv").hidden = false;
    } else {
        document.getElementById("fileSizeErrorDiv").hidden = true;
    }

    const formName = "clubForm"; // checking what form it is so that it doesn't submit the form

    if (isValid) {
        // Upload the image and submit the form
        if (document.getElementById("form").name !== formName) {
            document.getElementById("form").submit();
        } else {
            document.getElementById("profilePicture").src = URL.createObjectURL(file);
        }
    } else {
        setTimeout(function() {
            document.getElementById("errorDiv").hidden = true;
            document.getElementById("fileSizeErrorDiv").hidden = true;
        }, 4000);
    }
}


function isInvalidAttachmentType(file) {
    //This function checks to make sure it's a valid file type
    const validExtensions = ["jpg", "jpeg", "png", "svg", "mp4", "webm", "ogg"]
    const fileName = file.name;
    const ext = fileName.split('.').pop();
    return !validExtensions.includes(ext.toLowerCase());
}

function typeOfAttachment(file) {
    const imgExtensions = ["jpg", "jpeg", "png", "svg"]
    const videoExtensions = ["mp4", "webm", "ogg"]
    const fileName = file.name;
    const ext = (fileName.split('.').pop()).toLowerCase();

    if (imgExtensions.includes(ext)) {
        return "img"
    }
    if (videoExtensions.includes(ext)) {
        return "video"
    }
}

function addAttachmentFile() {
    const fileList = document.getElementById("postAttachment").files;
    let isValid = true;
    const file = fileList[0];
    if (isInvalidAttachmentType(file)) {
        //Shows type error on bad type
        isValid = false;
        document.getElementById("error").textContent = "Only JPG, PNG SVG, MP4, WEBM and OGG are allowed"
        document.getElementById("errorDiv").hidden = false;
    } else {
        document.getElementById("errorDiv").hidden = true;
    }
    if (file.size > maxFileSize) {
        //Shows size error on too large a file
        isValid = false;
        document.getElementById("fileSizeError").textContent = "Exceeded max file size of 10MB"
        document.getElementById("fileSizeErrorDiv").hidden = false;
    } else {
        document.getElementById("fileSizeErrorDiv").hidden = true;
    }

    const formName = "addPostForm"; // checking what form it is so that it doesn't submit the form

    if (isValid) {
        // Upload the image and submit the form
        if (document.getElementById("form").name !== formName) {
            document.getElementById("form").submit();
        } else {
            let imgAttachment = document.getElementById("postImgAttachmentSrc");
            let videoAttachment = document.getElementById("postVideoAttachmentSrc");

            if (typeOfAttachment(file) == "img") {
                imgAttachment.src = URL.createObjectURL(file);
//                videoAttachment.src = ""
                videoAttachment.pause();
                videoAttachment.removeAttribute('src');
                videoAttachment.load();
            }

            if (typeOfAttachment(file) == "video") {
//                imgAttachment.src = ""
                imgAttachment.removeAttribute('src', '');
                videoAttachment.src = URL.createObjectURL(file);
            }
//            imgAttachment.src = URL.createObjectURL(file);
//            document.getElementById("attachmentsContainer").appendChild(attachment);
        }
    } else {
        setTimeout(function() {
            document.getElementById("errorDiv").hidden = true;
            document.getElementById("fileSizeErrorDiv").hidden = true;
        }, 4000);
    }
}