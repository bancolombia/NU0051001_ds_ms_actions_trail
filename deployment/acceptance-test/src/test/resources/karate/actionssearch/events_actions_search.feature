Feature: I as a user want to verify the correct emission of the event in action search

  Background:
    * url api.baseUrl
    * def actionSearchPath = '/actions-search'
    * def delay = eventProperties.eventDelay
    * def routingKeyNamesSuccess = 'business.action-trail.action.dbb.distributionMicroservice.actionSearchDone'
    * def exchangeName = 'domainEvents'
    * def eventConsumer = call read('classpath:karate/management-event-factory.js') secrets.rabbitMQ
    * def handler = function(msg){ karate.signal(msg) }

  @ConsultActionsSuccessfulCase
  Scenario Outline:  Consult actions made by customers: Successful case
    * def template = read("classpath:jsonbase/actionssearch/create_filter.json")
    * def dateRangeFilter = get template.searchCriteria.dateRange
    * def jsonRequest = {}
    * set jsonRequest.searchCriteria.dateRange = dateRangeFilter
    Given path actionSearchPath
    And headers read("classpath:jsonbase/general_headers.json")
    And request jsonRequest
    * eventConsumer.listen(exchangeName, routingKeyNamesSuccess, karate.toJava(handler))
    When method Post
    Then status 200
    * listen delay
    * print '### command received:', listenResult
    * json commandPayload = listenResult
    And match commandPayload.name == routingKeyNamesSuccess
    And match commandPayload.data.type == routingKeyNamesSuccess
    And match commandPayload.data.data.meta.transactionCodeDesc == 'OK'
    And match commandPayload.data.data.meta.documentType == '<idType>'
    And match commandPayload.data.data.meta.documentNumber == '<idNumber>'

    Examples:
      | idType | idNumber   | startDate  | endDate    |
      | CC     | 1096647533 | 2023-01-01 | 2023-12-08 |
