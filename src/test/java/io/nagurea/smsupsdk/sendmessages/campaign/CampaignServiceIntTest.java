package io.nagurea.smsupsdk.sendmessages.campaign;

import com.google.common.collect.Sets;
import io.nagurea.smsupsdk.common.status.ResponseStatus;
import io.nagurea.smsupsdk.sendmessages.campaign.body.Gsm;
import io.nagurea.smsupsdk.sendmessages.campaign.body.Recipients;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfiguration.class)
class CampaignServiceIntTest {

    /**
     * Useless. Only here to see how services could be used with Spring
     */
    @Autowired
    private CampaignService campaignService;

    @BeforeAll
    public static void startMockSMSUpServer(){
        final ClientAndServer mockServer = ClientAndServer.startClientAndServer("localhost", 4242, 4242);
        mockServer.when(
                request()
                        .withPath("/SEND")
                        .withMethod("POST")
        ).respond(
                HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(
                                "{\n" +
                                        "  \"status\": 1,      \n" +
                                        "  \"message\": \"OK\",\n" +
                                        "  \"ticket\": \"14672468\", \n" +
                                        "  \"cost\": 2,              \n" +
                                        "  \"credits\": 642,         \n" +
                                        "  \"total\": 2,             \n" +
                                        "  \"sent\": 2,              \n" +
                                        "  \"blacklisted\": 0,       \n" +
                                        "  \"duplicated\": 0,        \n" +
                                        "  \"invalid\": 0,           \n" +
                                        "  \"npai\": 0               \n" +
                                        "}"
                        )
        )
        ;
    }

     @Test
     void sendMarketing() throws IOException {
         //given
         final CampaignResultResponse expectedResponse = CampaignResultResponse.builder()
                 .message(ResponseStatus.OK.getDescription())
                 .ticket("14672468")
                 .cost(2)
                 .credits(642)
                 .total(2)
                 .sent(2)
                 .blacklisted(0)
                 .duplicated(0)
                 .invalid(0)
                 .npai(0)
                 .build();
         final int expectedStatusCode = 200;
         final Recipients recipients = Recipients.builder().gsm(
                 Sets.newHashSet(
                     Gsm.builder()
                             .gsmsmsid("100").value("41781234567").build(),
                     Gsm.builder()
                             .gsmsmsid("101").value("41781234566").build()
                 )
         ).build();

         //when
         final CampaignResponse result = campaignService.sendMarketing("token", "This is a text", recipients);
         final Integer effectiveStatusCode = result.getStatusCode();
         final CampaignResultResponse effectiveResponse = result.getEffectiveResponse();

         //then
         assertEquals(expectedStatusCode, effectiveStatusCode);
         assertEquals(expectedResponse, effectiveResponse);

    }
}
