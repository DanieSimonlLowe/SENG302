Feature: U5 - (User) Add a profile picture to user
  Background:
    Given I am logged in

  Scenario: AC1 - Given I am on my user profile page, when I hit the “edit profile picture” button, then a file picker is shown.
    Given I am on page "/profile"
    When I click my profile picture
    Then a file picker is shown

  Scenario Outline: AC4 - Given I choose a new profile picture, when I submit a valid file with a size of more than 10MB, then an error message tells me the file is too big.
    Given I am on page "/profile"
    When I choose a new profile picture <file>
    Then I am redirected to "/profile"
    Then an error message tells me the file is too big
    Examples:
    |     file    |
    | "testBig.png"       |
    | "testExtraBig.png"          |
    | "testGinormous.png"          |

  Scenario Outline: AC4 - Given I choose a new profile picture, when I submit a valid file with a size of less than 10MB, then no error message is sent.
    Given I am on page "/profile"
    When I choose a new profile picture <file>
    Then I am redirected to "/profile"
    Examples:
      |     file    |
      | "testJustRight.png"      |
      | "testSmall.png"          |
      | "testVerySmall.png"      |

  Scenario Outline: AC3 - Given I choose a new profile picture, when I submit a file that is not either a png, jpg or svg, then an error message tells me the file is not of an acceptable format.
    Given I am on page "/profile"
    When I choose a new profile picture <wrongFile>
    Then an error message tells me the file is not of an acceptable format
    Examples:
    |       wrongFile       |
    | "testWrongFormat.pdf" |
    | "testWrongFormat.xml" |
    | "testWrongFormat.zip" |