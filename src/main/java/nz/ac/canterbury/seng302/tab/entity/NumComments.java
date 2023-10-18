package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "num_comments")
public class NumComments extends LeaderboardEntity{

    protected NumComments() {}

    public NumComments(Club club, User user) {
        super(club, user);
    }

}
