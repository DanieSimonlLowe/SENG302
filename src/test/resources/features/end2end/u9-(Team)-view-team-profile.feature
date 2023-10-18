@ignore
Feature: As Julie, I want to see my teams' profile so that I can make sure they are still accurate.

  Scenario: AC2 - Team details page is not editable
    Given I am logged in
    And I have a team in the database
    And I am on my team details page
    When I see all the details
    Then I cannot edit any of the details that are shown to me
