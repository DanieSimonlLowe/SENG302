Feature: U34 - Add activity statistics

  Background:
    Given I am logged in as Tom

  Scenario: Given I have an activity and I click on it, the activity statistics popup is opened
    Given I create an activity with description "Game Day!"
    And I am on the view activities page
    When I click on the activity card
    When I click on the stats button
    Then The activity statistics popup opens

#    Scenario Outline: Given I have an activity, When I change it's game score, then it's game score is updated
#      Given I am on page "/viewActivities"
#      And I am viewing "test" activity on "December 2023"
#      When I click on the score page
#      And I set it's game scores to <score1> <score2>
#      # below this is reloading page
#      And I am on page "/viewActivities"
#      And I am viewing "test" activity on "December 2023"
#      And I click on the score page
#      Then The game scores are <score1> <score2>
#      Examples:
#        | score1 | score2 |
#        | "1-2"  | "3-4"  |
#        | "5"  | "7"  |
#        | "1-1" | "1-6-4-1" |


  Scenario Outline: Given I have an activity, When I change it's game score to an invalid value, then an error is shown.
    Given I am on page "/viewActivities"
    And I am viewing "test" activity on "December 2023"
    When I click on the score page
    And I set it's game scores to <score1> <score2>
    Then I see a game score error of <error1> <error2>
    Examples:
      | score1 | score2 | error1                                                | error2                                                |
      | "1-2-" | "3-4"  | "Invalid score input. Please enter in the format 'n'" | ""                                                    |
      | "5-1"  | "7"    | "Invalid score input. Please enter in the format 'n'" | ""                                                    |
      | "1"    | "c"    | ""                                                    | "Invalid score input. Please enter in the format 'n'" |
      | "1"    | ""     | ""                                                    | "Please enter in the format 'n'"                      |
      | ""     | "1-2"  | "Please enter in the format 'n'"                      | ""                                                    |
      | "1-1"  | "1"    | "Invalid score input. Please enter in the format 'n'" | ""                                                    |



#  Scenario Outline: AC1 - Adding a valid activity score
#    Given I have an activity of type <type>
#    When I introduce a separate score value for each team
#    Then The scores must be a single integer or dash separated integers
#    And The score formats must match
#
#    Examples:
#      | type       |
#      | "game"     |
#      | "friendly" |
#
#  Scenario Outline: AC2 - Adding individual goals to an activity
#    When I have an activity of type <type>
#    Then I can add the names of the person who scored to the activity
#    And I can add the times of when they scored to the activity
#
#    Examples:
#      | type       |
#      | "game"     |
#      | "friendly" |

#  Scenario Outline: AC3 - Record substitutions
#    Given I have an activity of type <type>
#    And I am on the view activities page
#    When I click on the activity card
#    And I click on the stats button
#    And I select the substitution tab
#    Then I can record which player was substituted off of the field
#    And I can record which player was substituted on to the field
#    And I can record when the players were swapped
#
#    Examples:
#      | type       |
#      | "game"     |
#      | "friendly" |

  Scenario Outline: AC4 - Recording facts about an activity
    Given I have an activity of type <type>
    And I am on the view activities page
    When I click on the activity card
    And I click on the stats button
    And I select the facts tab
    Then I can record facts about that activity with a description and optional time

    Examples:
      | type          |
      | "game"        |
      | "friendly"    |
      | "competition" |
      | "other"       |