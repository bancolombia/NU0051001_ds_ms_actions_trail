@ignore
Feature: reusable scenarios for search parameters test

  Background:
    * url api.channelManagement

    @CreateParameters
    Scenario:
      * def jsonRequestTemplate = read("classpath:jsonbase/parameters/create_parameters.json")
      Given request jsonRequestTemplate
      When method Post
      Then status 200