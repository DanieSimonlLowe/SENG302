package nz.ac.canterbury.seng302.tab.entity;

import java.util.List;

/**
 * Custom Filter class. This is used to filter users and teams.
 */
public class Filter {

    /** The search string */
    private String search;

    /** The list of sport ids to filter by */
    private List<Long> sports;

    /** the list of cities to filter by */
    private List<String> cities;

    /**
     * Constructor for the Filter class
     * @param search the search string
     * @param sports the list of sports
     * @param cities the list of cities
     */
    public Filter(String search, List<Long> sports, List<String> cities) {
        this.search = search;
        this.sports = sports;
        this.cities = cities;
    }

    /**
     * Getter for the search string
     * @return the search string
     */
    public String getSearch() {
        return search;
    }

    /**
     * Setter for the search string
     * @param search the search string
     */
    public void setSearch(String search) {
        this.search = search;
    }

    /**
     * Getter for the cities to filter by
     * @return the cities to filter by
     */
    public List<String> getCities() {
        return cities;
    }

    /**
     * Setter for the cities to filter by
     * @param cities the cities to filter by
     */
    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    /**
     * Getter for the list of sports to filter by
     * @return the sports to filter by
     */
    public List<Long> getSports() {
        return sports;
    }

    /**
     * Setter for the list of sports to filter by
     * @param sports the sports to filter by
     */
    public void setSports(List<Long> sports) {
        this.sports = sports;
    }

}
