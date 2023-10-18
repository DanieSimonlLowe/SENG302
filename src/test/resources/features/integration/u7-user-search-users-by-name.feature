Feature: U7 - (User) Search users by name

  Scenario: Can search for user after they have had their username changed.
    Given A given user exists
    When The user's name has changed
    Then I can find user by new name