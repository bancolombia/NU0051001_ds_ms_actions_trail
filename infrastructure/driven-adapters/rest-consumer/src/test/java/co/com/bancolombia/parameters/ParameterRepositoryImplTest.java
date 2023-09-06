package co.com.bancolombia.parameters;

import co.com.bancolombia.exception.ErrorResponseDTO;
import co.com.bancolombia.exception.RestErrorMapper;
import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParameterRepositoryImplTest {

    private ParameterRepositoryImpl parameterAdapterImpl;

    public static MockWebServer mockBackEnd;

    private String channelManagementParameterResponse;
    private ParametersResponseDTO parametersResponseDTO;

    private final String CHANNEL_QUERY_PARAM = "channelId";
    private final String PARAMETER_NAME_QUERY_PARAM = "paramName";
    private final String TRANSACTION_CODE_QUERY_PARAM = "transactionCode";

    private final String CHANNEL = "TEST";
    private final String PARAMETER_NAME = "TEST";
    private final String TRANSACTION_CODE = "TEST";


    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        ParameterProperties parameterProperties = new ParameterProperties("test", "/test");
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockBackEnd.url("").toString())
                .build();


        RestErrorMapper restErrorMapper = new RestErrorMapper();
        parameterAdapterImpl = new ParameterRepositoryImpl(webClient, parameterProperties, restErrorMapper);
        channelManagementParameterResponse = Files.readString(new ClassPathResource("parameter.json").getFile().toPath(), StandardCharsets.UTF_8);
        parametersResponseDTO = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(channelManagementParameterResponse, ParametersResponseDTO.class);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    void shouldGetParametersSuccessfully() throws InterruptedException {
        Map<String, String> expectedQueryParams = Map.of(
                CHANNEL_QUERY_PARAM, CHANNEL,
                TRANSACTION_CODE_QUERY_PARAM, TRANSACTION_CODE,
                PARAMETER_NAME_QUERY_PARAM, PARAMETER_NAME);

        Map<String, Object> expectedParameters = parametersResponseDTO
                .getData().getParamValue().stream()
                .collect(Collectors.toMap(ParametersResponseDTO.ParameterValueDTO::getType,
                        this::getParameterValue));

        mockBackEnd.enqueue(new MockResponse()
                .setBody(channelManagementParameterResponse)
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(parameterAdapterImpl.getParameters(CHANNEL, PARAMETER_NAME, TRANSACTION_CODE))
                .expectNext(expectedParameters)
                .verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        List<String> queryParams = Arrays.asList(recordedRequest.getRequestUrl().query().split("&"));

        var actualQueryParameters = queryParams.stream()
                .map(queryParam -> Arrays.asList(queryParam.split("=")))
                .collect(Collectors.toMap(queryParamName -> queryParamName.get(0), queryParamValue -> queryParamValue.get(1)));
        assertEquals(expectedQueryParams, actualQueryParameters);
    }

    @Test
    void shouldThrowTechnicalRestClientExceptionWhenBodyIsEmpty() {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(httpStatus.value())
        );

        StepVerifier.create(parameterAdapterImpl.getParameters(CHANNEL, PARAMETER_NAME, TRANSACTION_CODE))
                .expectErrorMatches(ex -> {
                    TechnicalException technicalException = (TechnicalException) ex;
                    assertEquals(httpStatus.toString(), technicalException.getCause().getMessage());
                    assertEquals(TechnicalErrorMessage.REST_CLIENT_EXCEPTION, technicalException.getTechnicalErrorMessage());
                    return true;
                })
                .verify();
    }

    @Test
    void shouldThrowTechnicalRestClientExceptionWhenErrorCode() throws JsonProcessingException {
        ErrorResponseDTO.ErrorDTO errorDTO = new ErrorResponseDTO.ErrorDTO("TEST", "TEST", "at212", "TEST");
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(List.of(errorDTO));
        String response = new ObjectMapper().writeValueAsString(errorResponse);

        mockBackEnd.enqueue(new MockResponse()
                .setBody(response)
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        );

        StepVerifier.create(parameterAdapterImpl.getParameters(CHANNEL, PARAMETER_NAME, TRANSACTION_CODE))
                .expectErrorMatches(ex -> ex instanceof TechnicalException)
                .verify();
    }

    @Test
    void shouldThrowTechnicalExceptionWhenAnUnexpectedExceptionIsThrown() {
        mockBackEnd.enqueue(new MockResponse().setBody("{}")
                .setResponseCode(400));

        StepVerifier.create(parameterAdapterImpl.getParameters(CHANNEL, PARAMETER_NAME, TRANSACTION_CODE))
                .expectErrorMatches(ex -> ((TechnicalException) ex).getTechnicalErrorMessage().equals(TechnicalErrorMessage.UNEXPECTED_EXCEPTION))
                .verify();
    }

    @Test
    void shouldThrowTechnicalExceptionWhenAnWebClientExceptionIsThrown() {
        mockBackEnd.enqueue(new MockResponse()
                .setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)
        );

        StepVerifier.create(parameterAdapterImpl.getParameters(CHANNEL, PARAMETER_NAME, TRANSACTION_CODE))
                .expectErrorMatches(ex -> {
                    TechnicalException technicalException = (TechnicalException) ex;
                    assertTrue(technicalException.getCause() instanceof WebClientException);
                    assertEquals(TechnicalErrorMessage.REST_CLIENT_EXCEPTION, technicalException.getTechnicalErrorMessage());
                    return true;
                })
                .verify();
    }

    private Object getParameterValue(ParametersResponseDTO.ParameterValueDTO parameter) {
        try {
            return new ObjectMapper().readValue(parameter.getDescription(), ParametersResponseDTO.DescriptionDTO.class)
                    .getValue();
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}
