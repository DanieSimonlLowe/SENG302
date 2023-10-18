#Feature: U36 - View aggregated team statistics
#  Background:
#    Given I am logged in
#
#  Scenario: AC1 - Viewing team match stats
#    Given there is a team which has played some games or friendlies
#    When I am looking at that team
#    Then I can see how many games that team has played
#    And I can see how many games that team has won
#    And I can see how many games that team has lost
#    And I can see how many games that team has drew
#
#  Scenario: AC2 - Viewing a game results trend
#    Given there is a team which has played some games and friendlies
#    When I am looking at that team
#    Then I can see a trend of the last 5 matches in terms of games and friendlies won, lost and drew
#    But friendlies look different from games in this trend
#
#  Scenario: AC3 - Viewing top scorers
#    Given there is a team which has played some games or friendlies
#    When I am looking at that team
#    Then I can see the names of the top 5 scorers
#    And I can see the total number of goals each player has scored overall
#
#  Scenario: AC4 - Viewing players who have played the longest
#    Given there is a team which has played some games or friendlies
#    When I am looking at that team
#    Then I can see the names of the top 5 players who have played for the longest overall
#    And I can see their overall playtime
#    And I can see their average play time per game
#
#  Scenario: AC5 - Viewing last 5 activities that have a score
#    Given there is a team which has played some games or friendlies
#    And these games or friendlies have scores
#    When I am looking at that team
#    Then I can see the result of their last 5 games
#    And I can see each game's date
#    And I can see each game's score
#
#  Scenario: AC6 - Linking each displayed activity to the expanded activity page
#    Given there is a team which has played some games or friendlies
#    When I am looking at that team
#    And I click one of the activities displayed
#    Then I can see the activities details
#
#  Scenario: AC7 - Accessing a list of all activities team has taken part in
#    Given there is a team which has taken part in some activities
#    When I click a dedicated UI element to see the activities the team has taken part in
#    Then I see a list of all the activities the team has taken part in