package io.nagurea.smsupsdk.sendsms.shorturl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.nagurea.smsupsdk.common.status.ResponseStatus;
import io.nagurea.smsupsdk.sendsms.arguments.AlertOptionalArguments;
import io.nagurea.smsupsdk.sendsms.arguments.Delay;
import io.nagurea.smsupsdk.sendsms.campaignwithlist.CampaignWithListResponse;
import io.nagurea.smsupsdk.sendsms.campaignwithlist.CampaignWithListResultResponse;
import io.nagurea.smsupsdk.sendsms.campaignwithlist.CampaignWithListService;
import io.nagurea.smsupsdk.sendsms.campaignwithlist.body.ListId;
import io.nagurea.smsupsdk.sendsms.sender.Sender;
import lombok.NonNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.JsonBody.json;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfiguration.class)
class CampaignWithListAndShortURLServiceIntTest {

    /**
     * Useless. Only here to see how services could be used with Spring
     */
    @Autowired
    private CampaignWithListAndShortURLService campaignWithListAndShortURLService;

    private static ClientAndServer mockServer;

    private static final Object EXPECTED_JSON_OBJECT = new Object() {
        final Object sms = new Object() {
            final Object message = new Object(){
                final String text = "Message via API with a first link : <-short-> and a second one : <-short->";
                final String pushtype = "alert";
                final String sender = "GLaDOS";
                final String delay = "2022-07-08 10:44:03";
                final Integer unicode = 0;
                final Object[] links = {
                        new Object(){
                            final String value = "https://youtu.be/dQw4w9WgXcQ";
                        },
                        new Object(){
                            final String value = "https://youtu.be/X61BVv6pLtw";
                        }
                };
            };
            final Object[] lists = {
                    new Object(){
                        final Integer value = 45190;
                    },
                    new Object(){
                        final Integer value = 47854;
                    }
            };
        };
    };

   @BeforeAll
    public static void startMockSMSUpServer(){
        mockServer = ClientAndServer.startClientAndServer("localhost", 4242, 4242);

        mockServer.when(
                request()
                        .withPath("/send/lists")
                        .withMethod("POST")
                        .withBody(json(EXPECTED_JSON_OBJECT))
        ).respond(
                HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(
                                "{\n" +
                                        "  \"status\": 1,      \n" +
                                        "  \"message\": \"OK\",\n" +
                                        "  \"ticket\": \"14672468\",\n" +
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
        );


    }

    @AfterAll
    static void stopMockserver(){
        mockServer.stop();
    }

    @Test
    void sendAlert() throws IOException {
        //given
        final CampaignWithListResultResponse expectedResponse = CampaignWithListResultResponse.builder()
                .message(ResponseStatus.OK.getDescription())
                .responseStatus(ResponseStatus.OK)
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
        final Set<ListId> lists = Sets.newHashSet(
                ListId.builder().value(45190).build(),
                ListId.builder().value(47854).build());

        final AlertOptionalArguments alertOptionalArgument = AlertOptionalArguments.builder()
                .unicode(0)
                .delay(Delay.builder().value(LocalDateTime.of(2022, 7, 8, 10, 44, 3)).build())
                .sender(Sender.build("GLaDOS"))
                .build();

        final List<String> links = Lists.newArrayList("https://youtu.be/dQw4w9WgXcQ", "https://youtu.be/X61BVv6pLtw");

        //when
        final CampaignWithListResponse result = campaignWithListAndShortURLService.sendAlert("token", "Message via API with a first link : <-short-> and a second one : <-short->", lists, alertOptionalArgument, links);
        final Integer effectiveStatusCode = result.getStatusCode();
        final CampaignWithListResultResponse effectiveResponse = result.getEffectiveResponse();

        //then
        assertEquals(expectedStatusCode, effectiveStatusCode);
        assertEquals(expectedResponse, effectiveResponse);
        assertEquals(ResponseStatus.OK, effectiveResponse.getStatus());
        assertEquals("OK", effectiveResponse.getMessage());

    }

}
