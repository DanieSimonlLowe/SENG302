Feature: U25 – As Julie, I want to specify roles to team members so that members can have a personalised experience in the team.

  Scenario: AC6 - At least one manager in a team
    Given I update the roles of team members
    When I don't have any managers anymore
    Then An error message tells me that I "At least one manager is required"
    And The changes are not saved

  Scenario: AC7- No more than three managers in a team
    Given I update the roles of team members
    When I have more than three managers for the team
    Then An error message tells me that I "No more than three managers are allowed"
    And The changes are not saved