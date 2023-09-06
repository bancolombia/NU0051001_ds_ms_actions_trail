Feature: I want to verify the correct generation of the document in download history of actions

  Background:
    * url api.baseUrl
    * def actionSearchReportPath = '/report'

  @QueryActionsSuccessfulCase
  Scenario Outline: Download history of actions by a user with transaction tracker: Successful case
    Given path actionSearchReportPath + '/' + transactionTracker
    And headers read("classpath:jsonbase/general_headers.json")
    And header format = '<format>'
    When method Get
    Then status 201
    And match response != null
    And match responseHeaders.content-disposition[0] == 'attachment; filename=historialDeActividad.<format>'
    And match responseHeaders.Content-Type[0] == '<responseContentType>'

    Examples:
      | transactionTracker | idType | idNumber    | format | responseContentType                                               |
      | 8070               | CC     | 10172566988 | pdf    | application/pdf                                                   |
      | 8070               | CC     | 10172566988 | xlsx   | application/vnd.openxmlformats-officedocument.spreadsheetml.sheet |

  @QueryActionsAlternateCase1
  Scenario Outline: Download history of actions by a user with no exist transaction tracker: Alternate case
    Given path actionSearchReportPath + '/' + transactionTracker
    And headers read("classpath:jsonbase/general_headers.json")
    And header format = '<format>'
    When method Get
    Then status 404
    And match response.errors[0] == {"reason":"Could not find any action with the criteria", "domain":"/report/<transactionTracker>", "code":"ATB0007", "message":"Could not find any action with the criteria"}

    Examples:
      | transactionTracker | idType | idNumber    | format |
      | 1000               | CC     | 10172566988 | pdf    |

  @QueryActionsAlternateCase2
  Scenario Outline: Download history of actions by a user without header format: Alternate case
    Given path actionSearchReportPath + '/' + transactionTracker
    And headers read("classpath:jsonbase/general_headers.json")
    When method Get
    Then status 500
    And match response.errors[0] == {"reason":"header format must be required", "domain":"/report/<transactionTracker>", "code":"ATT0013", "message":"header format must be required"}

    Examples:
      | transactionTracker | idType | idNumber    |
      | 8070               | CC     | 10172566988 |

  @QueryActionsAlternateCase3
  Scenario Outline: Download history of actions by a user with invalid header format: Alternate case
    Given path actionSearchReportPath + '/' + transactionTracker
    And headers read("classpath:jsonbase/general_headers.json")
    And header format = '<format>'
    When method Get
    Then status 500
    And match response.errors[0] == {"reason":"Invalid report format", "domain":"/report/<transactionTracker>", "code":"ATB0006", "message":"Invalid report format"}

    Examples:
      | transactionTracker | idType | idNumber    | format |
      | 8070               | CC     | 10172566988 | docx   |
      | 8070               | CC     | 10172566988 | json   |

  @QueryActionsAlternateCase4
  Scenario Outline: Download history of actions by a user with invalid document type and document number: Alternate case
    Given path actionSearchReportPath + '/' + transactionTracker
    And headers read("classpath:jsonbase/general_headers.json")
    And header format = '<format>'
    When method Get
    Then status 404
    And match response.errors[0] == {"reason":"Could not find any action with the criteria", "domain":"/report/8070", "code":"ATB0007", "message":"Could not find any action with the criteria"}

    Examples:
      | transactionTracker | idType | idNumber    | format |
      | 8070               | CC     | 10020030012 | pdf    |
      | 8070               | TI     | 10172566988 | xlsx   |

  @QueryActionsAlternateCase5
  Scenario Outline: Download history of actions by a user with null document type and document number: Alternate case
    Given path actionSearchReportPath + '/' + transactionTracker
    And headers read("classpath:jsonbase/general_headers.json")
    And header format = '<format>'
    When method Get
    Then status 500
    And match response.errors[0] == {"reason":"<errorMessage>", "domain":"/report/<transactionTracker>", "code":<code>, "message":"<errorMessage>"}

    Examples:
      | transactionTracker | idType | idNumber    | format | errorMessage                                             | code    |
      | 8070               | CC     |             | pdf    | The identification-number header cannot be null or empty | ATB0016 |
      | 8070               |        | 10172566988 | xlsx   | The identification-type header cannot be null or empty   | ATB0017 |

  @QueryActionsAlternateCase6
  Scenario Outline: Download history of actions by a user with incomplete headers: Alternate case
    Given path actionSearchReportPath + '/' + transactionTracker
    * def headerRequest = read("classpath:jsonbase/general_headers.json")
    And header format = '<format>'
    And remove headerRequest.<headerToRemove>
    And headers headerRequest
    When method Get
    Then status 500
    And match response.errors[0] == {"reason":"<errorMessage>", "domain":"/report/<transactionTracker>", "code":<errorCode>, "message":"<errorMessage>"}

    Examples:
      | transactionTracker | idType | idNumber    | headerToRemove        | errorMessage                                             | format | errorCode |
      | 8070               | CC     | 10172566988 | ip                    | The ip header cannot be null or empty                    | pdf    | ATB0015   |
      | 8070               | CC     | 10172566988 | message-id            | The message-id header cannot be null or empty            | xlsx   | ATB0008   |
      | 8070               | CC     | 10172566988 | session-tracker       | The session-tracker header cannot be null or empty       | pdf    | ATB0009   |
      | 8070               | CC     | 10172566988 | request-timestamp     | The request-timestamp header cannot be null or empty     | xlsx   | ATB0010   |
      | 8070               | CC     | 10172566988 | channel               | The channel header cannot be null or empty               | pdf    | ATB0011   |
      | 8070               | CC     | 10172566988 | app-version           | The app-version header cannot be null or empty           | xlsx   | ATB0013   |
      | 8070               | CC     | 10172566988 | device-id             | The device-id header cannot be null or empty             | pdf    | ATB0014   |
      | 8070               | CC     | 10172566988 | identification-type   | The identification-type header cannot be null or empty   | xlsx   | ATB0017   |
      | 8070               | CC     | 10172566988 | identification-number | The identification-number header cannot be null or empty | pdf    | ATB0016   |