Feature: Notification Drawer: As Teka, I want to see my notifications for activities I involved in.
  Background:
    Given I am logged in


  Scenario: AC2 -  Given I am logged in, when I click on the notification drawer icon, a notification drawer is displayed
    When I click on the notification drawer
    Then I can see a notification drawer


