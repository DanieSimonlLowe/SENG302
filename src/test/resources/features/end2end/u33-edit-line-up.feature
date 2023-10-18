Feature: U33 - Edit line-up for game
  Background:
    Given I am logged in as Tom

  Scenario Outline: AC1 - Adding a formation to an activity
    Given I have a team with a formation
    And I have an activity with type <type>
    When I am on the edit activity page of that activity
    And I navigate to the lineup tab
    Then I can add a line-up from the list of existing formations for that team

    Examples:
      | type       |
      | "game"     |
      | "friendly" |

#    Scenario Outline: AC2 - The formation is displayed in the activity page
#      Given I am on the edit activity page of a team-based activity with type <type>
#      When I select a formation from the existing team's formation for that game
#      Then the formation is displayed in the activity page
#
#      Examples:
#        | type       |
#        | "game"     |
#        | "friendly" |
#
#    Scenario: AC3 - Filling positions on the line-up with players
#      Given I have selected a formation for the game
#      When I click on a player's icon
#      Then I can select a player from my team to fill in that position
#
#    Scenario: AC4 - No duplicate players and displaying correctly on the line-up
#      When I select a player for a position
#      Then I cannot select them for another position
#      And their profile picture is shown on the line-up
#      And their first name is shown below their picture
#
#    Scenario: AC5 - Select substitutes for a game
#      When I have selected all players to fill in the formation for the game
#      Then I can select one or more players who are substitutes for that game
#
#    Scenario: AC6 - Displaying substitutes correctly
#      When I have selected substitutes for that game
#      Then their full names are displayed next to or below the line-up
#      And their profile pictures are displayed next to or below the line-up
#
#    Scenario: AC7 - Error when line-up is not full
#      Given I have selected a formation for the game
#      When I have not filled in all positions with a player
#      Then I cannot save the activity
#      And I am shown an error message telling me the line-up is not complete
#
    Scenario: AC8 - Cancel changing formation on an activity
      Given I am on page "/editActivity?id=1"
      And I have made changes to the activity
      When I hit the cancel button
      Then The current changes to the formation are ignored