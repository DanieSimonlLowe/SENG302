#Feature: Moderate reactions As Inaya, I want to monitor reactions on blog posts of clubs and teams I manage so that I can prevent problematic comments
#  Background:
#    Given I am logged in
#
#  Scenario Outline: AC1 - Given I am logged in, When a team or club I follow adds an public activities or activity stat to a public activity Then within 10 seconds I am informed.
#    When a <name> posts <type>
#    Then I am informed within 10 seconds.
#    Examples:
#    | name  | type |
#    | "test"|"type"|
#
#  Scenario: AC3 - Given I am logged in, When I am on any page, Then I can see an icon with the number of unviewed updates
#    When I am on any page
#    Then I can see an icon with the number of unviewed updates
#
#  Scenario: AC4 - Given I am logged in, When I click on the update icon, Then I go to a page with all the updates
#    When I click on the update icon
#    Then I go to a page with all the updates
#
#  Scenario: AC5 - Given I am on the updates page, When there are more then one update, Then they are ordered from latest to earliest
#    Given I am on the updates page
#    When there are more then one update
#    Then they are ordered from latest to earliest
#
#  Scenario: AC6 - Given I am on the updates page, When an update is displayed on page then the number of unviewed updates is decreased by 1
#    Given I am on the updates page
#    When an update is displayed on page
#    Then the number of unviewed updates is decreased