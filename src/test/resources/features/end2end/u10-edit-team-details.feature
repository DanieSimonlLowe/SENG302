Feature: As Julie, I want to edit my team details so that the details are kept accurate

  Scenario: AC5 - Given I am on the edit profile form, when I hit the cancel button, I come back to the team’s profile page, and no changes have been made to its profile.
    Given I am logged in as Tom
    And I am on page "/teamEdit?displayId=1"
    When I enter a team name "evilCanEvil"
    And I hit the cancel button
    Then I am redirected to "/teamProfile?id=1"
    And Team name is "Test Team"

  Scenario: Editing another teams details (logged out)
    Given I am not logged in
    When I navigate to page "/teamEdit?displayId=1"
    Then I am redirected to "/login"

  Scenario: Editing another teams details (logged in)
    Given I am logged in as Natalie
    When I navigate to page "/teamEdit?displayId=1"
    Then I am redirected to "/"
