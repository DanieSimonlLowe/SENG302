Feature: As Ashley, I want to specify what sports I like so that I can get a personalised experience on the system.

  Scenario: AC1 - Given I am on the edit user profile form, when I want to add my favourite sport,  I can add a new sport name by manually.\
    Given I am logged in as Tom
    Given I am on page "/edit"
    When I add my sport "Karate"
    Then I can see the sport on the page

  Scenario: AC2 - Given I am on the edit user profile form, when I want to add my favourite sports, then I can add more than one sport.
    Given I am logged in as Tom
    Given I am on page "/edit"
    When I add my sport "Karate" and "Sport"
    Then I can see the sports on the page

  Scenario: AC3 - Given I am on the edit user profile form, when I delete one of my favourite sports, then it is not shown in my list of favourite sports.
    Given I am logged in as Tom
    Given I am on page "/edit"
    Given There is a sport "Karate" on my page already
    When I delete the sport "Karate"
    Then The sport "Karate" is not there


  Scenario Outline: AC4 - Given I add my favourite sport manually (i.e. not from the list of existing sports), when I add an empty sport or a sport with invalid characters (i.e. any non-letters except spaces,
  apostrophes and dashes), then an error message tells me the sport name is invalid.
    Given I am logged in as Tom
    Given I am on page "/edit"
    When I add the <invalidSport>
    Then an error <message> is shown

    Examples:
      | invalidSport | message |
      | "   "  |  "Sport name invalid - must only contain letters, spaces, apostrophes and dashes."  |
      | "?21!" |   "Sport name invalid - must only contain letters, spaces, apostrophes and dashes." |
      | "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" | "Sport name invalid - cannot be longer than 200 characters." |
      |  ""  | "Sport name invalid - must only contain letters, spaces, apostrophes and dashes."  |
      |  "team!"  | "Sport name invalid - must only contain letters, spaces, apostrophes and dashes."  |

