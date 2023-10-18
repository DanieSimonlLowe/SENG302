let collapsible = document.getElementsByClassName("collapsible");

for(let i=0; i<collapsible.length; ++i) {
    collapsible[i].addEventListener("click", function() {
        this.classList.toggle("active");
        const content = document.getElementById("filter-content");
        if (content.style.display === "block") {
            content.style.display = "none";
        } else {
            content.style.display = "block";
        }

        if(content.style.maxHeight) {
            content.style.maxHeight = null;
        } else {
            content.style.maxHeight = content.scrollHeight + "px";
        }
    });
}