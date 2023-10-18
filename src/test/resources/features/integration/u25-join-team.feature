Feature: U25 – As Teka, I want to join a team in the system so that I can interact with my teammates and follow what's going on with my team.

  Scenario Outline: AC2 - Joining a team with a valid invitation token
    Given A user with the first name "Jane", last name "Smith" and email "janesmith@fake.com" exists
    And There is a team called <teamName>, with city <city>, country <country> and sport <sport>
    When I input an invitation token associated with the team
    Then I am a member of the team
    Examples:
      | teamName        | city            | country       | sport         |
      | "Big Dogs"      | "Christchurch"  | "New Zealand" | "Futsal"      |
      | "Trial Blazers" | "Portland City" | "USA"         | "Basketball"  |

  Scenario: AC3 - Joining a team with an invalid invitation token
    Given A user with the first name "Jane", last name "Smith" and email "janesmith@fake.com" exists
    When I input an invitation token not associated with any team
    Then I am a not a member of a team
