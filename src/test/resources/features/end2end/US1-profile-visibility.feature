Feature: US1 - Profile visibility As Ashley, I want to be able to make my profile and personal stats private, friends-only, or public so that I can keep an eye on my privacy.
  Background:
    Given I am logged in

  Scenario Outline: AC1 - given a user views there own profile when they select between private, friends-only, or public then their account is set to  private, friends-only, or public.
    Given I am on page "/profile"
    When I select <setting> privacy level
    Then The privacy level has changed to <setting>
    Examples:
    | setting         |
    | "PRIVATE"       |
    | "FREINDS_ONLY"  |
    | "PUBLIC"        |

  Scenario: AC2 - given a user is private when other users view them then other users can't see there details other then their name and a follow button.
    When I view a private user
    Then I can't see user details
    Then I can see the name and the follow button

#  Scenario: AC3 - given a user is public when other users view them then other users see there details.
#    When I view a public user
#    Then I can see user details
#
