Feature: I want to manage the parameters to be used in action trail configured in channel management

  Background:
    * url api.baseUrl
    * def parameterPath = '/parameters'
    * def generateChannel = karate.read("classpath:karate/generate-random-code.js")
    * def channel = generateChannel(4)

  @CreateParametersSuccessfulCase
  Scenario Outline: Search parameters: Successful case
    * def responseCreateParameters = call read("search_parameters_snippets.feature@CreateParameters")
    Given path parameterPath
    And headers read("classpath:jsonbase/parameters/parameters_headers.json")
    When method Get
    Then status 200
    * json parameterMonetaria = responseCreateParameters.jsonRequestTemplate.paramValue[0].description
    * json parameterNoMonetaria = responseCreateParameters.jsonRequestTemplate.paramValue[1].description
    And match parameterMonetaria.value contains response.data.monetarias
    And match parameterNoMonetaria.value contains response.data.no_monetarias

    Examples:
      | idType | idNumber    |
      | CC     | 10172566988 |

  @CreateParametersAlternateCase
  Scenario Outline: Search parameters without parameter in channel management: Alternate case
    Given path parameterPath
    * def channel = '0a0'
    And headers read("classpath:jsonbase/parameters/parameters_headers.json")
    When method Get
    Then status 404
    And match response.errors[0] == { "reason": "The parameter does not exist", "domain": "/parameters", "code": "ATT0011", "message": "The parameter does not exist" }

    Examples:
      | idType | idNumber    |
      | CC     | 10172566988 |

  @CreateParametersAlternateCase01
  Scenario Outline: Search parameters without manditory header: Alternate case
    Given path parameterPath
    * def jsonHeaders = read("classpath:jsonbase/parameters/parameters_headers.json")
    * remove jsonHeaders.<headersDelete>
    And headers jsonHeaders
    When method Get
    Then status 500
    And match response.errors[0] == { "reason": "<message>", "domain": "/parameters", "code": "<code>", "message": "<message>" }

    Examples:
      | headersDelete         | message                                                  | idType | idNumber    | code    |
      | identification-number | The identification-number header cannot be null or empty | CC     | 10172566988 | ATB0016 |
      | identification-type   | The identification-type header cannot be null or empty   | CC     | 10172566988 | ATB0017 |
      | ip                    | The ip header cannot be null or empty                    | CC     | 10172566988 | ATB0015 |
      | device-id             | The device-id header cannot be null or empty             | CC     | 10172566988 | ATB0014 |
      | app-version           | The app-version header cannot be null or empty           | CC     | 10172566988 | ATB0013 |
      | channel               | The channel header cannot be null or empty               | CC     | 10172566988 | ATB0011 |
      | request-timestamp     | The request-timestamp header cannot be null or empty     | CC     | 10172566988 | ATB0010 |
      | session-tracker       | The session-tracker header cannot be null or empty       | CC     | 10172566988 | ATB0009 |
      | message-id            | The message-id header cannot be null or empty            | CC     | 10172566988 | ATB0008 |
      | platform-type         | The platform-type header cannot be null or empty         | CC     | 10172566988 | ATB0023 |
