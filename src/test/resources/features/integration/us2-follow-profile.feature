Feature: US2 - Follow Profiles

  Background:
    Given Tom is logged in

  Scenario: Tom becomes friends with Nathan
    Given I have a user Tom and a user Nathan
    When Tom follows Nathan
    And Nathan follows Tom
    Then Tom and Nathan are friends