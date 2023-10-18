Feature: Moderate reactions As Inaya, I want to monitor reactions on blog posts of clubs and teams I manage so that I can prevent problematic comments

  Scenario: AC4 - Given I am on my profile, Then I can see a list of followed teams.
    Given I have a user Sam
    When I follow a team
    Then The team is added to my list of followed teams