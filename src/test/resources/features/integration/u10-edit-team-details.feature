Feature: U10 - Edit a Team's Details
  Background:
    Given I am logged in as Tom

  Scenario: AC2  - When I edit a team with valid details, the team's details are saved.
    Given I own a team
    When I edit the team's details
    Then The team's details are updated