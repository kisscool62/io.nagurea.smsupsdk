package io.nagurea.smsupsdk.sendmessages.campaign;

import com.google.gson.Gson;
import io.nagurea.smsupsdk.common.post.POSTSMSUpService;
import io.nagurea.smsupsdk.helper.json.GsonHelper;
import io.nagurea.smsupsdk.sendmessages.arguments.AlertOptionalArguments;
import io.nagurea.smsupsdk.sendmessages.arguments.MarketingOptionalArguments;
import io.nagurea.smsupsdk.sendmessages.arguments.OptionalArguments;
import io.nagurea.smsupsdk.sendmessages.campaign.body.Campaign;
import io.nagurea.smsupsdk.sendmessages.campaign.body.Message;
import io.nagurea.smsupsdk.sendmessages.campaign.body.Recipients;
import io.nagurea.smsupsdk.sendmessages.campaign.body.SMS;
import io.nagurea.smsupsdk.sendmessages.sender.NoSender;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.IOException;
import java.util.UUID;

public class CampaignService extends POSTSMSUpService {
    private static final String URL = "/SEND";

    public CampaignService(String rootUrl) {
        super(rootUrl);
    }

    /**
     * Send a campaign message for general purpose (called alert)
     * @param token SMSUp token
     * @param text to send
     * @param recipients is actually the set of couples (gsmId, phone number) represented by @{@link Recipients}
     * @return CampaignResponse with detailed @{@link CampaignResultResponse}
     * @throws IOException when something got wrong during effective query to SMSUp
     */
    public CampaignResponse sendAlert(String token, String text, Recipients recipients) throws IOException {
        return send(token, text, recipients, AlertOptionalArguments.builder().sender(NoSender.build()).build());
    }

     /**
     * Send a campaign message for general purpose (called alert)
     * @param token SMSUp token
     * @param text to send
     * @param recipients is actually the set of couples (gsmId, phone number) represented by @{@link Recipients}
     * @param alertOptionalArgument is argument wrapper object
     * @return CampaignResponse with detailed @{@link CampaignResultResponse}
     * @throws IOException when something got wrong during effective query to SMSUp
     */
    public CampaignResponse sendAlert(String token, String text, Recipients recipients, @NonNull AlertOptionalArguments alertOptionalArgument) throws IOException {
        return send(token, text, recipients, alertOptionalArgument);
    }

    /**
     *
     * @param token SMSUp token
     * @param text to send
     * @param recipients to send the text to
     * @return CampaignResponse with detailed @{@link CampaignResultResponse}
     * @throws IOException when something got wrong during effective query to SMSUp
     */
    public CampaignResponse sendMarketing(String token, String text, Recipients recipients) throws IOException {
        return send(token, text, recipients, MarketingOptionalArguments.builder().sender(NoSender.build()).build());
    }

    /**
     *
     * @param token SMSUp token
     * @param text to send
     * @param recipients to send the text to
     * @param marketingOptionalArguments who do you want the sms appears to be sent by
     * @return CampaignResponse with detailed @{@link CampaignResultResponse}
     * @throws IOException when something got wrong during effective query to SMSUp
     */
    public CampaignResponse sendMarketing(String token, String text, Recipients recipients, @NonNull MarketingOptionalArguments marketingOptionalArguments) throws IOException {
        return send(token, text, recipients, marketingOptionalArguments);
    }

    /**
     *
     * @param token SMSUp token
     * @param text to send
     * @param recipients to send the text to
     * @param optionalArguments @{@link MarketingOptionalArguments} or @{@link AlertOptionalArguments}
     * @return CampaignResponse with detailed @{@link CampaignResultResponse}
     * @throws IOException
     */
    private CampaignResponse send(String token, String text, Recipients recipients, @NonNull OptionalArguments optionalArguments) throws IOException {
        final ImmutablePair<Integer, String> response = post(URL, token, buildData(text, recipients, optionalArguments));
        final CampaignResultResponse responseObject = new Gson().fromJson(response.getRight(), CampaignResultResponse.class);
        return CampaignResponse.builder()
                .uid(UUID.randomUUID().toString())
                .statusCode(response.getLeft())
                .effectiveResponse(responseObject)
                .build();
    }

    private String buildData(String text, Recipients recipients, @NonNull OptionalArguments optionalArguments) {
        return GsonHelper.toJson(Campaign.builder()
                .sms(
                        SMS.builder()
                                .message(
                                        Message.builder()
                                                .unicode(optionalArguments.getUnicode())
                                                .delay(optionalArguments.getDelay())
                                                .text(text)
                                                .pushtype(optionalArguments.getPushType())
                                                .sender(optionalArguments.getSender())
                                        .build()
                                )
                                .recipients(recipients)
                                .build()
                )
                .build()
        );
    }

}
