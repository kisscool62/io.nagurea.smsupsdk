package io.nagurea.smsupsdk.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.MalformedURLException;
import java.net.URL;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class URLHelper {

    private static final String SLASH = "/";

    public static URL add(URL rootUrl, String additionalPath) throws MalformedURLException {
        String spec = rootUrl.toString();
        if(!isTrailedBySlash(spec)) {
            spec += SLASH;
        }

        return new URL(spec + additionalPath);
    }

    private static boolean isTrailedBySlash(String spec) {
        return spec != null && spec.endsWith(SLASH);
    }
}
