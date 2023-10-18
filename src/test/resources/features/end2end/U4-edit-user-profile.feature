
Feature: U4 - (User) edit a users profile.
  Scenario: AC1/AC2 -  Given I am on my user profile page, when I hit the edit button, then I see the edit profile form with all my details prepopulated except the passwords.
    Given I am logged in
    Given I am on page "/profile"
    When I click on edit profile button
    Then I am redirected to "/edit"
    Then The edit fields have correct values

  Scenario Outline: AC3 - Given I am on the edit profile form, and I enter invalid values (i.e. empty strings or non-alphabetical characters) for my first name and/or last name, when I hit the save button, then an error message tells me these fields contain invalid values.
    Given I am logged in
    Given I am on page "/edit"
    When I enter incorrect <values> for First Name and Last Name
    Then an error message tells me I must have a valid first name and last name
    Examples:
      | values   |
      | "sd34!"    |
      | "12"  |
      | "!!"  |

  Scenario Outline: AC4 - Given I am on the edit profile form, and I enter a malformed or empty email address, when I hit the save button, then an error message tells me the email address is invalid.
    Given I am logged in
    Given I am on page "/edit"
    When I enter incorrect <emails> for email
    Then an error message tells me that the email is invalid
    Examples:
      | emails   |
      | "sdfgdsfh@kjdsnbfgdfg"    |
      | "sdfgdfg@dfg"  |
      | "sdfgdfg@gmail"  |

  Scenario: AC5 - Given I am on the edit profile form, and I enter a date of birth for someone younger than 13 years old, when I hit the save button, then an error message tells me I cannot register into the system because I am too young.
    Given I am logged in
    Given I am on page "/edit"
    When I enter incorrect details for DOB
    Then an error message tells me that the DOB is invalid


  Scenario: AC6 - Given I am on the edit profile form, and I enter a new email address that already exists in the system, when I hit the save button, then an error message tells me the email address is already registered.
    Given I am logged in
    Given I am on page "/edit"
    When I enter a email that already exists
    Then an error message tells me that the email is already in use

  Scenario: AC7 - Given I am on the edit profile form, when I hit the cancel button, I come back to my profile page, and no changes have been made to my profile.
    Given I am logged in
    Given I am on page "/edit"
    When I change my first name to "ELVIS"
    When I hit the cancel button
    Then I am redirected to "/profile"
    Then My name is stays the same