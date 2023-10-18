Feature: U15 - User profile location API

  Scenario: The City and Country must be entered
   Given A user exists
    When The user location is edited
    Then The user city value cannot be null
    And The user country value cannot be null

  Scenario: The address, suburb and postcode are not mandatory
    Given A user exists
    When The user location is edited
    Then The user address values can be null
    And The user suburb can be null
    And The user postcode can be null