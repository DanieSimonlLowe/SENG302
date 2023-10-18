package nz.ac.canterbury.seng302.tab.entity;


import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.stats.Substituted;
import nz.ac.canterbury.seng302.tab.exceptions.InvalidSubstitutionException;
import nz.ac.canterbury.seng302.tab.service.LineupPlayersPositionComparator;
import nz.ac.canterbury.seng302.tab.service.SubstituteMinuteComparator;

import java.util.*;

@Entity
@Table(name="tab_lineup")
public class LineUp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Formation formation;

    @ManyToOne
    @JoinColumn(name="activity_id")
    private Activity activity;

    @OneToMany(mappedBy = "lineUp", orphanRemoval = true)
    private List<LineupPlayer> players;

    /**
     * sets the players to the imputed players. ONLY USE FOR TESTS.
     * @param players the new list of LineupPlayers for the lineup.
     * */
    protected void setPlayer(List<LineupPlayer> players) {
        this.players = players;
    }

    protected LineUp() {}

    public LineUp(Formation formation, Activity activity) {
        this.formation = formation;
        this.activity = activity;
    }

    public Long getId() {
        return id;
    }

    public Formation getFormation() {
        return formation;
    }

    public Activity getActivity() {
        return activity;
    }



    /**
     * applies the substitution to the lineup to produce a valid lineup which would occurs after the Substitution has taken place.
     * @param substituted the substitution to apply
     * @throws InvalidSubstitutionException is thrown when the Substitution can't be applied to the lineup.
     * */
    public void applySubstitution(Substituted substituted) throws InvalidSubstitutionException {
        Optional<LineupPlayer> oldPlayer = players.stream().filter(p -> Objects.equals(p.getUser().getId(), substituted.getOldPlayer().getId())).findFirst();
        Optional<LineupPlayer> newPlayer = players.stream().filter(p -> Objects.equals(p.getUser().getId(), substituted.getNewPlayer().getId())).findFirst();
        if (oldPlayer.isEmpty() || newPlayer.isEmpty()) {
            throw new InvalidSubstitutionException();
        }
        int oldPos = oldPlayer.get().getPosition();
        oldPlayer.get().setPosition(newPlayer.get().getPosition());
        newPlayer.get().setPosition(oldPos);
    }

    /**
     * applies the list of substitutions to the lineup to produce a valid lineup which would occurs after the Substitution has taken place.
     * @param substitutions the substitutions to apply
     * @throws InvalidSubstitutionException is thrown when a substitution can't be applied to the lineup.
     * */
    public void applySubstitutions(List<Substituted> substitutions) throws InvalidSubstitutionException {
        for (Substituted substituted :  substitutions.stream().sorted(new SubstituteMinuteComparator()).toList()) {
            applySubstitution(substituted);
        }
    }


    /**
     * gets all the users that are linked to the Lineup in the order of there position.
     * @return a list of users.
     * */
    public List<User> getPlayersInOrder() {
        return players.stream().sorted(new LineupPlayersPositionComparator()).map(p -> p.getUser()).toList();
    }

    /**
     * checks if a substitution old and new players are contained in the lineups list of players.
     * @param substituted the substitution to be checked
     * @return if the substitution is valid or not
     * */
    public boolean isValidSubstitution(Substituted substituted) {
        return players.stream().anyMatch(p -> Objects.equals(p.getUser().getId(), substituted.getOldPlayer().getId())) && players.stream().anyMatch(p -> Objects.equals(p.getUser().getId(), substituted.getNewPlayer().getId()));
    }
}
