Feature: U38.1 - Create a club
  Background:
    Given I am logged in as Tom

  Scenario: AC1 - UI element for creating a club
    When I am anywhere on the system
    And I click the corresponding UI element to create a club
    Then I am shown a form where I can create a club

  Scenario: AC2 - Creating a club with valid information
    Given I am on the create club page
    When I enter valid name and team and location
    And I confirm the new club
    Then A club is created into the system and I see the details of the club

  Scenario: AC4 - Creating a club with valid information and team that is already in a club
    Given I am on the create club page
    Then I don't see teams that are all ready in a club

  Scenario: AC6 - Viewing the UI element for updating a club
    Given I am on my clubs page
    When I click on the edit button
    Then I see a form with the clubs information pre-populated

  Scenario: AC7 - Updating a club with valid information
    Given I am on the edit club page
    When I change the name to "Best Club"
    And I confirm the new changes
    Then The club page has been changed to "Best Club" to reflect the new details

  Scenario Outline: AC8/AC2.1 - Creating / Updating a club with invalid information (club details)
    Given I am logged in
    And I am on the edit club page
    When I enter invalid information <invalidInfo>
    And I confirm the changes
    Then An error message is displayed

  Examples:
    |  invalidInfo  |
    |    "!!!!"     |
    |  "NewTest!"   |
    |    "He110"    |

  Scenario Outline: AC8/AC2.2 - Updating a club with invalid information (club location)
    Given I am on the clubs edit page
    And I have entered valid club details
    When I enter an invalid location <city> <country> <address1> <postcode>
    And I confirm the changes
    Then The error field <errorField> displays the text <errorText>

    Examples:
      |      city        |    address1      | postcode | country |    errorField       | errorText |
      |  "Christchurch"  |  "12@Drury Lane"  |  "2832"  | "New Zealand" |"address1Error" | "Address line 1 can contain letters, numbers, hyphens, apostrophes, forward slash, fullstops and commas." |
      |  "Christchurch"  |  "12 Drury Lane"  |  ""  | "New Zealand" |"postcodeError" | "Postcode can contain letters, numbers and hyphens." |
      |  ""  |  "12 Drury Lane"  |  "28/2"  | "New Zealand"|"cityError" | "City name can contain letters, numbers, hyphens, apostrophes, forward slash, fullstops and commas." |
      |  "Christchurch"  | "12 Drury Lane"  |  "28/2"  | "" |"countryError" | "Country name can contain letters, numbers, hyphens, apostrophes, fullstops and commas." |