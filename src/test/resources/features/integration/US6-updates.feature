#Feature: Moderate reactions As Inaya, I want to monitor reactions on blog posts of clubs and teams I manage so that I can prevent problematic comments
#
#  Scenario Outline: AC2 - Given I am logged out, When a team or club I follow adds an public activities or activity stat to a public activity Then an update is sent to my updates.
#    When a <name> posts <type>
#    Then an update is sent to my updates
#    Examples:
#      | name  | type |
#      | "test"|"type"|