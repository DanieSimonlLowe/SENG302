let nextButton = document.getElementById("nextButton")
let prevButton = document.getElementById("prevButton")

let overviewTabButton = document.getElementById("overviewTabButton")
let locationTabButton = document.getElementById("locationTabButton")

const all_tabs = document.getElementsByClassName('nav-link')

function setNavigationButtons() {
    let active_tab = document.getElementsByClassName('nav-link active')[0]
    switch (active_tab) {
        case all_tabs[0]:
            prevButton.disabled = true
            nextButton.textContent = "Next"
            locationTabButton.classList.remove("highlight")
            break;
        case all_tabs[1]:
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

const tabs = document.querySelectorAll('button[data-bs-toggle="pill"]')
tabs.forEach(tab => {
    tab.addEventListener('hide.bs.tab', function (event) {
        let active_tab = document.getElementsByClassName('nav-link active')[0]
        if (!validate(active_tab)) {
            event.preventDefault()
        }
    })
})

nextButton.addEventListener('click', function(){
    let active_tab = document.getElementsByClassName('nav-link active')[0]
    if (validate(active_tab)) {
        switch (active_tab) {
            case all_tabs[0]:
                // Validate overview fields
                locationTabButton.click()
                nextButton.textContent = "Submit";
                break;
            case all_tabs[1]:
                // Submit Form
                document.forms["clubForm"].submit();
                break;
            default:
                throw new Error('Active tab is not defined');

        }
        setNavigationButtons()
    } else {
        console.log("invalid");
    }
})

prevButton.addEventListener('click', function(){
    let active_tab = document.getElementsByClassName('nav-link active')[0]
    switch (active_tab) {
        case all_tabs[0]:
            // No previous button
            break;
        case all_tabs[1]:
            locationTabButton.click()
            nextButton.textContent = "Next";
            break;
        default:
            throw new Error('Active tab is not defined');
    }
    setNavigationButtons()
})


const clubName = document.getElementById('clubName')
const team = document.querySelectorAll('input[type="checkbox"]')
const address1 = document.getElementById('form-address1')
const address2 = document.getElementById('form-address2')
const postcode = document.getElementById('form-postcode')
const suburb = document.getElementById('form-suburb')
const city = document.getElementById('form-city')
const country = document.getElementById('form-country')


function validate(active_tab) {
    let valid = true
    switch (active_tab) {
        case all_tabs[0]:
            const clubNameFormat = /^(?!.*[\d!])(?=.*\p{L})[ \p{L}\s'-]+$/u;

            if (clubName.value === null || /^\s*$/.test(clubName.value)) {
                clubName.classList.add('is-invalid')
                document.getElementById('clubNameError').innerText = 'Club name is required.'
                valid = false
            } else if (!clubName.value.match(clubNameFormat)) {
                clubName.classList.add('is-invalid')
                document.getElementById('clubNameError').innerText = 'Club name must only include letters, hyphens and apostrophes.'
                valid = false
            } else {
                clubName.classList.remove('is-invalid')
            }

            if (!Array.from(team).some(checkbox => checkbox.checked) ) {
                document.getElementById('checkboxeslist').classList.add('is-invalid')
                document.getElementById('teamClubError').innerText = 'A team is required.'
                valid = false
            } else {
                document.getElementById('checkboxeslist').classList.remove('is-invalid')
            }

            break
        case all_tabs[1]:
            const addressFormat = /^(?!.*!)(?![\d\s'\/,.\\-]*$)[\p{L}\d\s'\/,.\\-]+$/u;
            const postcodeFormat = /^(?:[\p{Letter}\p{Number}]+-[\p{Letter}\p{Number}]+)+|[\p{Letter}\p{Number}]+$/u;
            const suburbFormat = /^(?!.*!)(?=.*\p{Letter})[\p{Letter}\d\s'\/,.\\-]+$/u;
            const cityFormat = /^(?=.*\p{Letter})[\p{Letter}\d\s'-\/,.]+$/u;
            const countryFormat = /^(?!.*[\d!])(?=.*\p{L})[ \p{L}\s',.-]+$/u;

            if (!address1.value.match(addressFormat)) {
                address1.classList.add('is-invalid')
                document.getElementById('address1Error').innerText = 'Address line 1 can contain letters, numbers, hyphens, apostrophes, forward slash, fullstops and commas.'
                valid = false
            } else {
                address1.classList.remove('is-invalid')
            }

            if (!address2.value.match(addressFormat) && !(address2.value.trim() === "")) {
                address2.classList.add('is-invalid')
                document.getElementById('address2Error').innerText = 'Address line 2 can contain letters, numbers, hyphens, apostrophes, fullstops and commas.'
                valid = false
            } else {
                address2.classList.remove('is-invalid')
            }

            if (!postcode.value.match(postcodeFormat)) {
                postcode.classList.add('is-invalid')
                document.getElementById('postcodeError').innerText = 'Postcode can contain letters, numbers and hyphens.'
                valid = false
            } else {
                postcode.classList.remove('is-invalid')
            }

            if (!suburb.value.match(suburbFormat) && !(suburb.value === "") && !(suburb.value === null)) {
                suburb.classList.add('is-invalid')
                document.getElementById('suburbError').innerText = 'Suburb can contain letters, numbers, hyphens, apostrophes, forward slash, fullstops and commas.'
                valid = false
            } else {
                suburb.classList.remove('is-invalid')
            }

            if (!city.value.match(cityFormat)) {
                city.classList.add('is-invalid')
                document.getElementById('cityError').innerText = 'City name can contain letters, numbers, hyphens, apostrophes, forward slash, fullstops and commas.'
                valid = false
            } else {
                city.classList.remove('is-invalid')
            }

            if (!country.value.match(countryFormat)) {
                country.classList.add('is-invalid')
                document.getElementById('countryError').innerText = 'Country name can contain letters, numbers, hyphens, apostrophes, fullstops and commas.'
                valid = false
            } else {
                country.classList.remove('is-invalid')
            }
            break

    }
    return valid
}

