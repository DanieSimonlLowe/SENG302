# Created by Celeste at 4/07/2023
Feature: U14 - (Team) Teams location API

  Scenario: The City and Country must be entered
    Given A team exists
    Then The city value cannot be null
    And The country value cannot be null

  Scenario: The address, suburb and postcode are not mandatory
    Given A team exists
    When The team location is edited
    Then The address values can be null
    And The suburb can be null
    And The postcode can be null