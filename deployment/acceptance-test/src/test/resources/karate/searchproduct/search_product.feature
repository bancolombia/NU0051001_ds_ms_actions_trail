Feature: I as a user want to consult the products that are being used by customers.

  Background:
    * url api.baseUrl
    * def productPath = '/products'

  @ConsultProductSuccessful
  Scenario Outline: Consult products used by the customer with a unique product type: Successful case
    Given path productPath
    And headers read("classpath:jsonbase/general_headers.json")
    When method Get
    Then status 200
    And match response.[0] == {"type": "CUENTA_DE_AHORRO","numbers": ["123456789123456"]}

    Examples:
      | idType | idNumber    |
      | CC     | 00000009043 |

  @ConsultProductWithoutHeader
  Scenario Outline: Consult a product that is not in the database
    Given path productPath
    And headers read("classpath:jsonbase/general_headers.json")
    When method Get
    Then status 200
    And match response == []

    Examples:
      | idType | idNumber      |
      | TI     | 0045660009043 |
      | TI     | Ahahah        |
      | TI     | Metian&nos    |
      | TI     | Metian>>nos   |

  @ConsultProductWithoutHeader
  Scenario: Consult the products used by the customer without sending him all the headers: Alternate case
    Given path productPath
    When method Get
    Then status 500
    And match response.errors[0] ==  {"reason":"#string","domain":"/products","code":"#string","message":"#string"}

  @ConsultProductWithoutHeader
  Scenario Outline: Consult the products used by the customer without headers: Alternate case 2
    * def jsonHeaders = read("classpath:jsonbase/general_headers.json")
    * remove jsonHeaders.<headersDelete>
    Given path productPath
    And headers jsonHeaders
    When method Get
    Then status 500
    And match response.errors[0] == {"reason":"<message>","domain":"/products","code":"<code>","message":"<message>"}

    Examples:
      | headersDelete         | message                                                  | idType | idNumber    | code    |
      | identification-number | The identification-number header cannot be null or empty | CC     | 00000009043 | ATB0016 |
      | identification-type   | The identification-type header cannot be null or empty   | CC     | 00000009043 | ATB0017 |
      | ip                    | The ip header cannot be null or empty                    | CC     | 00000009043 | ATB0015 |
      | device-id             | The device-id header cannot be null or empty             | CC     | 00000009043 | ATB0014 |
      | app-version           | The app-version header cannot be null or empty           | CC     | 00000009043 | ATB0013 |
      | channel               | The channel header cannot be null or empty               | CC     | 00000009043 | ATB0011 |
      | request-timestamp     | The request-timestamp header cannot be null or empty     | CC     | 00000009043 | ATB0010 |
      | session-tracker       | The session-tracker header cannot be null or empty       | CC     | 00000009043 | ATB0009 |
      | message-id            | The message-id header cannot be null or empty            | CC     | 00000009043 | ATB0008 |