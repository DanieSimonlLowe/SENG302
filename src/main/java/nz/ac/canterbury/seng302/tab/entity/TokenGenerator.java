package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Custom RegistrationToken class. Used to store the user registration details and the token that is sent to the user.
 *
 */
@Entity
@Table(name = "tab_registration_token")
public class TokenGenerator {

    /** The password encoder */
    protected static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);

    /** The random generator */
    private static final SecureRandom random = new SecureRandom();

    /** Integer constant for the maximum token length */
    private static final int MAX_TOKEN_LENGTH = 10;

    /** String constant for the allowed characters in the token */
    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /** Integer constant for the lifespan of the token */
    private static final int LIFESPAN_MILLI_SECONDS_REG = 7200000;

    private static final int LIFESPAN_MILLI_SECONDS_PAS = 3600000;

    /**
     * gets the life span of a token in milliseconds.
     * */
    public int getLifeSpan() {
        if (tokenType.equals("register")) {
            return LIFESPAN_MILLI_SECONDS_REG;
        } else {
            return LIFESPAN_MILLI_SECONDS_PAS;
        }
    }

    /** The token id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    /** The token value */
    @Column(name = "token")
    private String token;

    /** The timestamp the token was created */
    @Column(name = "time_stamp")
    private long timeStamp;

    /** The email the token is for */
    @Column(nullable = false)
    private String email;

    /** The first name of the user the token is for */
    @Column(nullable = false)
    private String firstName;

    /** The last name of the user the token is for */
    @Column(nullable = false)
    private String lastName;

    /** The date of birthday of the user the token is for */
    @Column(nullable = false)
    private int dateOfBirthDay;

    /** The date of birth month of the user the token is for */
    @Column(nullable = false)
    private int dateOfBirthMonth;

    /** The date of birth year of the user the token is for */
    @Column(nullable = false)
    private int dateOfBirthYear;

    /** The password hash of the user the token is for */
    @Column(nullable = false)
    private String passwordHash;

    /** The password hash of the user the token is for */
    @Column(nullable = false)
    private String tokenType;

    /** The location of the user the token is for */
    @OneToOne
    @JoinColumn(name="location_id")
    private Location location;

    /** 
     * Empty constructor for the RegistrationToken class.
     */
    protected TokenGenerator() {}

    /**
     * Non-empty constructor for the RegistrationToken class
     * @param email     the email of the user the token is for
     * @param firstName the first name of the user the token is for
     * @param lastName  the last name of the user the token is for
     * @param date      the birthdate of the user the token is for
     * @param location  the location of the user the token is for
     * @param password  the password hash of the user the token is for
     * @param type      the type of token - "register" or "resetPassword" depending on what the token is used for
     */
    public TokenGenerator(String email, String firstName, String lastName, LocalDate date, Location location, String password, String type) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        //setting the password hash
        this.passwordHash = passwordEncoder.encode(password);

        this.timeStamp = Instant.now().toEpochMilli();

        this.tokenType = type;

        byte[] array = new byte[MAX_TOKEN_LENGTH];
        random.nextBytes(array);
        //timestamp and email mean it must be unique.
        this.token = generateToken() + this.timeStamp + this.email;
        setDate(date);
    }

    /**
     * Generates the token that is emailed to the user
     * @return the token, as a string
     */
    private static String generateToken()
    {
        char[] text = new char[MAX_TOKEN_LENGTH];
        for (int i = 0; i < MAX_TOKEN_LENGTH; i++)
        {
            text[i] = ALLOWED_CHARS.charAt(random.nextInt(ALLOWED_CHARS.length()));
        }
        return new String(text);
    }

    /**
     * Getter for the token 
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Getter of the timestamp the token was generated
     * @return the timestamp
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * Getter of the user's email
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Getter for the user's first name
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Getter for the user's last name
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Getter for the user's day of birth
     * @return the day of birth
     */
    public int getDateOfBirthDay() {
        return dateOfBirthDay;
    }

    /**
     * Getter for the user's month of birth
     * @return the month of birth
     */
    public int getDateOfBirthMonth() {
        return dateOfBirthMonth;
    }

    /**
     * Getter for the user's year of birth
     * @return the year of birth
     */
    public int getDateOfBirthYear() {
        return dateOfBirthYear;
    }

    /**
     * Getter for the user's location
     * @return the user's location
     */
    public Location getLocation() { return this.location; }

    /**
     * Setter for the user's birthdate
     * @param date the user's birthdate
     */
    public void setDate(LocalDate date) {
        this.dateOfBirthDay = date.getDayOfMonth();
        this.dateOfBirthMonth = date.getMonthValue();
        this.dateOfBirthYear = date.getYear();
    }

    /**
     * Getter for the local date
     * @return the current date
     */
    public LocalDate getDate() {
        LocalDate date = LocalDate.now();
        date = date.withYear(dateOfBirthYear).withMonth(dateOfBirthMonth).withDayOfMonth(dateOfBirthDay);
        return date;
    }

    /**
     * Generates a new user
     * @return the new user, of type User
     */
    public User generateUser() {
        return new User(email, firstName, lastName, getDate(), location, passwordHash);
    }

    /**
     * Getter for the id of the token
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Getter for the token's type
     * @return the token's type
     */
    public String getTokenType() {
        return tokenType;
    }


    /**
     * Checks if a token has expired
     * @return boolean whether the token has expired or not
     */
    public boolean isToOld() {
        long currentTime = Instant.now().toEpochMilli();
        if (tokenType.equals("resetPassword")) {
            return currentTime - getTimeStamp() > LIFESPAN_MILLI_SECONDS_PAS;
        } else {
            return currentTime - getTimeStamp() > LIFESPAN_MILLI_SECONDS_REG;
        }

    }

    /**
     * sets the timestamp to only be used for tests.
     * */
    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * Setter for the token's type
     */
    public void setTokenType(String type) {
        this.tokenType = type;
    }


}
