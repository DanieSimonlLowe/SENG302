Feature: U35 - View activity statistics
  Background:
    Given I am logged in as Tom

  Scenario: AC1 - Viewing activities with statistics
    Given I am on page "/viewActivities"
    And I am viewing "test" activity on "December 2023"
    Then The activity statistics popup opens

#  Scenario Outline: AC2 - Viewing players scores on the line-up
#    Given I have an activity of type <type> with a line-up
#    And I add a score to the activity
#    When I click a player's icon on the line-up
#    Then I can see the time when the player scored next to their icon on the line-up
#    Examples:
#      | type        |
#      | "game"      |
#      | "friendly"  |

  Scenario: AC3 - Viewing substitutions on the lineup
    Given I am on page "/viewActivities"
    And I am viewing "test" activity on "December 2023"
    When I click on the lineup
    And I click the user at position 0, 0
    Then I see the that "Morgan" was substituted for "Tom" at 23

  Scenario: AC4 - Sorting statistics and facts by time
    Given I am on page "/viewActivities"
    And I am viewing "test" activity on "December 2023"
    When I click on the facts
    Then the facts are sorted by their time in ascending order