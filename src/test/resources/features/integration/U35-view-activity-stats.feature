Feature: U32 - View Activity Statistics

  Background:
    Given Tom is logged in

  Scenario: AC2 - Check rest controller passes correct information
    Given I have a team that has a formation "1-2"
    And I have an activity with a lineup
    When I view the lineup on the activity statistics
    Then I get the lineup