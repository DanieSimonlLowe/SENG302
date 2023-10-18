#Feature: U41 – View competition scoreboard
#  Background:
#    Given I am logged in
#
#  Scenario: AC1 - Viewing a the results of a regular or tournament competition's matches
#    Given A regular or tournament competition exists
#    When I open the details of the competition
#    Then I can see the results of all the matches
#    And The matches are ordered by date
#    And Then the matches are in alphabetical order
#
#
#
#  Scenario: AC2 - Viewing a regular or tournament competition's upcoming matches
#    Given A regular or tournament competition exists
#    When I open the details of the competition
#    Then I can see the next matches to come
#    And The matches are ordered by date
#
##  Scenario: AC3 - Define the points calculation rules for a regular or tournament competition
##    Given I am logged in as a federation adminsitrator
##    When I
#
#  Scenario: AC4 - Viewing the teams' results in a regular or tournament competition
#    Given A regular or tournament competition exists
#    When I view the results of the competition
#    Then I can see a table of all the teams' results in the competition
#    And The teams are sorted according to their points
#
#
#  Scenario: AC5 - Viewing the results of an event type competition
#    Given An event competition exists
#    When I view the results of the event type competition
#    Then I can see the results grouped by events for each individual
#    And The time results are sorted in ascending order
#
#
#  Scenario: AC5 - Viewing the results of an event type competition
#    Given An event competition exists
#    When I view the results of the event type competition
#    Then I can see the results grouped by events for each individual
#    And The score results are sorted in descending order