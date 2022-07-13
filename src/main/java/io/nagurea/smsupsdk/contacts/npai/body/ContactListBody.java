package io.nagurea.smsupsdk.contacts.npai.body;

import lombok.Builder;
import lombok.ToString;

/**
 Body of post query described on https://doc.smsup.ch/en/api/sms/contact/blacklist
 */
@Builder
@ToString
public class ContactListBody {

    private final NPAIList npai;


}
