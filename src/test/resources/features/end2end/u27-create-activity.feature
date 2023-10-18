#Feature: U27 - Create activity
#
#  Scenario Outline: AC1 - Given I am anywhere on the system, when I click on a UI element to create an activity, then I see a form to create an activity.
#    Given I am logged in
#    And I am at <url> on the system
#    When I click on a UI element to create an activity
#    Then I see a form to create an activity
#    Examples:
#    | url         |
#    | "/"         |
#    | "/login"    |
#    | "/register" |
#
##  Scenario: AC2 - Given I am on the create activity page and I enter valid values for the team it relates to (optional), the activity type, a short description and the activity start and end time, when I hit the create activity button, then an activity is created into the system and I see the details of the activity.
##    Given I am logged in
##    And I am on the create activity page
##    And I enter values for the team it relates to (optional), the activity "other", a short description "string" and the activity start "2024-03-02T05:15" and end time "2024-03-12T05:15"
##    And I enter values for the location
##    When I hit the create activity button
##    Then an activity is created into the system and I see the details of the activity
#
#
#  Scenario: AC4 - Given I am on the create activity page, when I am asked to select the activity type, then I can select from: “game”, “friendly”, “training”, “competition”, “other”.
#    Given I am logged in
#    And I am on the create activity page
#    Then I can select from: game, friendly, training, competition, other.
#
#  Scenario Outline: AC5 - Given I select “game” or “friendly” as the activity type, when I do not select a team and I hit the create activity button, then an error message tells me I must select a team for that activity type.
#    Given I am logged in
#    And I am on the create activity page
#    And I enter values for the team it relates to (optional), the activity <type>, a short description "string" and the activity start "2024-03-02T05:15" and end time "2024-03-12T05:15"
#    When I hit the create activity button
#    Then an error message tells me I must select a team
#    Examples:
#      | type       |
#      | "game"     |
#      | "friendly" |
#
#  Scenario: AC6 Given I do not select the activity type, when I hit the create activity button, then an error message tells me I must select an activity type.
#    Given I am logged in
#    And I am on the create activity page
#    And I enter values for the team it relates to (optional), the activity "None", a short description "string" and the activity start "2024-03-02T05:15" and end time "2024-03-12T05:15"
#    When I hit the create activity button
#    Then an error message tells me I must select an activity type
#
#  Scenario Outline: AC7 Given I enter an empty description, a description made of numbers or non-alphabetical characters only, or a description longer than 150 characters, when I hit the create activity button, then an error message tells me the description is invalid.
#    Given I am logged in
#    And I am on the create activity page
#    And I enter values for the team it relates to (optional), the activity "other", a short description <dec> and the activity start "2024-03-02T05:15" and end time "2024-03-12T05:15"
#    When I hit the create activity button
#    Then an error message tells me I must have a valid description
#    Examples:
#      | dec   |
#      | ""    |
#      | "12"  |
#      | "!!"  |
#
#  Scenario: AC8 - Given I do not provide both a start and an end time, when I hit the create activity button, then an error message tells me the start and end time are compulsory
#    Given I am logged in
#    And I am on the create activity page
#    And I enter values for the team it relates to (optional), the activity "other", a short description "kjbhuy" and the activity start "" and end time ""
#    When I hit the create activity button
#    Then an error message tells me the start and end time are compulsory
#
#  Scenario: AC9 - Given I enter the activity start and end time, and I enter an end time before the start time, when I hit the create activity button, then an error message tells me the end date is before the start date.
#    Given I am logged in
#    And I am on the create activity page
#    And I enter values for the team it relates to (optional), the activity "other", a short description "string" and the activity start "2024-03-02T05:15" and end time "2024-02-12T05:15"
#    When I hit the create activity button
#    Then an error message tells me the end date is before the start date
#
#  Scenario: AC10 - Given I enter the activity start and end time, and I enter either a start or an end time before the team creation date, when I hit the save button, when I hit the create activity button, then an error message tells me the dates are prior the team’s creation date and the team’s creation date is shown.
#    Given I am logged in
#    And I am on the create activity page
#    And I enter values for the team it relates to (optional), the activity "other", a short description "string" and the activity start "2006-03-02T05:15" and end time "2010-03-12T05:15"
#    When I hit the create activity button
#    Then an error message tells me the dates are prior the team’s creation date and the team’s creation date is shown