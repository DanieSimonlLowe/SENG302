package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

/**
 * Custom Authority class. This is used to hold the String representations of the user's authorities.
 */
@Entity
@Table(name = "tab_authority")
public class Authority {
    /** The id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_id")
    private Long id;

    /** The user */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    /** The role */
    @Column()
    private String role;

    /**
     * JPA required no-args constructor
     */
    protected Authority() {}

    /**
     * Constructor for the Authority class
     */
    public Authority(String role) {
        this.role = role;
    }

    /**
     * Gets the role
     *
     * @return the role
     */
    public String getRole(){
        return role;
    }
}
