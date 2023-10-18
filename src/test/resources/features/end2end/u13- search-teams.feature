Feature: As Julie, Teka, I want to search for all teams by their name or location so that I can find my friends.

  Scenario Outline: AC4 - Searching with a valid string
    Given I am on page "/allTeams"
    And I enter a search string "<query>"
    When I hit the search button
    Then The team "Test Team" is shown to me in the list of results
    Examples:
      | query        |
      | Christchurch |
      | Test         |

    Scenario Outline: AC7 - Search string less than 3 characters
      Given I am on page "/allTeams"
      And I enter a search string "<query>"
      When I hit the search button
      Then An error message tells me that my query is too short
      Examples:
        | query |
        | ch    |
        | te    |