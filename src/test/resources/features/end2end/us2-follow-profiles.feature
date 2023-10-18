Feature: US2 - Follow profiles
  Background:
    Given I am logged in

  Scenario: AC2 - Following a user
    Given I am on page "/otherProfile?email=morgan.english8@hotmail.com&prevPage=1&search="
    When I hit the follow button
    Then I am following the user

  # It is more beneficial to test AC1 after AC2 to verify that followed users are shown
  Scenario: AC1 - View users I am following
    Given I am on page "/profile"
    When I hit the following button
    Then I see a list of user profiles I follow

#  Scenario: AC3 - Receive workout notifications
#    Given I am anywhere on the system
#    When a user I follow starts a workout
#    Then I receive a notification
#
#  Scenario: AC4 - Unfollowing users
#    Given I am on my following page
#    When I hit the unfollow button on a user
#    Then The user is removed from the list of user's I follow

Scenario: AC3 - Unfollow users
  Given I am following a user
  When I hit the unfollow button
  Then I am not following the user