Feature: U1 - (User) Register a new user into not that TAB

  Scenario: AC1 - Register button leads to registration form
    Given I connect to the system's main URL
    When I hit the button to register
    Then I see a registration form

  Scenario Outline: AC2 - Enter valid values and log in.
    Given I am on the registration page
    When I enter information <firstName> <lastName> <email> <city> <country> <dateOfBirth> <password> <confirmPassword>
    And I click the register button
    Then I am redirected to the login page with an email confirmation
    Examples:
      |firstName|lastName|email|city|country|dateOfBirth|password|confirmPassword|
      |  "Tom"  |  "Barthelmeh"  |  "email@email.com"  |  "Christchurch"  |  "New Zealand"  |  "2003-08-28"  |  "Password1!"  |  "Password1!"  |
      |  "Celia"  |  "Allen"     |  "testEmail2@email.com"  |  "Helsinki"  |  "Finland"     |  "1940-02-28"  |  "Oranges!@67"  |  "Oranges!@67"  |
      |  "Nathan"  |  "Harper"  |  "testEmail3@email.com"  |  "Mount Gambier"  |  "Australia"  |  "2010-04-12"  |  "NotMyPassword*1"  |  "NotMyPassword*1"  |


  Scenario Outline: AC3 - Enter invalid values
    Given I am on the registration page
    When I enter information <firstName> <lastName> <email> <city> <country> <dateOfBirth> <password> <confirmPassword> <address1> <postcode> <suburb>
    And I click the register button
    Then I stay on the registration page
    And The error field <errorField> displays the text <errorText>
    Examples:
      |firstName    |lastName        |email                |city              |country          |dateOfBirth     |password        |confirmPassword  |address1                 |postcode|suburb                |errorField         |errorText|
      |  "    "     |  "Barthelmeh"  |  "email@email.com"  |  "Christchurch"  |  "New Zealand"  |  "2003-08-28"  |  "Password1!"  |  "Password1!"   |  "12 Drury Lane"        | "2832" | "Riccarton"          | "invalidFName"    | "First name is not valid" |
      |  "@#$%^&*"  |  "Barthelmeh"  |  "email@email.com"  |  "Christchurch"  |  "New Zealand"  |  "2003-08-28"  |  "Password1!"  |  "Password1!"   |  "12 Drury Lane"        | "2832" | "Riccarton"          | "invalidFName"    | "First name is not valid" |
      |  "Tom"      |  "    "        |  "email@email.com"  |  "Christchurch"  |  "New Zealand"  |  "2003-08-28"  |  "Password1!"  |  "Password1!"   |  "12 Drury Lane"        | "2832" | "Riccarton"          |"invalidLName"     | "Last name is not valid"  |
      |  "Tom"      |  "2345@#$%^&"  |  "email@email.com"  |  "Christchurch"  |  "New Zealand"  |  "2003-08-28"  |  "Password1!"  |  "Password1!"   |  "12 Drury Lane"        | "2832" | "Riccarton"          |"invalidLName"     | "Last name is not valid"  |
      |  "Tom"      |  "Barthelmeh"  |  "emailemail.com"   |  "Christchurch"  |  "New Zealand"  |  "2003-08-28"  |  "Password1!"  |  "Password1!"   |  "12 Drury Lane"        | "2832" | "Riccarton"          | "invalidEmail"    | ""  |
      |  "Tom"      |  "Barthelmeh"  |  "email@@emailcom"  |  "Christchurch"  |  "New Zealand"  |  "2003-08-28"  |  "Password1!"  |  "Password1!"   |  "12 Drury Lane"        | "2832" | "Riccarton"          |"invalidEmail"     | ""  |
      |  "Tom"      |  "Barthelmeh"  |  "email@email.com"  |  "Christchurch"  |  "New Zealand"  |  "2013-08-28"  |  "Password1!"  |  "Password1!"   |  "12 Drury Lane"        | "2832" | "Riccarton"          | "invalidDOB"      | "Enter a valid DOB. Must be 13 years or older"  |
      |  "Tom"      |  "Barthelmeh"  |  "email@email.com"  |  "Christchurch"  |  "New Zealand"  |  "2020-08-28"  |  "Password1!"  |  "Password1!"   |  "12 Drury Lane"        | "2832" | "Riccarton"          | "invalidDOB"      | "Enter a valid DOB. Must be 13 years or older"  |
      |  "Tom"      |  "Barthelmeh"  |  "email@email.com"  |  "Christchurch"  |  "New Zealand"  |  "2003-08-28"  |  "Password"    |  "Password"     |  "12 Drury Lane"        | "2832" | "Riccarton"          |"invalidPassword"  | "Password must be at least 8 characters and contain a number and a special character" |
      |  "Tom"      |  "Barthelmeh"  |  "email@email.com"  |  "Christchurch"  |  "New Zealand"  |  "2003-08-28"  |  "Password4"   |  "Password4"    |  "12 Drury Lane"        | "2832" | "Riccarton"          |"invalidPassword"  | "Password must be at least 8 characters and contain a number and a special character" |
      |  "Tom"      |  "Barthelmeh"  |  "email@email.com"  |  "Christchurch"  |  "New Zealand"  |  "2003-08-28"  |  "Password4"   |  "Passworddd4"  |  "12 Drury Lane"        | "2832" | "Riccarton"          |"invalidPassword"  | "Passwords do not match"                                                              |
      |  "Tom"      |  "Barthelmeh"  |  "email@email.com"  |  "Christchurch"  |  "New Zealand"  |  "2003-08-28"  |  "Password1!"  |  "Password1!"   |  "12 Drury *&_@#)(_$@&" | "2832" | "Riccarton"          | "invalidLocation" | "Please enter an address containing at least one digit alongside any letters. Hyphens and apostrophes are also acceptable."|
      |  "Tom"      |  "Barthelmeh"  |  "email@email.com"  |  "Christchurch"  |  "New Zealand"  |  "2003-08-28"  |  "Password1!"  |  "Password1!"   |  "12 Drury Lane"        | "28!!" | "Riccarton"          | "invalidPostcode" | "Please enter a postcode which only contains numbers, letters or hyphens."|
      |  "Tom"      |  "Barthelmeh"  |  "email@email.com"  |  "Christchurch"  |  "New Zealand"  |  "2003-08-28"  |  "Password1!"  |  "Password1!"   |  "12 Drury Lane"        | "2888" | "XX-!KoolKat@420-XX" | "invalidSuburb" | "Please enter a suburb which contains only letters."  |

  Scenario: AC9 -  Given I am on the registration form, when I hit the cancel button, I come back to the home page of the system.
    Given I am on the registration page
    When I hit the cancel button
    Then I am redirected to "/"