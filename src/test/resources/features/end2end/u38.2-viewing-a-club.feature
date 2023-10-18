Feature: U38.2 - Viewing a club
  #this can't be run isolated as it fails if the other tests have not run first.
  Scenario Outline: AC1 - Searching for clubs
    Given I am on page "/allClubs"
    When I enter a clubs search string of <search>
    And I click the clubs search submit button
    Then The only the clubs <clubs> are shown
    Examples:
      | search | clubs |
      | "club" | "Best Club,the normal club,the abnormal club" |
      | "Chicago" | "the abnormal club" |

  Scenario Outline: AC2 - Filtering clubs search
    Given I am on page "/allClubs"
    When I select cities <cities>
    And I select sports <sports>
    And I click the filter button
    Then The only the clubs <clubs> are shown
    Examples:
      | cities | sports | clubs |
      | "CHICAGO" | ""    | "the abnormal club" |
      | ""        | "Basketball" | "Best Club,the normal club"       |
      | "CHRISTCHURCH"          |    "Baseball"          |      "nz football,league of evil"        |
#
  Scenario: AC3 - Link to club in team's profile page
    Given I am logged in as Tom
    When I am on the profile page for a team with a club
    Then I see the name of the club
    And There is a link to the club

  Scenario Outline: AC4 - Team displays the club anywhere on system.
    Given I am on page <page>
    When I see a team <id>
    Then There is a UI element that displays the team <id> is a part of <club>
    Examples:
      | page | id | club |
      | "/allTeams" | 7 | "the normal club"|
      | "/allTeams?page=2" | 16 | ""        |
      | "/allTeams?page=2" | 9  | "the normal club"|
      | "/allTeams?search=cub" | 5 | "league of evil" |


#
#  Scenario Outline: AC5 - Display teams on the club page
#    When I am on the profile club page for <id>
#    Then The teams <teams> are displayed in the clubs profile
#    Examples:
#      | id | teams |
#      | 1  | "Test Team,Dolphins,Heat" |
#      | 2  | "Marlins" |
#      | 3  | "Cubs" |
#

  Scenario Outline: AC6 - Too short search
    Given I am on page "/allClubs"
    When I enter a clubs search string of <search>
    And I click the clubs search submit button
    Then An error message tells me that my query is too short
    Examples:
      | search |
      | "a"    |
      | "  a"  |
      | " b "  |
      | "ab "  |
    
  Scenario: Given I am on the teams page I can get to the clubs page
    Given I am on page "/allTeams"
    When I click the clubs tab
    Then I am redirected to "/allClubs"

  Scenario: Given I am on the clubs page I can get to the teams page
    Given I am on page "/allClubs"
    When I click the teams tab
    Then I am redirected to "/allTeams"
