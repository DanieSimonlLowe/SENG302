/** Used to store location from current request so if user clicks location can autofill information. */
let locations = [];

window.onload = function() {

    //Append the new path to the base URL
    let searchValue = "";

    const locationForm = document.querySelector('#form-address1');
    locationForm.addEventListener("input", function (e) {
        searchValue = this.value;
        if(searchValue.length < 2) return;
        fetch(`${getCorrectBaseUrl()}/api/location-request?searchValue=${searchValue}`, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'Accept': 'application/json',
                'Content-Type': 'application/json; charset=utf-8'
            }
        })  .then((response) => response.json())
            .then((response) => {
                const locationData = response;
                const locationList = document.getElementById("location-list");
                locations = locationData;

                // Clear existing locations
                while (locationList.firstChild) {
                    locationList.removeChild(locationList.firstChild);
                }

                // Add the new locations
                locationData.forEach(location => {
                    const option = document.createElement("option");
                    option.value = location.displayString;
                    option.textContent = location.displayString;
                    locationList.appendChild(option);
                })
            })
    });
}

function onInput() {
    const selectedAddress = document.getElementById("form-address1").value;
    const locationObj = locations.filter((location) => location.displayString === selectedAddress)[0];

    if (!locationObj) return;
    const locationProperties = locationObj.place.properties;

    // Address Line 1
    if ('street' in locationProperties) {
        document.getElementById('form-address1').value = locationProperties.street;
    } else {
        document.getElementById('form-address1').value = "";
    }

    // Address Line 2
    if ('neighborhood' in locationProperties) {
        document.getElementById('form-address2').value = locationProperties.neighborhood;
    } else {
        document.getElementById('form-address2').value = "";
    }

    // Suburb
    if('state' in locationProperties) {
        document.getElementById('form-suburb').value = locationProperties.state;
    } else {
        document.getElementById('form-suburb').value = "";
    }

    // Postcode
    if ('postalCode' in locationProperties) {
        document.getElementById('form-postcode').value = locationProperties.postalCode;
    } else {
        document.getElementById('form-postcode').value = "";
    }

    // City
    // If city not in Object.keys(locationProperties) then use county instead
    if ('city' in locationProperties) {
        document.getElementById('form-city').value = locationProperties.city;
    } else if ('county' in locationProperties) {
        document.getElementById('form-city').value = locationProperties.county;
    } else {
        document.getElementById('form-city').value = "";
    }

    // Country
    if ('country' in locationProperties) {
        document.getElementById('form-country').value = locationProperties.country;
    } else {
        document.getElementById('form-country').value = "";
    }
}