Feature: MoneyTransfer

  Scenario: Successful transfer
    Given account1 has a balance of 50.52
    And account2 has a balance of 10.49
    When I request to transfer 1.11 from account1 to account2
    Then response status code is 200
    And response body is the transfer with id
    And account1 has new balance of 49.41
    And account2 has new balance of 11.60

  Scenario: Invalid origin account
    Given account1 does not exist
    And account2 has a balance of 10
    When I request to transfer 10 from account1 to account2
    Then response status code is 400
    And response message is "Invalid origin account."

  Scenario: Invalid destination account
    Given account1 has a balance of 10
    And account2 does not exist
    When I request to transfer 10 from account1 to account2
    Then response status code is 400
    And response message is "Invalid destination account."

  Scenario: Insufficient funds in origin account
    Given account1 has a balance of 5
    And account2 has a balance of 50
    When I request to transfer 10 from account1 to account2
    Then response status code is 400
    And response message is "Insufficient funds in origin account."


