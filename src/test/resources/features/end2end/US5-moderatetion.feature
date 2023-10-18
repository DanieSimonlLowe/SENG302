#Feature: Moderate reactions As Inaya, I want to monitor reactions on blog posts of clubs and teams I manage so that I can prevent problematic comments
#  Background:
#    Given I am logged in
#
#  Scenario: AC1 - Given I manage a team or club, when I view a reaction to it's blog, Then I see a button to remove the reaction.
#    Given I view the reactions to a team I manage
#    Then I can see a delete button
#
#  Scenario: AC2 - Given I manage a team or club, when I click the button to remove a reaction to it's blog, then the reaction is removed from the blog.
#    Given I view the reactions to a team I manage
#    When I click on a button to remove a reaction
#    Then The Reaction is removed