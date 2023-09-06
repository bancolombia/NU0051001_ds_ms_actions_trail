Feature: I as a user want to download the detail of a specific action.

  Background:
    * url api.baseUrl
    * def actionDetailPath = '/detailed-report/'
    * def actionId = '63b5f3f3d3afdd7121d701b9'
    * def routingKeyName = 'business.action-trail.report.dbb.distributionMicroservice.actionDetailedReportDone'
    * def routingKeyNameOnError = 'business.action-trail.report.dbb.distributionMicroservice.actionDetailedReportRejected'
    * def exchangeName = 'domainEvents'
    * def eventConsumer = call read('classpath:karate/management-event-factory.js') secrets.rabbitMQ
    * def handler = function(msg){ karate.signal(msg) }

  @DownloadActionDetailSuccessfulCase
  Scenario Outline: Check service response is ok and content type, content disposition, size and event are correct: Successful case
    Given path actionDetailPath + actionIdAlternate
    And headers read("classpath:jsonbase/general_headers.json")
    * eventConsumer.listen(exchangeName, routingKeyName, karate.toJava(handler))
    When method get
    And header 'Accept' = 'application/pdf'
    Then status 201
    * listen 7000
    * print '### command received:', listenResult
    * json commandPayload = listenResult
    And match commandPayload.name == routingKeyName
    And match commandPayload.data.type == routingKeyName
    And match commandPayload.data.data.meta.transactionCodeDesc == 'OK'
    And match commandPayload.data.data.meta.documentType == '<idType>'
    And match commandPayload.data.data.meta.documentNumber == '<idNumber>'
    And match commandPayload.data.data.request.transactionId == '<actionIdAlternate>'
    And match commandPayload.data.data.request.format == 'pdf'
    And match commandPayload.data.data.response.state == '<responseState>'
    * def size = responseHeaders['Content-Length'];
    And match size != 0
    And match responseHeaders['Content-Length'] != 0
    And match responseHeaders['Content-Type'] == ["application/pdf"]
    And match responseHeaders['content-disposition'] == ['attachment; filename=detalleDeActividad.pdf']

    Examples:
      | actionIdAlternate        | idType | idNumber    | responseState                          |
      | 63b5f3f3d3afdd7121d701b9 | CC     | 00000009043 | Detailed Report Generated Successfully |

  @DownloadActionDetailWithWrongDataAlternateCase
  Scenario Outline: Check if the download is not correct with wrong data in idType and idNumber: Alternate Case
    Given path actionDetailPath + actionIdAlternate
    And headers read("classpath:jsonbase/general_headers.json")
    * eventConsumer.listen(exchangeName, routingKeyNameOnError, karate.toJava(handler))
    When method get
    Then status 404
    And match responseHeaders['Content-Type'] == ["application/json"]
    * listen 7000
    * print '### command received:', listenResult
    * json commandPayload = listenResult
    And match commandPayload.name == routingKeyNameOnError
    And match commandPayload.data.type == routingKeyNameOnError
    And match commandPayload.data.data.meta.transactionCodeDesc == 'Not Found'
    And match commandPayload.data.data.meta.transactionCode == '404'
    And match commandPayload.data.data.meta.documentType == "#(idType)"
    And match commandPayload.data.data.meta.documentNumber == "#(idNumber)"
    And match commandPayload.data.data.request.transactionId == '<actionIdAlternate>'
    And match commandPayload.data.data.request.format == 'pdf'
    And match commandPayload.data.data.response.state.reason == '<reason>'
    And match commandPayload.data.data.response.state.domain == "/detailed-report/<actionIdAlternate>"
    And match commandPayload.data.data.response.state.code == '<errorCode>'
    And match commandPayload.data.data.response.state.message == '<errorMessage>'
    And match response.errors.[0] == {"reason":"<reason>","domain":"/detailed-report/<actionIdAlternate>","code":"<errorCode>","message":"<errorMessage>"}

    Examples:
      | actionIdAlternate        | idType  | idNumber      | reason                                       |  errorCode | errorMessage                                |
      | 63e26b3c8c2053c98a422f24 | B       | 0000000009044 | Could not find any action with the criteria  |  ATB0007   | Could not find any action with the criteria |
      | 63e26b3c8c2053c98a422f24 | CC      | 0             | Could not find any action with the criteria  |  ATB0007   | Could not find any action with the criteria |
      | 63e26b3c8c2053c98a422f24 | D       | 0             | Could not find any action with the criteria  |  ATB0007   | Could not find any action with the criteria |
      | 63e26b3c8c2053c98a422f24 | --      | --            | Could not find any action with the criteria  |  ATB0007   | Could not find any action with the criteria |
      | 63e26b3c8c2053c98a422f24 | #       | !             | Could not find any action with the criteria  |  ATB0007   | Could not find any action with the criteria |

  @DownloadActionDetailWithNonExistentUserAlternateCase
  Scenario Outline: Check if the search is empty with nonexistent user : Alternate Case
    Given path actionDetailPath + actionIdAlternate
    And headers read("classpath:jsonbase/general_headers.json")
    * eventConsumer.listen(exchangeName, routingKeyNameOnError, karate.toJava(handler))
    When method get
    Then status 404
    And match responseHeaders['Content-Type'] == ["application/json"]
    * listen 7000
    * print '### command received:', listenResult
    * json commandPayload = listenResult
    And match commandPayload.name == routingKeyNameOnError
    And match commandPayload.data.type == routingKeyNameOnError
    And match commandPayload.data.data.meta.transactionCodeDesc == 'Not Found'
    And match commandPayload.data.data.meta.transactionCode == '404'
    And match commandPayload.data.data.meta.documentType == "#(idType)"
    And match commandPayload.data.data.meta.documentNumber == "#(idNumber)"
    And match commandPayload.data.data.request.transactionId == '<actionIdAlternate>'
    And match commandPayload.data.data.request.format == 'pdf'
    And match commandPayload.data.data.response.state.reason == '<reason>'
    And match commandPayload.data.data.response.state.domain == "/detailed-report/<actionIdAlternate>"
    And match commandPayload.data.data.response.state.code == '<errorCode>'
    And match commandPayload.data.data.response.state.message == '<errorMessage>'
    And match response.errors.[0] == {"reason":"<reason>","domain":"/detailed-report/<actionIdAlternate>","code":"<errorCode>","message":"<errorMessage>"}

    Examples:
      | actionIdAlternate        | idType  | idNumber      | reason                                       |  errorCode | errorMessage                                |
      | 63e26b3c8c2053c98a422f24 | CC      | 9595959595959 | Could not find any action with the criteria  |  ATB0007   | Could not find any action with the criteria |

  @DownloadActionDetailWithoutHeadersAlternateCase
  Scenario Outline: Check service response is not ok without mandatory headers: Alternate Case
    Given headers read("classpath:jsonbase/dynamic_headers.json")
    And path actionDetailPath + actionId
    When method get
    Then status 500
    And match responseHeaders['Content-Type'] == ["application/json"]
    And match response.errors.[0] == {"reason":"<reason>","domain":"/detailed-report/63b5f3f3d3afdd7121d701b9","code":"<code>","message":"<reason>"}

    Examples:
      | idMessage                            | idSession | idRequestTime | idChannel | idAppVer | idDevice      | ip        | platformType |  idType | idNumber    | code    | reason                                                   |
      |                                      | 448560    | 1669940362    | D2B       | 2.0      | asus-rog-G752 | 127.0.0.1 | web          |  CC     | 00000009043 | ATB0008 | The message-id header cannot be null or empty            |
      | 8f86702e-f66b-4f76-9d2d-6f7b0650734d |           | 1669940362    | D2B       | 2.0      | asus-rog-G752 | 127.0.0.1 | web          |  CC     | 00000009043 | ATB0009 | The session-tracker header cannot be null or empty       |
      | 8f86702e-f66b-4f76-9d2d-6f7b0650734d | 448560    | 1669940362    |           | 2.0      | asus-rog-G752 | 127.0.0.1 | web          |  CC     | 00000009043 | ATB0011 | The channel header cannot be null or empty               |
      | 8f86702e-f66b-4f76-9d2d-6f7b0650734d | 448560    |               | D2B       | 2.0      | asus-rog-G752 | 127.0.0.1 | web          |  CC     | 00000009043 | ATB0010 | The request-timestamp header cannot be null or empty     |
      | 8f86702e-f66b-4f76-9d2d-6f7b0650734d | 448560    | 1669940362    | D2B       | 2.0      |               | 127.0.0.1 | mobile       |  CC     | 00000009043 | ATB0014 | The device-id header cannot be null or empty             |
      | 8f86702e-f66b-4f76-9d2d-6f7b0650734d | 448560    | 1669940362    | D2B       |          | asus-rog-G752 | 127.0.0.1 | mobile       |  CC     | 00000009043 | ATB0013 | The app-version header cannot be null or empty           |
      | 8f86702e-f66b-4f76-9d2d-6f7b0650734d | 448560    | 1669940362    | D2B       | 2.0      | asus-rog-G752 |           | mobile       |  CC     | 00000009043 | ATB0015 | The ip header cannot be null or empty                    |
      | 8f86702e-f66b-4f76-9d2d-6f7b0650734d | 448560    | 1669940362    | D2B       | 2.0      | asus-rog-G752 | 127.0.0.1 | mobile       |         | 00000009043 | ATB0017 | The identification-type header cannot be null or empty   |
      | 8f86702e-f66b-4f76-9d2d-6f7b0650734d | 448560    | 1669940362    | D2B       | 2.0      | asus-rog-G752 | 127.0.0.1 |              |  CC     | 00000009043 | ATB0023 | The platform-type header cannot be null or empty         |

  @DownloadActionDetailWithoutActionIdAlternateCase
  Scenario: Check if the download is not correct without the action id: Alternate Case
    Given path actionDetailPath
    And headers read("classpath:jsonbase/general_headers.json")
    * def idType = 'CC'
    * def idNumber = '00000009043'
    When method get
    Then status 500
    And match response.errors.[0] == {"reason":"Unexpected error","domain":"/detailed-report","code":"ATT0001","message":"Unexpected error"}

  @DownloadActionDetailWithNotExistActionIdAlternateCase
  Scenario Outline: Check if the download is not correct with nonexistent action id: Alternate Case
    Given path actionDetailPath + "<actionIdAlternate>"
    * def idType = 'CC'
    * def idNumber = '00000009043'
    And headers read("classpath:jsonbase/general_headers.json")
    * eventConsumer.listen(exchangeName, routingKeyNameOnError, karate.toJava(handler))
    When method get
    Then status 404
    * listen 7000
    * print '### command received:', listenResult
    * json commandPayload = listenResult
    And match commandPayload.name == routingKeyNameOnError
    And match commandPayload.data.type == routingKeyNameOnError
    And match commandPayload.data.data.meta.transactionCodeDesc == 'Not Found'
    And match commandPayload.data.data.meta.transactionCode == '404'
    And match commandPayload.data.data.meta.documentType == "#(idType)"
    And match commandPayload.data.data.meta.documentNumber == "#(idNumber)"
    And match commandPayload.data.data.request.transactionId == '<actionIdAlternate>'
    And match commandPayload.data.data.request.format == 'pdf'
    And match commandPayload.data.data.response.state.reason == '<reason>'
    And match commandPayload.data.data.response.state.domain == "/detailed-report/<actionIdAlternate>"
    And match commandPayload.data.data.response.state.code == '<errorCode>'
    And match commandPayload.data.data.response.state.message == '<errorMessage>'
    And match response.errors.[0] == {"reason":"<reason>","domain":"/detailed-report/<actionIdAlternate>","code":"<errorCode>","message":"<errorMessage>"}

    Examples:
      | actionIdAlternate                     | reason                                       | errorCode | errorMessage                                |
      | a                                     | Could not find any action with the criteria  | ATB0007   | Could not find any action with the criteria |