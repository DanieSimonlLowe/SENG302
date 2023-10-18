@ignore
Feature: US3 – Follow teams
  Background:
    Given I am logged in

#  Scenario: AC1 - Follow teams UI element present
#    Given I am viewing a team's page that I am not a member of
#    Then There is a UI element that will allow me to follow the team
#
#
#  Scenario: AC2 - A followed team's UI indication
#    Given I am following a team
#    When I view that team's profile page
#    Then There is an indication that I am following the team
#    And I cannot follow the team again
#
#
#  Scenario: AC3 - View a team's public activity on my feed
#    Given I am following a team
#    When I the team posts a public activity
#    Then I can see the team's public activity on my feed


  Scenario: AC4 - View a list of followed teams
    Given I am following a team
    When I open my profile page
    Then I can see the team I am following