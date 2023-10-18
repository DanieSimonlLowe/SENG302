package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Filter;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * TableController for common table methods
 */
@Controller
public class TableController {
    /** Page offset constant */
    private static final int PAGE_OFFSET = 1;

    /**
     * Empty Constructor for TableController class
     */
    public TableController() {
    }

    /**
     * Initializes a filter object with the given parameters
     * @param search to filter for
     * @param selectedSports to filter for
     * @param selectedCities to filter for
     * @return filter object
     */
    public Filter initializeOptionalParameters(String search,
                                               List<Long> selectedSports,
                                               List<String> selectedCities) {
        if (selectedSports == null) { selectedSports = new ArrayList<>(); }
        if (selectedCities == null) { selectedCities = new ArrayList<>(); }

        return new Filter(search, selectedSports, selectedCities);
    }

    /**
     * Converts a given list to a page
     * @param page current page
     * @param page_size number of elements on a page
     * @param list list to convert
     * @return the page
     * @param <T> generic type T
     */
    public <T> Page<T> convertToPage(int page, int page_size, List<T> list) {
        Pageable pageable = PageRequest.of(page-PAGE_OFFSET, page_size);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    /**
     * Generates an empty table message based on whether a search query is passed
     * @param entryType type of the table entries
     * @param search search query
     * @return empty table message
     */
    public String emptyEntriesMessage(String entryType, String search) {
        if (search == null) { return "No " + entryType + " registered to display."; }
        else { return "No " + entryType + " match that query."; }
    }

    /**
     * Adds the given table attributes to the given model
     * @param model model to add attributes to
     * @param entries the table entries
     * @param page the current page
     * @param allSports all the sports available to filter from
     * @param allCities all the cities available to filter from
     * @param filter the filter object
     * @param <T> generic type T
     */
    public <T> void setModelAttributes(Model model,
                                        Page<T> entries,
                                        String pageName,
                                        int page,
                                        List<Sport> allSports,
                                        List<String> allCities,
                                        Filter filter) {

        // Convert list of selected sport ids from long to string
        List<String> selectedSportsString = new ArrayList<>();
        for (Long id : filter.getSports()) {
            selectedSportsString.add(id.toString());
        }

        // Add table entries and the current page
        model.addAttribute("entries", entries);
        model.addAttribute("page", page);

        model.addAttribute("pageName", pageName);

        // Add dropdown filter data to the model
        model.addAttribute("allSports", allSports);
        model.addAttribute("allCities", allCities);

        // Add optional parameters to the model
        model.addAttribute("selectedSportsString", selectedSportsString);
        model.addAttribute("selectedSports", filter.getSports());
        model.addAttribute("selectedCities", filter.getCities());
        model.addAttribute("search", filter.getSearch());
    }

    /**
     * Adds the given table attributes to the given model
     * @param model model to add attributes to
     * @param entries the table entries
     * @param page the current page
     * @param <T> generic type T
     */
    public <T> void setModelAttributes(Model model,
                                       Page<T> entries,
                                       String pageName,
                                       int page) {


        // Add table entries and the current page
        model.addAttribute("entries", entries);
        model.addAttribute("page", page);

        model.addAttribute("pageName", pageName);
    }
}
