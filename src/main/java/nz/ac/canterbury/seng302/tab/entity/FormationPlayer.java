package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Formation_Player")
public class FormationPlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    protected User user;

    @ManyToOne(optional=false)
    protected Formation formation;

    protected FormationPlayer() {}

    /*
    protected FormationPlayer(User user, Formation formation) {
        this.user = user;
        this.formation = formation;
    }

     */

}
