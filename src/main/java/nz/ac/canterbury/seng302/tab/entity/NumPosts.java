package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "num_posts")
public class NumPosts extends LeaderboardEntity{

        protected NumPosts() {}

        public NumPosts(Club club, User user) {
            super(club, user);
        }
}
