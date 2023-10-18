#Feature: UT1.4 - View a team's homepage
#  Background:
#    Given I am logged in
#
#  Scenario: AC1 - Authorized viewing of a team's homepage
#    Given I am on a team's profile that I am apart of
#    When I navigate to the team's homepage
#    Then The team's homepage should be visible
#
#  Scenario: AC2 - Unauthorized viewing of a team's homepage
#    Given I am on a team's profile that I am not apart of
#    When I navigate to the team's homepage
#    Then The team's homepage should not be visible
