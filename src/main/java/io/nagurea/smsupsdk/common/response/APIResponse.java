package io.nagurea.smsupsdk.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class APIResponse <T extends ResultResponse>{

    private final String uid;
    private final Integer statusCode;
    private final String additionalMessage;
    private final T effectiveResponse;

}
