Feature: U32 - Create formation
  Background:
    Given I am logged in as Tom and I have a team


  Scenario Outline: AC4 - Persisting the formation on the system
    When I provide a lineup with the player positions <players> and substitutions <substitutions>
    And I provide a formation string <formation>
    And I select a player for two different positions
    Then I am shown an error message saying that a player cannot be in two different positions

    Examples:
      |   players       |  substitutions  |  formation  |
      |     "1,2"       |       "1"       |    "1-1"    |
      |    "2,3,4,5"    |      "1,2"      |    "1-3"    |
      |   "3,3,4,4,4"   |      "1,2"      |   "2-1-2"   |



  Scenario Outline: AC7 - Persisting the formation on the system
    When I provide a lineup with the player positions <players> and substitutions <substitutions>
    And I provide a formation string <formation>
    And I do not fill in all the positions of the formation for the lineup
    Then I am shown an error message saying that all of the formation positions must be filled

    Examples:
      |   players   |  substitutions  |   formation   |
      |     "1"     |       "2"       |     "1-2"     |
      |     "2"     |        ""       |      "2"      |
      |   "5,2,3"   |      "1,4"      |   "1-1-1-1"   |