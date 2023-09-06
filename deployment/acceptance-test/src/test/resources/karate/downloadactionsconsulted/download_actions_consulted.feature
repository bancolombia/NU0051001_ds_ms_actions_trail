Feature:  I as a user want to download the consultation made in order to have this document available
  through the secure mailbox.

  Background:
    * url api.baseUrl
    * path '/report'
    * def routingKeyName = 'business.action-trail.report.dbb.distributionMicroservice.actionDownloadDone'
    * def routingKeyNameOnError = 'business.action-trail.report.dbb.distributionMicroservice.actionDownloadRejected'
    * def exchangeName = 'domainEvents'
    * def eventConsumer = call read('classpath:karate/management-event-factory.js') secrets.rabbitMQ
    * def handler = function(msg){ karate.signal(msg) }
    * def s3Client = call read('../s3-client-factory.js')
    * def secureMailboxTransactionCode = parameters.secureMailboxTransactionCode

  @DownloadActionsConsultedUploadPdfInS3SuccessfulCase
  Scenario Outline: Check service response is ok with only date range and xlsx file upload in bucket s3 is ok : Successful Case
    Given headers read("classpath:jsonbase/general_headers.json")
    And request {"searchCriteria": {"dateRange": {"startDate": "<startDate>","endDate": "<endDate>"}}}
    And header format = "xlsx"
    * def channel = "dbb"
    And header channel = channel
    * eventConsumer.listen(exchangeName, routingKeyName, karate.toJava(handler))
    When method post
    Then status 202
    * listen 20000
    * print '### command received:', listenResult
    * json commandPayload = listenResult
    And match commandPayload.name == routingKeyName
    And match commandPayload.data.type == routingKeyName
    And match commandPayload.data.data.meta.transactionCode == '200'
    And match commandPayload.data.data.meta.transactionCodeDesc == 'OK'
    And match commandPayload.data.data.meta.documentType == '<idType>'
    And match commandPayload.data.data.meta.documentNumber == '<idNumber>'
    And match commandPayload.data.data.request.format == 'xlsx'
    And match commandPayload.data.data.request.searchCriteria.date == "{fromValue=<startDate>, toValue=<endDate>}"
    And match commandPayload.data.data.response.state == '<finalState>'
    * def filename = commandPayload.data.data.response.filename
    * def fileExist = s3Client.checkFileExistInS3(filename)
    And match fileExist == true
    * def fileNameSnippet  = channel + '_' + secureMailboxTransactionCode + '_<idType>_<idNumber>'
    And match filename == ('#regex' + fileNameSnippet +'_[^_]+$')

    Examples:
      | idType | idNumber    | startDate  | endDate    | finalState                    |
      | CC     | 00000009043 | 2019-09-25 | 2019-09-25 | Report Generated Successfully |

  @DownloadActionsConsultedWhenNotDataFoundAlternateCase
  Scenario Outline: Check that the service response is not successful when no data is found in the date range: Alternate case
    Given headers read("classpath:jsonbase/general_headers.json")
    And request {"searchCriteria": {"dateRange": {"startDate": "<startDate>","endDate": "<endDate>"}}}
    And header format = "xlsx"
    * eventConsumer.listen(exchangeName, routingKeyNameOnError, karate.toJava(handler))
    When method post
    Then status 404
    * listen 7000
    * print '### command received:', listenResult
    * json commandPayload = listenResult
    And match commandPayload.name == routingKeyNameOnError
    And match commandPayload.data.type == routingKeyNameOnError
    And match commandPayload.data.data.meta.transactionCode == '404'
    And match commandPayload.data.data.meta.transactionCodeDesc == 'Not Found'
    And match commandPayload.data.data.meta.documentType == '<idType>'
    And match commandPayload.data.data.meta.documentNumber == '<idNumber>'
    And match commandPayload.data.data.meta.transactionState == 'Fallida'
    And match commandPayload.data.data.request.format == 'xlsx'
    And match commandPayload.data.data.request.searchCriteria.date == "{fromValue=<startDate>, toValue=<endDate>}"
    And match commandPayload.data.data.response.state == {"reason":"<reason>","domain":"/report","code":"<code>","message":"<reason>"}

    Examples:
      | idType | idNumber    | startDate  | endDate    | code    | reason                                                 |
      | CC     | 00000009043 | 2009-09-25 | 2009-09-25 | ATB0007 | Could not find any action with the criteria            |

  @DownloadActionsConsultedWithoutHeaderFormatAlternateCase
  Scenario: Check service response is not ok without mandatory header format: Alternate Case
    Given headers read("classpath:jsonbase/general_headers.json")
    And request {"searchCriteria": {"dateRange": {"startDate": "2019-09-25","endDate": "2019-09-25"}}}
    When method post
    Then status 500
    And match response.errors.[0] == {"reason":"header format must be required","domain":"/report","code":"ATT0013","message":"header format must be required"}

  @DownloadActionsConsultedWithoutHeadersAlternateCase
  Scenario Outline: Check service response is not ok without mandatory headers: Alternate Case
    Given headers read("classpath:jsonbase/dynamic_headers.json")
    And header format = "pdf"
    And request {"searchCriteria": {"dateRange": {"startDate": "2019-09-25","endDate": "2019-09-25"}}}
    When method post
    Then status 500
    And match responseHeaders['Content-Type'] == ["application/json"]
    And match response.errors.[0] == {"reason":"<reason>","domain":"/report","code":"<code>","message":"<reason>"}

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

  @DownloadActionsConsultedWithNoDataFoundAlternateCase
  Scenario Outline: Check that the service generates an error when no data is found with the identification data: Alternate Case
    Given headers read("classpath:jsonbase/general_headers.json")
    And request {"searchCriteria": {"dateRange": {"startDate": "<startDate>","endDate": "<endDate>"}}}
    And header format = "xlsx"
    * eventConsumer.listen(exchangeName, routingKeyNameOnError, karate.toJava(handler))
    When method post
    Then status 404
    And match responseHeaders['Content-Type'] == ["application/json"]
    * listen 7000
    * print '### command received:', listenResult
    * json commandPayload = listenResult
    And match commandPayload.name == routingKeyNameOnError
    And match commandPayload.data.type == routingKeyNameOnError
    And match commandPayload.data.data.meta.transactionCode == '404'
    And match commandPayload.data.data.meta.transactionCodeDesc == 'Not Found'
    And match commandPayload.data.data.meta.documentType == '<idType>'
    And match commandPayload.data.data.meta.documentNumber == '<idNumber>'
    And match commandPayload.data.data.meta.transactionState == 'Fallida'
    And match commandPayload.data.data.request.format == 'xlsx'
    And match commandPayload.data.data.request.searchCriteria.date == "{fromValue=<startDate>, toValue=<endDate>}"
    And match commandPayload.data.data.response.state == {"reason":"<reason>","domain":"/report","code":"<code>","message":"<reason>"}
    And match response.errors.[0] == {"reason":"<reason>","domain":"/report","code":"<code>","message":"<reason>"}

    Examples:
      | idType | idNumber      | startDate  | endDate    | code    | reason                                                 |
      | CC     | 000000090000  | 2019-09-25 | 2019-09-25 | ATB0007 | Could not find any action with the criteria            |

  @DownloadActionsConsultedWithNoDataFoundAlternateCase02
  Scenario Outline: Check service generate error when no data found: Alternate Case
    Given headers read("classpath:jsonbase/general_headers.json")
    And request read("classpath:jsonbase/downloadactionsconsulted/request_download_actions_consulted.json")
    And header format = "xlsx"
    * eventConsumer.listen(exchangeName, routingKeyNameOnError, karate.toJava(handler))
    When method post
    Then status 404
    And match responseHeaders['Content-Type'] == ["application/json"]
    * listen 7000
    * print '### command received:', listenResult
    * json commandPayload = listenResult
    And match commandPayload.name == routingKeyNameOnError
    And match commandPayload.data.type == routingKeyNameOnError
    And match commandPayload.data.data.meta.transactionCode == '404'
    And match commandPayload.data.data.meta.transactionCodeDesc == 'Not Found'
    And match commandPayload.data.data.meta.documentType == '<idType>'
    And match commandPayload.data.data.meta.documentNumber == '<idNumber>'
    And match commandPayload.data.data.meta.transactionState == 'Fallida'
    And match commandPayload.data.data.request.format == 'xlsx'
    And match commandPayload.data.data.request.searchCriteria.date == "{fromValue=<startDate>, toValue=<endDate>}"
    And match commandPayload.data.data.response.state == {"reason":"<reason>","domain":"/report","code":"<code>","message":"<reason>"}
    And match response.errors.[0] == {"reason":"<reason>","domain":"/report","code":"<code>","message":"<reason>"}

    Examples:
      | idType | idNumber     | startDate  | endDate    | transactionState | transactionType | transactionName | bankEntity  | productType | productNumber | identificationType | identificationNumber | name          | code    | reason                                                 |
      | CC     | 00000009043  | 2019-09-25 | 2019-09-25 | Ejecutado        | Monetaria       | Pago de nómina  | Bancolombia | Depositos   | 89465980      | TIPODOC_FS001      | 1071815850           | Pedro Perez   | ATB0007 | Could not find any action with the criteria            |

    #TODO ORGANIZAR
  @DownloadActionsConsultedWithAllParametersSuccessfulCase
  Scenario Outline: Check if service generate download successfully with all parameters of search: Successful Case
    Given headers read("classpath:jsonbase/general_headers.json")
    And request read("classpath:jsonbase/downloadactionsconsulted/request_download_actions_consulted.json")
    And header format = "xlsx"
    * eventConsumer.listen(exchangeName, routingKeyName, karate.toJava(handler))
    When method post
    Then status 202
    * listen 7000
    * print '### command received:', listenResult
    * json commandPayload = listenResult
    And match commandPayload.name == routingKeyName
    And match commandPayload.data.type == routingKeyName
    And match commandPayload.data.data.meta.transactionCode == '200'
    And match commandPayload.data.data.meta.transactionCodeDesc == 'OK'
    And match commandPayload.data.data.meta.documentType == '<idType>'
    And match commandPayload.data.data.meta.documentNumber == '<idNumber>'
    And match commandPayload.data.data.request.format == 'xlsx'
    And match commandPayload.data.data.request.searchCriteria.date == "{fromValue=<startDate>, toValue=<endDate>}"
    And match commandPayload.data.data.response.state == '<finalState>'

    Examples:
      | startDate  | endDate    | transactionState | transactionType | transactionName | bankEntity           | productType      | productNumber   | identificationType | identificationNumber | name            | idType | idNumber      | finalState                    |
      | 2019-09-25 | 2019-09-25 | No_monetaria     |  Monetaria      | Momento1        | 12345678901234567890 | CUENTA_DE_AHORRO | 123456789123456 | CC                 | 12345678901234567890 | Juan_rodriguez  | CC     | 00000009044   | Report Generated Successfully |

  @DownloadActionsConsultedWithParametersSuccessfulCase
  Scenario Outline: Check if service generate download successfully with different parameters of search: Successful Case
    Given headers read("classpath:jsonbase/general_headers.json")
    * def jsonRequest = read("classpath:jsonbase/downloadactionsconsulted/request_download_actions_consulted.json")
    And remove jsonRequest.<keyToRemove>
    And request jsonRequest
    And header format = "xlsx"
    * eventConsumer.listen(exchangeName, routingKeyName, karate.toJava(handler))
    When method post
    Then status 202
    * listen 7000
    * print '### command received:', listenResult
    * json commandPayload = listenResult
    And match commandPayload.name == routingKeyName
    And match commandPayload.data.type == routingKeyName
    And match commandPayload.data.data.meta.transactionCode == '200'
    And match commandPayload.data.data.meta.transactionCodeDesc == 'OK'
    And match commandPayload.data.data.meta.documentType == '<idType>'
    And match commandPayload.data.data.meta.documentNumber == '<idNumber>'
    And match commandPayload.data.data.request.format == 'xlsx'
    And match commandPayload.data.data.request.searchCriteria.date == "{fromValue=<startDate>, toValue=<endDate>}"
    And match commandPayload.data.data.response.state == '<finalState>'

    Examples:
      | keyToRemove                               | startDate  | endDate    | transactionState | transactionType | transactionName | bankEntity           | productType      | productNumber   | identificationType | identificationNumber | name            | idType | idNumber      | finalState                    |
      | searchCriteria.state                      | 2019-09-25 | 2019-09-25 | No_monetaria     |  Monetaria      | Momento1        | 12345678901234567890 | CUENTA_DE_AHORRO | 123456789123456 | CC                 | 12345678901234567890 | Juan_rodriguez  | CC     | 00000009044   | Report Generated Successfully |
      | searchCriteria.type                       | 2019-09-25 | 2019-09-25 | No_monetaria     |  Monetaria      | Momento1        | 12345678901234567890 | CUENTA_DE_AHORRO | 123456789123456 | CC                 | 12345678901234567890 | Juan_rodriguez  | CC     | 00000009044   | Report Generated Successfully |
      | searchCriteria.name                       | 2019-09-25 | 2019-09-25 | No_monetaria     |  Monetaria      | Momento1        | 12345678901234567890 | CUENTA_DE_AHORRO | 123456789123456 | CC                 | 12345678901234567890 | Juan_rodriguez  | CC     | 00000009044   | Report Generated Successfully |
      | searchCriteria.bankEntity                 | 2019-09-25 | 2019-09-25 | No_monetaria     |  Monetaria      | Momento1        | 12345678901234567890 | CUENTA_DE_AHORRO | 123456789123456 | CC                 | 12345678901234567890 | Juan_rodriguez  | CC     | 00000009044   | Report Generated Successfully |
      | searchCriteria.product                    | 2019-09-25 | 2019-09-25 | No_monetaria     |  Monetaria      | Momento1        | 12345678901234567890 | CUENTA_DE_AHORRO | 123456789123456 | CC                 | 12345678901234567890 | Juan_rodriguez  | CC     | 00000009044   | Report Generated Successfully |
      | searchCriteria.product.type               | 2019-09-25 | 2019-09-25 | No_monetaria     |  Monetaria      | Momento1        | 12345678901234567890 | CUENTA_DE_AHORRO | 123456789123456 | CC                 | 12345678901234567890 | Juan_rodriguez  | CC     | 00000009044   | Report Generated Successfully |
      | searchCriteria.product.number             | 2019-09-25 | 2019-09-25 | No_monetaria     |  Monetaria      | Momento1        | 12345678901234567890 | CUENTA_DE_AHORRO | 123456789123456 | CC                 | 12345678901234567890 | Juan_rodriguez  | CC     | 00000009044   | Report Generated Successfully |
      | user                                      | 2019-09-25 | 2019-09-25 | No_monetaria     |  Monetaria      | Momento1        | 12345678901234567890 | CUENTA_DE_AHORRO | 123456789123456 | CC                 | 12345678901234567890 | Juan_rodriguez  | CC     | 00000009044   | Report Generated Successfully |
      | searchCriteria.user.identification        | 2019-09-25 | 2019-09-25 | No_monetaria     |  Monetaria      | Momento1        | 12345678901234567890 | CUENTA_DE_AHORRO | 123456789123456 | CC                 | 12345678901234567890 | Juan_rodriguez  | CC     | 00000009044   | Report Generated Successfully |
      | searchCriteria.user.identification.type   | 2019-09-25 | 2019-09-25 | No_monetaria     |  Monetaria      | Momento1        | 12345678901234567890 | CUENTA_DE_AHORRO | 123456789123456 | CC                 | 12345678901234567890 | Juan_rodriguez  | CC     | 00000009044   | Report Generated Successfully |
      | searchCriteria.user.identification.number | 2019-09-25 | 2019-09-25 | No_monetaria     |  Monetaria      | Momento1        | 12345678901234567890 | CUENTA_DE_AHORRO | 123456789123456 | CC                 | 12345678901234567890 | Juan_rodriguez  | CC     | 00000009044   | Report Generated Successfully |
      | name                                      | 2019-09-25 | 2019-09-25 | No_monetaria     |  Monetaria      | Momento1        | 12345678901234567890 | CUENTA_DE_AHORRO | 123456789123456 | CC                 | 12345678901234567890 | Juan_rodriguez  | CC     | 00000009044   | Report Generated Successfully |

  @DownloadActionsConsultedWithEachParameterXlsxFileSuccessfulCase
  Scenario Outline: Check if service generate download successfully with each parameters: Successful Case
    Given headers read("classpath:jsonbase/general_headers.json")
    * def jsonRequest = {"searchCriteria": {"dateRange": {"startDate": "<startDate>","endDate": "<endDate>"}}}
    *  jsonRequest.searchCriteria.<keyToAdd> = <keyValue>
    And request jsonRequest
    And header format = "xlsx"
    * eventConsumer.listen(exchangeName, routingKeyName, karate.toJava(handler))
    When method post
    Then status 202
    * listen 7000
    * print '### command received:', listenResult
    * json commandPayload = listenResult
    And match commandPayload.name == routingKeyName
    And match commandPayload.data.type == routingKeyName
    And match commandPayload.data.data.meta.transactionCode == '200'
    And match commandPayload.data.data.meta.transactionCodeDesc == 'OK'
    And match commandPayload.data.data.meta.documentType == '<idType>'
    And match commandPayload.data.data.meta.documentNumber == '<idNumber>'
    And match commandPayload.data.data.request.format == 'xlsx'
    And match commandPayload.data.data.request.searchCriteria.date == "{fromValue=<startDate>, toValue=<endDate>}"
    And match commandPayload.data.data.response.state == '<finalState>'

    Examples:
      | keyToAdd   | keyValue                                                                                | startDate  | endDate    | idType | idNumber      | finalState                    |
      | state      | "No_monetaria"                                                                          | 2019-09-25 | 2019-09-25 | CC     | 00000009044   | Report Generated Successfully |
      | type       | "Monetaria"                                                                             | 2019-09-25 | 2019-09-25 | CC     | 00000009044   | Report Generated Successfully |
      | name       | "Momento1"                                                                              | 2019-09-25 | 2019-09-25 | CC     | 00000009044   | Report Generated Successfully |
      | bankEntity | "12345678901234567890"                                                                  | 2019-09-25 | 2019-09-25 | CC     | 00000009044   | Report Generated Successfully |
      | product    | {type: "CUENTA_DE_AHORRO"}                                                              | 2019-09-25 | 2019-09-25 | CC     | 00000009044   | Report Generated Successfully |
      | product    | {type: "CUENTA_DE_AHORRO", number: "123456789123456"}                                   | 2019-09-25 | 2019-09-25 | CC     | 00000009044   | Report Generated Successfully |
      | user       | {identification: {type: "CC"}}                                                          | 2019-09-25 | 2019-09-25 | CC     | 00000009044   | Report Generated Successfully |
      | user       | {identification: {type: "CC", number: "12345678901234567890"}}                          | 2019-09-25 | 2019-09-25 | CC     | 00000009044   | Report Generated Successfully |
      | user       | {identification: {type: "CC", number: "12345678901234567890"}, name: "Juan_Barrientos"} | 2019-09-25 | 2019-09-25 | CC     | 00000009043   | Report Generated Successfully |