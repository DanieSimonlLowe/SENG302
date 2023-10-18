package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

/**
 * Custom Location class.
 */
@Entity
@Table(name = "Location")
public class Location {

    /** The id of the location */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The location's address line 1 */
    @Column
    private String address1;

    /** The location's address line 2 */
    @Column
    private String address2;

    /** The location's suburb */
    @Column
    private String suburb;

    /** The location's city */
    @Column(nullable = false)
    private String city;

    /** The location's postcode */
    @Column
    private String postcode;

    /** The location's country */
    @Column(nullable = false)
    private String country;

    /**
     * Empty constructor for the Location class
     */
    public Location() {}

    /**
     * Non-empty constructor for the Location class
     * @param address1    street address 1 of the location
     * @param address2    street address 2 of the location
     * @param suburb      the suburb of the location
     * @param postcode    the postcode
     * @param city        the city
     * @param country     the country
     */
    public Location(String address1, String address2, String suburb, String postcode, String city, String country) {
        this.address1 = address1;
        this.address2 = address2;
        this.suburb = suburb;
        this.postcode = postcode;
        this.city = city;
        this.country = country;
    }

    public void setAll(String address1, String address2, String suburb, String postcode, String city, String country) {
        this.address1 = address1;
        this.address2 = address2;
        this.suburb = suburb;
        this.postcode = postcode;
        this.city = city;
        this.country = country;
    }

    /**
     * Builds the separate location parts back into the original location string.
     * This is used to display the location to the user.
     * @return The original location string.
     */
    public String toString(){
        String[] locationParts = {this.address1, this.address2, this.suburb, this.city, this.postcode};
        StringBuilder location = new StringBuilder();
        for (String part : locationParts) {
            if (!part.isBlank()) {
                location.append(part).append(", ");
            }
        }
        location.append(this.country);
        return location.toString();
    }

    /**
     * Builds the separate location parts minus the suburb into one string
     * This is because suburb is in a separate field on the page
     * @return The completed location string
     */
    public String toEditString(){
        String[] locationParts = {this.address1, this.address2, this.city, this.postcode};
        StringBuilder location = new StringBuilder();
        for (String part : locationParts) {
            if (!part.isBlank()) {
                location.append(part).append(", ");
            }
        }
        location.append(this.country);
        return location.toString();
    }

    /**
     * Getter for the id in the database
     * @return the id in the database
     */
    public Long getId() { return id; }

    /**
     * Getter for the street address
     * @return the first address line
     */
    public String getAddressOne() {
        return address1;
    }

    /**
     * Getter for address line 2
     * @return the second address line
     */
    public String getAddressTwo() {
        return address2;
    }

    /**
     * Getter for the suburb
     * @return the suburb
     */
    public String getSuburb() {
        return suburb;
    }

    /**
     * Getter for the city
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Getter for the postcode
     * @return the postcode
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * Getter for the country code
     * @return the country code
     */
    public String getCountry() {
        return country;
    }

    /**
     * Setter for the street address
     * @param address1 the address line
     */
    public void setAddressOne(String address1) {
        this.address1 = address1;
    }

    /**
     * Setter for the street address
     * @param address2 the address line
     */
    public void setAddressTwo(String address2) {
        this.address2 = address2;
    }

    /**
     * Setter for the suburb
     * @param suburb the suburb
     */
    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    /**
     * Setter for the city
     * @param city the city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Setter for the postcode
     * @param postcode the postcode
     */
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    /**
     * Setter for the country code
     * @param country the country
     */
    public void setCountryCode(String country) {
        this.country = country;
    }

    /**
     * checks if two locations are in the same city
     * @param location the other location being checked
     * @return if the two locations are in the same city
     * */
    public boolean isSameCity(Location location) {
        return location.getCity().equals(city) && location.getCountry().equals(country);
    }
}
