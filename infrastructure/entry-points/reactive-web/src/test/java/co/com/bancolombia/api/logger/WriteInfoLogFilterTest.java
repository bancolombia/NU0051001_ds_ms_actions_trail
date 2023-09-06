package co.com.bancolombia.api.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;


import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WriteInfoLogFilterTest {

    @InjectMocks
    private WriteInfoLogFilter writeInfoLogFilter;

    @Mock
    private WebFilterChain filterChain;

    private final ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);

    @BeforeEach
    void setUp() {
        when(filterChain.filter(captor.capture())).thenReturn(Mono.empty());
    }

    @Test
    void shouldWriteInfoLog() {

        String logName = "JSON_TECH_APPENDER";

        String expectedMessage = "ObjectTechMsg(appName=ds_ms_action_trail, transactionId=message-id, actionName=, " +
                "serviceName=ds_ms_action_trail, componentName=WriteInfoLogFilter, tagList=[, app-version ], message=";

        Logger logger = (Logger) LoggerFactory.getLogger(logName);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        MockServerHttpRequest request = MockServerHttpRequest
                .method(HttpMethod.GET, anyString())
                .header(LogsConstantsEnum.MESSAGE_ID.getName(), LogsConstantsEnum.MESSAGE_ID.getName())
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getAttributes().put(LogsConstantsEnum.CACHE_RESPONSE_INSTANT.getName(), System.currentTimeMillis());

        writeInfoLogFilter.filter(exchange, filterChain).subscribe();

        List<ILoggingEvent> logsList = listAppender.list;

        assertEquals(logName, logsList.get(0).getLoggerName());
        assertEquals(Level.INFO, logsList.get(0).getLevel());
        Boolean containExpected = logsList.get(0).getMessage().startsWith(expectedMessage);
        assertEquals(Boolean.TRUE, containExpected, "The log message is not the same as expected");
    }

    @Test
    void shouldRunMethodFilterWithOutErrors() {
        MockServerHttpRequest request = MockServerHttpRequest
                .method(HttpMethod.GET, anyString())
                .header(LogsConstantsEnum.MESSAGE_ID.getName(), LogsConstantsEnum.MESSAGE_ID.getName())
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getAttributes().put(LogsConstantsEnum.CACHE_RESPONSE_INSTANT.getName(), System.currentTimeMillis());

        writeInfoLogFilter.filter(exchange, filterChain)
                .as(StepVerifier::create)
                .verifyComplete();
    }
}
