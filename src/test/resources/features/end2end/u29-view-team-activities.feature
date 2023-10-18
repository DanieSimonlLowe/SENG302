Feature: U29 - View team activities

  Background:
    Given I am logged in
    #Allows checking that only team activities show
    And I have a personal activity
    And I am the owner of a team that has multiple different activities

#  Scenario: AC1 - Seeing all team activities
#    Given I am on my team’s profile
#    When I click on a UI element to see all the team’s activities
#    Then I see a list of all activities for that team

#  Scenario: AC2 - Sorting activities by time
#    When I see the list of all team activities
#    Then these activities are sorted by start time in ascending order