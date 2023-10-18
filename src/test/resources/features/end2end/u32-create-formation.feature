Feature: U32 - Create formation
  Background:
    Given I am logged in as Tom

  Scenario: AC1 - View team's formations
    Given I have a formation
    And I am on my team's profile
    When I click on a UI element to see all the team's formations
    Then I see a list of all formations for that team

  Scenario Outline: AC2 - See a graphical representation of sport pitch on sport formation page
    Given I am on a formation creation page
    When I click on a UI element to create a new line-up with the sport <sport>
    Then I see a graphical representation of a sport pitch corresponding to the sport <sport> of that team
    Examples:
      | sport      |
      | "football_pitch" |
      | "baseball_field" |
      | "hockey_pitch"   |

  Scenario Outline: AC3 - Specify a valid player formation
    Given I am on a formation creation page
    When I set up the number of players per sector to <numPlayers>
    When I click the formation save button
    Then I am redirected to my team's page
    Examples:
      | numPlayers |
      | "1"        |
      | "1-3-2-1"  |
      | "15-15-15"      |
      | "1-1-1-1-1-1-1-1" |

#  Scenario Outline: AC4 - Invalid formation input
#    Given I am logged in
#    And I am on a formation creation page
#    When I enter in the formation <formation>
#    And I click the submit formation button
#    Then An formation error message is displayed
#    Examples:
#      |       formation       |
#      | "1-2-3-4-5-6-7-8-9-0" |
#      |       "1-16-4"        |
#      |       "15-120"        |
#      |         "22"          |
#      |       "4-2-1-"        |
#      |          ""           |
#      |       "-4-4-2"        |
#      |       "badInput"      |
#      |       "test-1"        |

  Scenario Outline: AC5 - Can view the icons of players organised in the pattern specified
    Given I am on a formation creation page
    When I set up the number of players per sector to <numPlayers>
    Then I see icons of players organised as described by the pattern on the graphical pitch
    Examples:
      | numPlayers |
      | "1"        |
      | "1-3-2-1"  |
      | "15-4-1"      |
      | "1-2-3-4-5-6-7" |
#
# Can't be implented yet but will be able to be implented when viewing activty
#  Scenario: AC6 - Persisting the formation on the system
#    Given I have correctly set up a formation with the number of players per sector
#    When I click on the create formation button
#    Then the formation is persisted in the system
#