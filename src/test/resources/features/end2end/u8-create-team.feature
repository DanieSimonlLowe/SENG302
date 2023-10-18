Feature: U8 - (Team) Create a new team

  Scenario Outline: AC2 -  Given I am on the create team form and I enter a name, a location, and a sport, when I hit the create button, then a new team is created into the system, and I see the team’s page.
    Given I am logged in
    And I am on page "/form"
    When I enter a name <teamName>
    And I enter an address1 <address1>
    And I enter a suburb <suburb>
    And I enter a postcode <postcode>
    And I enter a city <city>
    And I enter a country <country>
    And I enter a sport <sport>
    And I click the submit button
    Then I am viewing my team profile
    Examples:
      | teamName | address1          | suburb            | postcode      | city           | country       | sport        |
      | "a"      | "802 clarence st" | "upper riccarton" | "8041"        | "Christchurch" | "New Zealand" | "basketball" |
      | "{a a}"  | "8 a"             | "no"              | "801-203-433" | "Azerbaijan"   | "London"      | "sport"      |

  Scenario Outline: AC3 - Given I enter either an empty team name or a name with invalid characters for a team (e.g., non-alphanumeric other than dots or curly brackets, name made of only acceptable non-alphanumeric), when I hit the create new team button, then an error message tells me the name is invalid.
    Given I am logged in
    And I am on page "/form"
    When I enter a name <teamName>
    And I enter an address1 <address1>
    And I enter a suburb <suburb>
    And I enter a postcode <postcode>
    And I enter a city <city>
    And I enter a country <country>
    And I enter a sport <sport>
    And I click the submit button
    Then I am still on the page "/form"
    Examples:
      | teamName  | address1    | suburb             | postcode      | city           | country       | sport        |
      | " "       | "21 clar"   |  "upper riccarton" | "8041"        | "Christchurch" | "New Zealand" | "basketball" |
      | " a"      | ""          |  "upper riccarton" | "8041"        | "Christchurch" | ""            | "^(&^@(*"    |
      | "team"    | "address"   |  "upper riccarton" | "!!8041"      | "Christchurch" | "New Zealand" | "basketball" |
      | "team"    | "1 address" |  "upper riccarton" | "8041"        | "()&(*&)&)"    | "New Zealand" | "basketball" |
      | "team"    | "1 address" |  "upper riccarton" | "---"         | "Christchurch" | "New Zealand" | "basketball" |
      | "team"    | "1 address" |  "upper riccarton" | "1234"        | " "            | "New Zealand" | "basketball" |
      | "team"    | "1 address" |  "upper riccarton" | "1234"        | "Christchurch" | " "           | "basketball" |
      | "team"    | "1 address" | "upper riccarton"  | "1234"        | "Christchurch" | "New Zealand" | " "          |
      | "hep "    | "21 la!_bye"|  "upper riccarton" | "8041"        | "Christchurch" | "New Zealand" | "basketball" |
      | "hep "    | "21 clar"   |  "up 0m@string {}" | "8041"        | "Christchurch" | "New Zealand" | "basketball" |
      | "hep "    | "21 clar"   |  "upper riccarton" | "8041"        | "Christchurch" | "Costco22 @#" | "basketball" |
