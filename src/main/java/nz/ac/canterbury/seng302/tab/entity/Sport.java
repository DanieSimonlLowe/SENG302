package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * Custom Sport class.
 */
@Entity
@Table(name = "tab_sports")
public class Sport {

    /** The sport's id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sport_id")
    private Long id;

    /** The name of the sport */
    @Column(nullable = false, unique = true)
    private String sportName;

    /** The users that play this sport */
    @ManyToMany(mappedBy = "favSports")
    List<User> users;


    /**
     * JPA required no-args constructor
     */
    protected Sport() {}

    /**
     * Constructor for the Sport class
     * @param sportName  the name of the sport, which is made up of letters, spaces, apostrophes and dashes. 
     *                   First letter is capitalized by default.
     */
    public Sport(String sportName) {
        this.sportName = sportName.substring(0, 1).toUpperCase() + sportName.substring(1);
    }

    /**
     * Getter for the sport's id
     * @return the sport's id
     */
    public Long getId() {
        return id;
    }

    /**
     * Getter for the sport's name. 
     * Made up of letters, spaces, apostrophes and dashes.
     * First letter is capitalized by default.
     * @return the sport's name
     */
    public String getSportName() {
        return sportName;
    }
}
