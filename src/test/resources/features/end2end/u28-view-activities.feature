
Feature: U28 - View my activities
#  Background:
#    Given I am logged in as Tom
##
#  Scenario: AC1 - Given I am anywhere on the system, when I click on a UI element to see all my activities, then I see a list of all activities from all the teams I belong to and all the personal activities I created for myself.
#    Given I am logged in
#    And I have created 1 activities
#    When I click the navbar element with id "#myActivities"
#    Then I see the activity I created

#  Scenario: AC2 - Given I see the calendar containing all my activities, when I have personal activities, they are coloured different to the other activities.
#    Given I have 1 personal activity
#    And I have 1 team activity
#    Then the personal activity has the colour "#DDDDDD"
#    And the team activity does not have the colour "#DDDDDD"
#
#  Scenario: AC4 - Given I see the calendar of all my activities, when there are multiple activities of the same team, they are all coloured the same.
#    Given I have 2 team activity
#    Then the 2 team activities are the same colour
#
#  Scenario Outline:  AC5 - Given I am on the page to view my activities and I have activities, when I click an activity , Then I see the activities details.
#    Given I am logged in as Tom
#    When I click on an activity with <name>
#    Then I see an activity card with <name>
#    Examples:
#      | name    |
#      | "test1" |
#      | "test2" |
#
#  Scenario Outline: AC6 -  Given I am seeing the details of a team activity, When I click the team name, Then I am redirected to the team profile
#    Given I am logged in as Tom
#    When I click on an activity with <name>
#    When I click on the activity team <iter>
#    Then I am redirected to team profile <team>
#    Examples:
#      | name | team | iter |
#      | "test1"  |  2   | 1    |