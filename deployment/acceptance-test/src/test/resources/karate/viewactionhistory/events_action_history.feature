Feature: I want to verify the correct emission of the event in view action history

  Background:
    * url api.baseUrl
    * def actionSearchPath = '/actions-search'
    * def actionSearchReportPath = '/report'
    * def delay = eventProperties.eventDelay
    * def exchangeName = 'domainEvents'
    * def eventConsumer = call read('classpath:karate/management-event-factory.js') secrets.rabbitMQ
    * def handler = function(msg){ karate.signal(msg) }

  @QueryActionsSuccessfulCase
  Scenario Outline: Consult history of actions by a user: Successful case
    * def routingKeyName = 'business.action-trail.action.dbb.distributionMicroservice.actionHistoryQueryDone'
    Given path actionSearchPath + '/' + transactionTracker
    And headers read("classpath:jsonbase/general_headers.json")
    * eventConsumer.listen(exchangeName, routingKeyName, karate.toJava(handler))
    When method Get
    Then status 200
    * listen delay
    * print '### command received:', listenResult
    * json commandPayload = listenResult
    And match commandPayload.name == routingKeyName
    And match commandPayload.data.type == routingKeyName
    And match commandPayload.data.data.meta.transactionCodeDesc == 'OK'
    And match commandPayload.data.data.meta.documentType == '<idType>'
    And match commandPayload.data.data.meta.documentNumber == '<idNumber>'
    And match commandPayload.data.data.request.searchCriteria.criteria.transactionTracker == '<transactionTracker>'

    Examples:
      | transactionTracker | idType | idNumber    |
      | 8070               | CC     | 10172566988 |

  @DownloadActionsSuccessfulCase
  Scenario Outline: Download history of actions by a user: Successful case
    * def routingKeyName = 'business.action-trail.report.dbb.distributionMicroservice.actionHistoryDownloadDone'
    Given path actionSearchReportPath + '/' + transactionId
    And headers read("classpath:jsonbase/general_headers.json")
    And header format = '<format>'
    * eventConsumer.listen(exchangeName, routingKeyName, karate.toJava(handler))
    When method Get
    Then status 201
    * listen delay
    * print '### command received:', listenResult
    * json commandPayload = listenResult
    And match commandPayload.name == routingKeyName
    And match commandPayload.data.type == routingKeyName
    And match commandPayload.data.data.meta.transactionCodeDesc == 'OK'
    And match commandPayload.data.data.meta.documentType == '<idType>'
    And match commandPayload.data.data.meta.documentNumber == '<idNumber>'
    And match commandPayload.data.data.request.transactionId == '<transactionId>'
    And match commandPayload.data.data.request.format == '<format>'

    Examples:
      | transactionId | idType | idNumber    | format |
      | 8070          | CC     | 10172566988 | pdf    |
      | 8070          | CC     | 10172566988 | xlsx   |