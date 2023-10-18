Feature: U24 – As Julie, I want to share the details of my team in the system so that my players can join it

  Scenario: AC1 - When I am on the team profile page of a team I manage, then I can see a unique secret token for my team that is exactly 12 char long with a combination of letters and numbers, but no special characters.
    Given There is a team called "BananaBreadTeamTest" that I manage
    When I retrieve the token for my team
    Then I can see a valid unique token for my team

  Scenario: AC2 - Given I am on the team profile page, when I generate a new secret token for my team, then a new token is generated, and this token is unique across the system, and that token is not a repeat of a previous token.
    Given A team I manage has a token
    When I generate a new token for my team
    Then The new token doesn't match the old token
