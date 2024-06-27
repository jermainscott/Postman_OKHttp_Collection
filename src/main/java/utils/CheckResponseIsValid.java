package utils;

import okhttp3.Response;

public class CheckResponseIsValid {

    public static void checkResponseCode(Response response, int expectedCode) {
        if (response.code() != expectedCode) {
            throw new AssertionError("Expected response code: " + expectedCode + " but was: "
                    + response.code());
        }
    }

    public static void checkResponseBodyContains(String responseBody, String keyword) {
        if (!responseBody.contains(keyword)) {
            throw new AssertionError("Expected response body to contain: " + keyword + " but was: "
                    + responseBody);
        }
    }
}
