Feature: U26 - As Julie, I want to specify roles to team members so that members can have a personalised experience in the team

  Scenario: AC1 - Viewing team members and their roles
    Given I am in the team profile page of a team I manage
    When I click on a UI element to edit the members role
    Then I see the list of members and their roles in the team