Feature: U25 – let users join a preexisting team in not that tab.

  Scenario Outline: AC1 - Given I am anywhere on the system, when I click on a dedicated UI element to see the teams I am a member of, then I can choose to join a new team
    Given I am logged in as Natalie
    And I am on <page>
    When I hit the button to choose to join a new team
    Then I am on the join a team page
    Examples:
      | page           |
      |  "/"            |
      |  "/login"       |
      |  "/profile"     |
      |  "/allProfiles" |
      |  "/allTeams"    |
      |  "/edit"        |


  Scenario: AC2 - Given I am being showed an input to join a new team, when I input an invitation token that is associated to a team in the system, then I am added as a member to this team.
    Given I am logged in as Natalie
    And I am on page "/joinTeam"
    When I input an invitation token that is associated to a team in the system
    Then I am added as a member to this team

  Scenario: AC3 - Given I am being showed an input to join a new team, when I input an invitation token that is not associated to a team in the system, then an error message tells me the token is invalid.
    Given I am logged in as Natalie
    And I am on page "/joinTeam"
    When I input an invitation token that is not associated to a team in the system
    Then an error message tells me the token is invalid

  Scenario: AC4 - Given I have joined a new team, when I click on a dedicated UI element to see the teams I am a member of, then I see the new team I just joined
    Given I am logged in as Natalie
    And I have joined a new team
    When I click on a dedicated UI element to see the teams I am a member of
    Then I see the new team I just joined