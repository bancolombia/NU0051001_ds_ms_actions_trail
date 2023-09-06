Feature: I want to query the actions carried out by a client in a specific channel

  Background:
    * url api.baseUrl
    * def actionSearchPath = '/actions-search'

  @QueryActionsSuccessfulCase
  Scenario Outline: Consult history of actions by a user: Successful case
    Given path actionSearchPath + '/' + transactionTracker
    And headers read("classpath:jsonbase/general_headers.json")
    When method Get
    Then status 200
    And match response.data[0].transactionTracker == transactionTracker
    And match response.data[0].documentType == idType
    And match response.data[0].documentNumber == idNumber

    Examples:
      | transactionTracker | idType | idNumber    |
      | 8070               | CC     | 10172566988 |

  @QueryActionsTransactionNotExistAlternateCase
  Scenario Outline: Consult history of actions with transaction not exist: Alternate case
    Given path actionSearchPath + '/' + transactionTracker
    And headers read("classpath:jsonbase/general_headers.json")
    When method Get
    Then status 200
    And match response.data[0] == '#notpresent'

    Examples:
      | transactionTracker | idType | idNumber    |
      | 2011               | CC     | 10172566988 |

  @QueryActionsDocumentNotExistAlternateCase
  Scenario Outline: Consult history of actions with invalid id type and id number: Alternate case
    Given path actionSearchPath + '/' + transactionTracker
    And headers read("classpath:jsonbase/general_headers.json")
    When method Get
    Then status 200
    And match response.data[0] == '#notpresent'

    Examples:
      | transactionTracker | idType | idNumber    |
      | 8070               | TI     | 10172566988 |
      | 8070               | CC     | 10172566000 |

  @QueryActionsIncompleteHeadersAlternateCase
  Scenario Outline: Consult history of actions with incomplete headers: Alternate case
    Given path actionSearchPath + '/' + transactionTracker
    * def headerRequest = read("classpath:jsonbase/general_headers.json")
    And remove headerRequest.<headerToRemove>
    And headers headerRequest
    When method Get
    Then status 500
    And match response.errors[0] == {"reason":"<errorMessage>","domain":"/actions-search/<transactionTracker>","code":<errorcode>,"message":"<errorMessage>"}

    Examples:
      | transactionTracker | idType | idNumber    | headerToRemove        | errorMessage                                             | errorcode |
      | 8070               | CC     | 10172566988 | ip                    | The ip header cannot be null or empty                    | ATB0015   |
      | 8070               | CC     | 10172566988 | message-id            | The message-id header cannot be null or empty            | ATB0008   |
      | 8070               | CC     | 10172566988 | session-tracker       | The session-tracker header cannot be null or empty       | ATB0009   |
      | 8070               | CC     | 10172566988 | request-timestamp     | The request-timestamp header cannot be null or empty     | ATB0010   |
      | 8070               | CC     | 10172566988 | channel               | The channel header cannot be null or empty               | ATB0011   |
      | 8070               | CC     | 10172566988 | app-version           | The app-version header cannot be null or empty           | ATB0013   |
      | 8070               | CC     | 10172566988 | device-id             | The device-id header cannot be null or empty             | ATB0014   |
      | 8070               | CC     | 10172566988 | identification-type   | The identification-type header cannot be null or empty   | ATB0017   |
      | 8070               | CC     | 10172566988 | identification-number | The identification-number header cannot be null or empty | ATB0016   |