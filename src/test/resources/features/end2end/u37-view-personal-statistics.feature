#Feature: U37 - View aggregated personal statistics
#  Background:
#    Given I am logged in
#
#
#  Scenario Outline: AC1 - Viewing statistics on the profile page
#    Given I have statistics saved for some <type>
#    When I view my profile
#    Then I can see statistics for each <type> I belong to
#
#    Examples:
#      | type    |
#      | "sport" |
#      | "team"  |
#
#  Scenario: AC2 - Viewing sport statistics
#    Given I have statistics saved for some "sport" belonging to an individual activity
#    When I view my profile
#    Then statistics are grouped by individual sport
#
#  Scenario: AC3 - Viewing personal team statistics
#    Given I have statistics saved for some "team"
#    When I view my profile
#    Then I can see how many goals I have scored for that team
#    And I can see how much time I have played overall for that team
#    And I can see how much time I spend playing on average per match in that team
#    And I can see how many matches I have played in out of the total number of matches that team has played
#
#  Scenario: AC4 - Viewing personal detailed team activity statistics
#    Given I have statistics saved for some "team"
#    When I am viewing my personal statistics for an activity of that team
#    Then I can see how much time I played
#    And I can see how many goals I scored and the time of said goals
#    And I can see if I substituted or was substituted along with the time
#
#  Scenario: AC5 - Accessing full activity details
#    Given I have activities
#    When I view my activities
#    And click on one of my activities
#    Then I can see the full details of that activity
