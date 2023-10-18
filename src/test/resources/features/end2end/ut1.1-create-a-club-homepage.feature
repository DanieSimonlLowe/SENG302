#Feature: UT1.1 - Create a club homepage
#  Background:
#    Given I am logged in
#
#  Scenario: AC1 - Removing a post
#    Given I am on a club's homepage for a club that I manage or coach
#    And The homepage has at least one post
#    When I select a post
#    And I choose to delete the post
#    Then the post should be removed from the homepage
#
#  Scenario: AC2 - Adding a post form
#    Given I am on a club's homepage for a club that I manage or coach
#    When I press the add post button
#    Then a form to add a post to the homepage is shown
#
#  Scenario: AC3 - Entering valid post values
#    Given I am on the add post form
#    When I enter valid values for title, description and optionally attachment
#    And I submit the post
#    Then the post is added to the club's homepage
#
#  Scenario: AC4 - Title input validation
#    Given I am on the add post form
#    When I enter a title longer than 50 characters
#    Then an error message tells me that the title is longer than 50 characters
#
#  Scenario: AC5 - Description input validation
#    Given I am on the add post form
#    When I enter a description longer than 200 characters
#    Then an error message tells me that the description is longer than 200 characters
#
#  Scenario: AC6 - Attachment format validation
#    Given I am on the add post form
#    When I upload an attachment that is not either a png, jpg, svg, mp4, webm, or ogg
#    Then An error message tells me that the file is not of acceptable format
#
#  Scenario: AC7 - Attachment size validation
#    Given I am on the add post form
#    When I submit a valid attachment with a size more than 25MB
#    Then An error message tells me the file is too big