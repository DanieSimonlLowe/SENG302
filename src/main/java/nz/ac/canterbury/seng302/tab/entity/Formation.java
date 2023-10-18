package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.exceptions.MismatchSumPlayersSize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * is used for the storage and manipulations of formations that a group of users can place them self's in.
 * */
@Entity
@Table(name = "Formation")
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    private List<Short> playersPerSection;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(nullable = false)
    private String sportPitch;

    public Long getId() {
        return id;
    }

    public List<Short> getPlayersPerSection() {
        return playersPerSection;
    }

    /**
     * for the use of the persistence API do not use.
     * */
    protected Formation() {}

    /**
     * @param input a string of the format number or number-number (any amount of -number can be added to the value).
     *              these numbers are shorts so can not be more than 32767.
     *              this string defines the amount of players per each vertical slice of the game surface.
     *              can't be null.
     * @throws NumberFormatException this is thrown when an input can't be parsed into a list of shorts.
     * @throws NullPointerException this is thrown when ever the input or the players are null.
     * */
    public Formation(String input, String sportPitch, Team team) throws NumberFormatException, MismatchSumPlayersSize, NullPointerException {
        if (input == null) {
            throw new NullPointerException();
        }
        if (input.length() == 0) {
            throw new NumberFormatException();
        }
        if (input.charAt(input.length()-1) == '-') {
            throw new NumberFormatException();
        }

        String[] sections = input.split("-");
        playersPerSection = new ArrayList<>();
        for (String section: sections) {
            short value = Short.parseShort(section);
            if (value <= 0) {
                throw new NumberFormatException();
            }
            playersPerSection.add(value);
        }

        List<String> sports = Arrays.asList("baseball", "basketball", "football", "hockey", "netball", "rugby", "softball", "volleyball");

        if (sports.contains(sportPitch.substring(0,sportPitch.lastIndexOf("_")))) {
            this.sportPitch = sportPitch;
        } else {
            this.sportPitch = "unknown";
        }

        this.team = team;
    }

    /**
     * Gets the formation and transforms it into the correct format
     * @return a string representation of the formation
     */
    public String getPlayersPerSectionString() {
        String playerLayout = playersPerSection.get(0).toString();
        for (int i = 1; i < playersPerSection.size(); i++) {
            playerLayout += "-";
            playerLayout += playersPerSection.get(i);
        }
        return playerLayout;
    }

    /**
     * Gets the name of the background image for the formation
     * @return a string of the image name
     */
    public String getPitchString() {
        return sportPitch;
    }

    /**
     * Gets the sport that the formation is associated with
     * @return the sport
     */
    public String getSportName() {
        String sport = sportPitch.substring(0,sportPitch.lastIndexOf("_"));
        sport = sport.substring(0, 1).toUpperCase() + sport.substring(1);
        return sport;
    }

}
