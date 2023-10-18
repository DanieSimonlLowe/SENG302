Feature: U3 - (User) View user profile
  Scenario: AC1: See all my details on the profile page
    Given I am logged in as Tom
    When I am on page "/profile"
    Then I can see all my details

  Scenario: AC1: If I add a sport, I can see it on the page
    Given I am logged in as Tom
    When I add a favourite sport "Football"
    Then I can see the sport "Football" on my profile
