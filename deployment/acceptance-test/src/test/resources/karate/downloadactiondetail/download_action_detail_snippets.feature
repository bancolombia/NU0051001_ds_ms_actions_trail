@ignore
Feature: Scenario to check if the file returned by the endpoint is equal to the local file

  Background:
    * url api.baseUrl
    * def actionDetailPath = '/detailed-report/'
    * def actionId = '63b5f3f3d3afdd7121d701b9'
    * def lcoalFile = karate.readAsBytes("local_file.extension")

  @DownloadActionDetailFileDataCompareSuccessfulCase
  Scenario Outline: Check if the file data of response is equals to local file data: Successful Case
    Given path actionDetailPath + actionId
    And headers read("classpath:jsonbase/general_headers.json")
    When method get
    And header 'Accept' = 'application/pdf'
    Then status 201
    * def fileDataComparator = Java.type('karate.downloadactiondetail.FileDataComparator')
    * def areFileDataEquals = fileDataComparator.compareFileData(lcoalFile, responseBytes)
    And match areFileDataEquals == true

    Examples:
      | idType | idNumber    |
      | CC     | 00000009043 |