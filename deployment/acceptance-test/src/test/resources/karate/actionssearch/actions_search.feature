Feature: I as a user want to consult the actions that the customers are carrying out.

  Background:
    * url api.baseUrl
    * def actionsPath = '/actions-search'

  @ConsultActionsSuccessWithMandatoryCriteria
  Scenario Outline: Consult actions made by customers only with mandatory filters: Successful case
    * def template = read("classpath:jsonbase/actionssearch/create_filter.json")
    * def dateRangeFilter = get template.searchCriteria.dateRange
    * def jsonRequest = {}
    * set jsonRequest.searchCriteria.dateRange = dateRangeFilter
    Given path actionsPath
    And headers read("classpath:jsonbase/general_headers.json")
    And request jsonRequest
    When method Post
    Then status 200
    And match response.data[*] contains read("classpath:jsonbase/actionssearch/response_actions_search.json")

    Examples:
      | startDate  | endDate    | idType | idNumber   |
      | 2023-01-01 | 2023-12-08 | CC     | 1096647533 |
      | 2023-01-01 | 2023-12-08 | CC     | 44229952   |

  @ConsultActionsSuccessPaginated
  Scenario Outline: Consult actions made by customers only with mandatory filters and Paginated: Successful case
    * def paramsActionsSearch = {pageNumber:"<pageNumber>", pageSize:"<pageSize>"}
    * def template = read("classpath:jsonbase/actionssearch/create_filter.json")
    * def dateRangeFilter = get template.searchCriteria.dateRange
    * def jsonRequest = {}
    * set jsonRequest.searchCriteria.dateRange = dateRangeFilter
    * def paginatedResponse = read("classpath:jsonbase/actionssearch/paginated_actions_search.json")
    * def jsonResponse = paginatedResponse.links
    Given path actionsPath
    And params paramsActionsSearch
    And headers read("classpath:jsonbase/general_headers.json")
    And request jsonRequest
    When method Post
    Then status 200
    And match response.meta contains paginatedResponse.meta
    And match response.links contains jsonResponse
    And match response.data[*] contains read("classpath:jsonbase/actionssearch/response_actions_search.json")

    Examples:
      | startDate  | endDate    | idType | idNumber   | pageNumber | pageSize | prevPageItem             |
      | 2023-01-01 | 2023-12-08 | CC     | 1096647533 | 1          | 2        |                          |
      | 2023-01-01 | 2023-12-08 | CC     | 1096647533 | 2          | 2        | ?pageNumber=1&pageSize=2 |
      | 2023-01-01 | 2023-12-08 | CC     | 1096647533 | 4          | 2        | ?pageNumber=3&pageSize=2 |
      | 2023-01-01 | 2023-12-08 | CC     | 1096647533 | 2          | 4        | ?pageNumber=1&pageSize=4 |
      | 2023-01-01 | 2023-12-08 | CC     | 44229952   | 1          | 2        |                          |

  @ConsultActionsSuccessWithOptionalBodyCriteria
  Scenario Outline:  Consult actions requested by customers with optional filters: Successful case
    * def startDate = "2023-01-01"
    * def endDate = "2023-12-08"
    * def idType = "CC"
    * def template = read("classpath:jsonbase/actionssearch/create_filter.json")
    * def mandatoryCriteria = get template.searchCriteria.dateRange
    * def addedFilter = get template.searchCriteria.<criteriaAdd>
    * def jsonRequest = {}
    * set jsonRequest.searchCriteria.dateRange = mandatoryCriteria
    * set jsonRequest.searchCriteria.<criteriaAdd> = addedFilter
    Given path actionsPath
    And headers read("classpath:jsonbase/general_headers.json")
    And request jsonRequest
    When method Post
    Then status 200
    And match each response.data[*].<dbFieldName> contains jsonRequest.searchCriteria.<criteriaPath>
    And match response.data[*] contains read("classpath:jsonbase/actionssearch/response_actions_search.json")

    Examples:
      | criteriaAdd | criteriaPath               | dbFieldName                  | idNumber   | state     | transactionType | transactionName | bankEntity    | productType         | productNumber | userName             | userType | userNumber    |
      | state       | state                      | transactionState             | 1096647533 | Ejecutado |                 |                 |               |                     |               |                      |          |               |
      | state       | state                      | transactionState             | 1096647533 | Rechazado |                 |                 |               |                     |               |                      |          |               |
      | state       | state                      | transactionState             | 1096647533 | Cancelada |                 |                 |               |                     |               |                      |          |               |
      | type        | type                       | transactionType              | 1096647533 |           | Administrativa  |                 |               |                     |               |                      |          |               |
      | type        | type                       | transactionType              | 1096647533 |           | Monetaria       |                 |               |                     |               |                      |          |               |
      | type        | type                       | transactionType              | 1096647533 |           | No_monetaria    |                 |               |                     |               |                      |          |               |
      | name        | name                       | transactionCodeDesc          | 1096647533 |           |                 | MMM1            |               |                     |               |                      |          |               |
      | name        | name                       | transactionCodeDesc          | 1096647533 |           |                 | MMM2            |               |                     |               |                      |          |               |
      | name        | name                       | transactionCodeDesc          | 1096647533 |           |                 | MMM3            |               |                     |               |                      |          |               |
      | bankEntity  | bankEntity                 | originBankCode               | 44229952   |           |                 |                 | 4561236489    |                     |               |                      |          |               |
      | bankEntity  | bankEntity                 | originBankCode               | 1096647533 |           |                 |                 | 9856416848321 |                     |               |                      |          |               |
      | product     | product.type               | originProductType            | 1096647533 |           |                 |                 |               | CUENTA_DE_AHORRO    | 899999991     |                      |          |               |
      | product     | product.number             | originProductNumber          | 44229952   |           |                 |                 |               | CUENTA_DE_CORRIENTE | 9878452123    |                      |          |               |
      | product     | product.number             | originProductNumber          | 1096647533 |           |                 |                 |               | TARJETA_DE_CREDITO  | 4898765460    |                      |          |               |
      | user        | user.identification.type   | authorizedUserDocumentType   | 1096647533 |           |                 |                 |               |                     |               | Jhon_Doe             | CC       | 9876543210123 |
      | user        | user.identification.number | authorizedUserDocumentNumber | 44229952   |           |                 |                 |               |                     |               | Angelito_Perez       | CE       | 24658876      |
      | user        | user.identification.number | authorizedUserDocumentNumber | 1096647533 |           |                 |                 |               |                     |               |                      | CE       | 44229585      |
      | user        | user.name                  | authorizedUserName           | 1096647533 |           |                 |                 |               |                     |               | Juanita_Alonsa_Perez | CC       | 98754229585   |

  @ConsultActionsSuccessWithNullOptionalBodyCriteria
  Scenario Outline:  Consult actions requested by customers with a null optional filters: Successful case
    * def startDate = "2023-01-01"
    * def endDate = "2023-12-08"
    * def idType = "CC"
    * def idNumber = "1096647533"
    * def template = read("classpath:jsonbase/actionssearch/create_filter.json")
    * def mandatoryCriteria = get template.searchCriteria.dateRange
    * def addedFilter = get template.searchCriteria.<criteriaAdd>
    * def jsonRequest = {}
    * set jsonRequest.searchCriteria.dateRange = mandatoryCriteria
    * set jsonRequest.searchCriteria.<criteriaAdd> = addedFilter
    Given path actionsPath
    And headers read("classpath:jsonbase/general_headers.json")
    And request jsonRequest
    When method Post
    Then status 200
    And match response.data[*] contains read("classpath:jsonbase/actionssearch/response_actions_search.json")

    Examples:
      | criteriaAdd | state | transactionType | transactionName | bankEntity | productType | productNumber | userName | userType | userNumber |
      | state       |       |                 |                 |            |             |               |          |          |            |
      | type        |       |                 |                 |            |             |               |          |          |            |
      | name        |       |                 |                 |            |             |               |          |          |            |
      | bankEntity  |       |                 |                 |            |             |               |          |          |            |
      | product     |       |                 |                 |            |             |               |          |          |            |
      | user        |       |                 |                 |            |             |               |          |          |            |

  @ConsultActionsSuccessPageSizeLimit
  Scenario: Consult actions made by customers with an PageSize > 100: Successful case
    * def startDate = "2023-01-01"
    * def endDate = "2023-12-08"
    * def idType = "CC"
    * def idNumber = "1096647533"
    * def pageSize = 100
    * def pageNumber = "1"
    * def prevPageItem = ""
    * def paramsActionsSearch = {pageNumber:"1", pageSize:600}
    * def template = read("classpath:jsonbase/actionssearch/create_filter.json")
    * def dateRangeFilter = get template.searchCriteria.dateRange
    * def jsonRequest = {}
    * set jsonRequest.searchCriteria.dateRange = dateRangeFilter
    * def paginatedResponse = read("classpath:jsonbase/actionssearch/paginated_actions_search.json")
    * def jsonResponse = paginatedResponse.links
    Given path actionsPath
    And params paramsActionsSearch
    And headers read("classpath:jsonbase/general_headers.json")
    And request jsonRequest
    When method Post
    Then status 200
    And match response.links contains jsonResponse
    And match response.data[*] contains read("classpath:jsonbase/actionssearch/response_actions_search.json")

  @ConsultActionsSuccessButNonExistentIdNumber
  Scenario Outline: Consult actions made by customers with a non-existent identification-number: Successful case
    * def template = read("classpath:jsonbase/actionssearch/create_filter.json")
    * def dateRangeFilter = get template.searchCriteria.dateRange
    * def jsonRequest = {}
    * set jsonRequest.searchCriteria.dateRange = dateRangeFilter
    Given path actionsPath
    And headers read("classpath:jsonbase/general_headers.json")
    And request jsonRequest
    When method Post
    Then status 204
    And match response == ""

    Examples:
      | startDate  | endDate    | idType | idNumber   |
      | 2023-01-01 | 2023-12-08 | CC     | 2096647535 |

  @ConsultActionsSuccessButNonExistentPage
  Scenario: Consult actions made by customers with a non-existent page: Successful case
    * def startDate = "2023-01-01"
    * def endDate = "2023-12-08"
    * def idType = "CC"
    * def idNumber = "1096647533"
    * def paramsActionsSearch = {pageNumber: "9000000", pageSize:"100"}
    * def template = read("classpath:jsonbase/actionssearch/create_filter.json")
    * def dateRangeFilter = get template.searchCriteria.dateRange
    * def jsonRequest = {}
    * set jsonRequest.searchCriteria.dateRange = dateRangeFilter
    Given path actionsPath
    And params paramsActionsSearch
    And headers read("classpath:jsonbase/general_headers.json")
    And request jsonRequest
    When method Post
    Then status 204
    And match response == ""

  @ConsultActionsSuccessWithSpecialCharactersOptionalCriteria
  Scenario Outline:  Consult actions requested by customers with special characters in optional filters: Successful case
    * def startDate = "2023-01-01"
    * def endDate = "2023-12-08"
    * def idType = "CC"
    * def idNumber = "1096647533"
    * def template = read("classpath:jsonbase/actionssearch/create_filter.json")
    * def mandatoryCriteria = get template.searchCriteria.dateRange
    * def addedFilter = get template.searchCriteria.<criteriaAdd>
    * def jsonRequest = {}
    * set jsonRequest.searchCriteria.dateRange = mandatoryCriteria
    * set jsonRequest.searchCriteria.<criteriaAdd> = addedFilter
    Given path actionsPath
    And headers read("classpath:jsonbase/general_headers.json")
    And request jsonRequest
    When method Post
    Then status 204
    And match response == ""

    Examples:
      | criteriaAdd | criteriaPath               | dbFieldName                  | state | transactionType | transactionName | bankEntity | productType | productNumber | userName | userType | userNumber |
      | state       | state                      | transactionState             | @={*} |                 |                 |            |             |               |          |          |            |
      | type        | type                       | transactionType              |       | @={*}           |                 |            |             |               |          |          |            |
      | name        | name                       | transactionCodeDesc          |       |                 | @={*}           |            |             |               |          |          |            |
      | bankEntity  | bankEntity                 | originBankCode               |       |                 |                 | @={*}      |             |               |          |          |            |
      | product     | product.type               | originProductType            |       |                 |                 |            | @={*}       |               |          |          |            |
      | product     | product.number             | originProductNumber          |       |                 |                 |            |             | @={*}         |          |          |            |
      | user        | user.identification.type   | authorizedUserDocumentType   |       |                 |                 |            |             |               | @={*}    |          |            |
      | user        | user.identification.number | authorizedUserDocumentNumber |       |                 |                 |            |             |               |          | @={*}    |            |
      | user        | user.name                  | authorizedUserName           |       |                 |                 |            |             |               |          |          | @={*}      |

  @ConsultActionsFailedNoBodyContent
  Scenario: Consult actions made by customers without body: Failed case
    * def errorMessage = "The object searchCriteria is not found in body request"
    * def idType = "CC"
    * def idNumber = "1096647533"
    Given path actionsPath
    And headers read("classpath:jsonbase/general_headers.json")
    And request {}
    When method Post
    Then status 500
    And match response.errors.[*] == [{"reason": #(errorMessage),"domain": #(actionsPath),"code": "ATB0019","message": #(errorMessage)}]

  @ConsultActionsFailedMissingMandatoryCriteria
  Scenario: Consult actions made by customers without mandatory filters: Failed case
    * def errorMessage = "The object dateRange is not found in body request"
    * def idType = "CC"
    * def idNumber = "1096647533"
    Given path actionsPath
    And headers read("classpath:jsonbase/general_headers.json")
    And request '{"searchCriteria": {}}'
    When method Post
    Then status 500
    And match response.errors.[*] == [{"reason": #(errorMessage),"domain": #(actionsPath),"code": "ATB0020","message": #(errorMessage)}]

  @ConsultActionsFailedWithoutHeaders
  Scenario Outline: Consult actions requested by the customer without Headers : Failed case
    * def startDate = "2023-01-01"
    * def endDate = "2023-12-08"
    * def idType = "CC"
    * def idNumber = "1096647533"
    * def headerRequest = read("classpath:jsonbase/general_headers.json")
    * remove headerRequest.<headerDelete>
    * def templateBody = read("classpath:jsonbase/actionssearch/create_filter.json")
    * def dateRangeFilter = get templateBody.searchCriteria.dateRange
    * def bodyRequest = {}
    * set bodyRequest.searchCriteria.dateRange = dateRangeFilter
    * set headerRequest.Content-Type = headerDelete == "Content-Type" ? "" : "application/json"
    Given path actionsPath
    And headers headerRequest
    And request bodyRequest
    When method Post
    Then status 500
    And match response.errors.[*] == [{"reason": <errorMessage>,"domain": #(actionsPath),"code": <code>,"message": <errorMessage>}]

    Examples:
      | headerDelete          | code    | errorMessage                                             |
      | message-id            | ATB0008 | The message-id header cannot be null or empty            |
      | session-tracker       | ATB0009 | The session-tracker header cannot be null or empty       |
      | request-timestamp     | ATB0010 | The request-timestamp header cannot be null or empty     |
      | channel               | ATB0011 | The channel header cannot be null or empty               |
      | app-version           | ATB0013 | The app-version header cannot be null or empty           |
      | device-id             | ATB0014 | The device-id header cannot be null or empty             |
      | ip                    | ATB0015 | The ip header cannot be null or empty                    |
      | identification-number | ATB0016 | The identification-number header cannot be null or empty |
      | identification-type   | ATB0017 | The identification-type header cannot be null or empty   |
      | platform-type         | ATB0023 | The platform-type header cannot be null or empty         |
      | Content-Type          | ATT0001 | Unexpected error                                         |

  @ConsultActionsFailedWithInvalidDate
  Scenario Outline:  Consult actions requested by customers with an invalid date format: Failed case
    * def idType = "CC"
    * def idNumber = "1096647533"
    * def template = read("classpath:jsonbase/actionssearch/create_filter.json")
    * def mandatoryCriteria = get template.searchCriteria.dateRange
    * def jsonRequest = {}
    * set jsonRequest.searchCriteria.dateRange = mandatoryCriteria
    Given path actionsPath
    And headers read("classpath:jsonbase/general_headers.json")
    And request jsonRequest
    When method Post
    Then status 500
    And match response.errors.[*] == [{"reason": <errorMessage>,"domain": #(actionsPath),"code": <status>,"message": <errorMessage>}]

    Examples:
      | startDate    | endDate      | status  | errorMessage                                                                    |
      | 2023-01-01   | 08-12-2023   | ATB0018 | The startDate and endDate fields must contain the date in the format yyyy-MM-dd |
      | 08-01-2019   | 2023-12-08   | ATB0018 | The startDate and endDate fields must contain the date in the format yyyy-MM-dd |
      | 2023-01-01   | invalid_date | ATB0018 | The startDate and endDate fields must contain the date in the format yyyy-MM-dd |
      | invalid_date | 2023-12-08   | ATB0018 | The startDate and endDate fields must contain the date in the format yyyy-MM-dd |
      | invalid_date | invalid_date | ATB0018 | The startDate and endDate fields must contain the date in the format yyyy-MM-dd |
      | 2023-01-01   | 2023-i2-08   | ATB0018 | The startDate and endDate fields must contain the date in the format yyyy-MM-dd |
      | .#;asdf      | 2023-i2-08   | ATB0018 | The startDate and endDate fields must contain the date in the format yyyy-MM-dd |
      | 2023-i2-08   | ;aksjdfh     | ATB0018 | The startDate and endDate fields must contain the date in the format yyyy-MM-dd |
      |              | 2023-12-08   | ATB0021 | The startDate field cannot be null or empty                                     |
      | 2023-01-01   |              | ATB0022 | The endDate field cannot be null or empty                                       |

  @ConsultActionsFailedWithStartDateGreaterThanEndDate
  Scenario:  Consult actions requested by customers with an start date greater than end date: Failed case
    * def idType = "CC"
    * def idNumber = "1096647533"
    * def startDate = "2023-12-08"
    * def endDate = "2023-01-01"
    * def errorMessage = "The initial search range value cannot be greater than the final one"
    * def template = read("classpath:jsonbase/actionssearch/create_filter.json")
    * def mandatoryCriteria = get template.searchCriteria.dateRange
    * def jsonRequest = {}
    * set jsonRequest.searchCriteria.dateRange = mandatoryCriteria
    Given path actionsPath
    And headers read("classpath:jsonbase/general_headers.json")
    And request jsonRequest
    When method Post
    Then status 500
    And match response.errors.[*] == [{"reason": #(errorMessage),"domain": #(actionsPath),"code": "ATB0005","message": #(errorMessage)}]

  @ConsultActionsFailedWithoutDateFields
  Scenario Outline:  Consult actions requested by customers without date fields: Failed case
    * def startDate = "2023-01-01"
    * def endDate = "2023-12-08"
    * def idType = "CC"
    * def idNumber = "1096647533"
    * def template = read("classpath:jsonbase/actionssearch/create_filter.json")
    * def mandatoryCriteria = get template.searchCriteria.dateRange
    * def jsonRequest = {}
    * set jsonRequest.searchCriteria.dateRange = mandatoryCriteria
    * remove template.searchCriteria.dateRange.<criteriaRemove>
    Given path actionsPath
    And headers read("classpath:jsonbase/general_headers.json")
    And request jsonRequest
    When method Post
    Then status 500
    And match response.errors.[*] == [{"reason": <errorMessage>,"domain": #(actionsPath),"code": <code>,"message": <errorMessage>}]

    Examples:
      | criteriaRemove | code    | errorMessage                                |
      | startDate      | ATB0021 | The startDate field cannot be null or empty |
      | endDate        | ATB0022 | The endDate field cannot be null or empty   |

  @ConsultActionsErrorInvalidPageNumber
  Scenario Outline: Consult actions made by customers with an invalid PageNumber: Error case
    * def startDate = "2023-01-01"
    * def endDate = "2023-12-08"
    * def idType = "CC"
    * def idNumber = "1096647533"
    * def paramsActionsSearch = {pageNumber:<pageNumber>, pageSize:"2"}
    * def template = read("classpath:jsonbase/actionssearch/create_filter.json")
    * def dateRangeFilter = get template.searchCriteria.dateRange
    * def jsonRequest = {}
    * set jsonRequest.searchCriteria.dateRange = dateRangeFilter
    Given path actionsPath
    And params paramsActionsSearch
    And headers read("classpath:jsonbase/general_headers.json")
    And request jsonRequest
    When method Post
    Then status <status>
    And match response.errors.[*] == [{"reason": <errorMessage>,"domain": #(actionsPath),"code": <code>,"message": <errorMessage>}]

    Examples:
      | pageNumber          | code    | status | errorMessage                           |
      | invalid_page_number | ATT0001 | 500    | Unexpected error                       |
      |                     | ATT0001 | 500    | Unexpected error                       |
      | -1                  | ATB0001 | 422    | The page number must be greater than 0 |
      | 0                   | ATB0001 | 422    | The page number must be greater than 0 |

  @ConsultActionsErrorInvalidPageSize
  Scenario Outline: Consult actions made by customers with an invalid PageSize: Error case
    * def startDate = "2023-01-01"
    * def endDate = "2023-12-08"
    * def idType = "CC"
    * def idNumber = "1096647533"
    * def paramsActionsSearch = {pageSize:<pageSize>, pageNumber:"1"}
    * def template = read("classpath:jsonbase/actionssearch/create_filter.json")
    * def dateRangeFilter = get template.searchCriteria.dateRange
    * def jsonRequest = {}
    * set jsonRequest.searchCriteria.dateRange = dateRangeFilter
    Given path actionsPath
    And params paramsActionsSearch
    And headers read("classpath:jsonbase/general_headers.json")
    And request jsonRequest
    When method Post
    Then status <status>
    And match response.errors.[*] == [{"reason": <errorMessage>,"domain": #(actionsPath),"code": <code>,"message": <errorMessage>}]

    Examples:
      | pageSize          | code    | status | errorMessage                         |
      | invalid_page_size | ATT0001 | 500    | Unexpected error                     |
      |                   | ATT0001 | 500    | Unexpected error                     |
      | -1                | ATB0002 | 422    | The page size must be greater than 0 |
      | 0                 | ATB0002 | 422    | The page size must be greater than 0 |