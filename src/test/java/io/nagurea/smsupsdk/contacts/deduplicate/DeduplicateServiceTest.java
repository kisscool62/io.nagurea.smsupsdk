package io.nagurea.smsupsdk.contacts.deduplicate;

import io.nagurea.smsupsdk.common.TestIntBase;
import io.nagurea.smsupsdk.common.status.ResponseStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfiguration.class)
class DeduplicateServiceTest extends TestIntBase {

    private static final String LIST_ID = "50433";

    /**
     * Useless. Only here to show how services could be used with Spring
     */
    @Autowired
    private DeduplicateService deduplicateService;

    private static ClientAndServer mockServer;

    @BeforeAll
    public static void startMockSMSUpServer() {
        ConfigurationProperties.logLevel("DEBUG");
        mockServer = startMockServer();
        mockServer.when(
                request()
                        .withPath("/list/deduplicate/" + LIST_ID )
                        .withMethod("PUT")
                        .withHeader("Authorization", EXPECTED_TOKEN)
        ).respond(
                HttpResponse.response()
                        .withStatusCode(HttpStatusCode.OK_200.code())
                        .withBody(
                                "{\n" +
                                        "  \"status\": 1,\n" +
                                        "  \"message\": \"OK\",\n" +
                                        "  \"removed\": 4\n" +
                                        "}"
                        )
        );
    }

    @AfterAll
    static void stopMockserver(){
        mockServer.stop();
    }

    @Test
    void deduplicate() throws IOException {
        //given
        //expected results
        final DeduplicateResultResponse expectedResponse = DeduplicateResultResponse.builder()
                .message(ResponseStatus.OK.getDescription())
                .removed(4)
                .build();
        final int expectedStatusCode = 200;

        //given arguments

        //when
        final DeduplicateResponse result = deduplicateService.deduplicate(YOUR_TOKEN, LIST_ID);
        final Integer effectiveStatusCode = result.getStatusCode();
        final DeduplicateResultResponse effectiveResponse = result.getEffectiveResponse();

        //then
        assertEquals(expectedStatusCode, effectiveStatusCode);
        assertEquals(expectedResponse, effectiveResponse);
    }


}