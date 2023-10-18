#Feature: US4 – React on a blog post
#  Background:
#    Given I am logged in
#
#  Scenario: AC1 - Write a comment on a club's blog post
#    Given I follow a club
#    When The club makes a blog post
#    Then I can write a comment on the blog post
#    And the blog post has a maximum of 400 characters
#    And the blog post has a minimum of 5 characters
#
#
#  Scenario: AC2 - Emoji react to a club's blog post
#    Given I follow a club
#    When The club makes a blog post
#    Then I can add an emoji reaction to the blog post
#
#
#  Scenario: AC3 - Emoji react to an emoji reaction
#    Given I follow a club or team
#    And The club or team makes a blog post
#    When There is an emoji reaction on the blog post
#    Then I can add an emoji reaction to the emoji reaction
#
#
#  Scenario: AC4 - Write a comment on a team's blog post
#    Given I follow a team
#    When The team makes a blog post
#    Then I can write a comment on the blog post
#    And the blog post has a maximum of 400 characters
#    And the blog post has a minimum of 5 characters
#
#
#  Scenario: AC5.1 - Reply to a comment
#    Given I follow a club or team
#    And The club or team makes a blog post
#    When There is a comment on the blog post
#    Then I can reply to the comment

