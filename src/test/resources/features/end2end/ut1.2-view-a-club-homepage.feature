#Feature: UT1.2 - View a club homepage
#  Background:
#    Given I am logged in
#
#  Scenario: AC1 - Authorized viewing of a club's homepage
#    Given I am on a club's profile that I am apart of
#    When I navigate to the club's homepage
#    Then The club's homepage should be visible
#
#  Scenario: AC2 - Unauthorized viewing of a club's homepage
#    Given I am on a club's profile that I am not apart of
#    When I navigate to the club's homepage
#    Then The club's homepage should not be visible
