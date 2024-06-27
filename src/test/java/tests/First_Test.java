package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Board;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import utils.CheckResponseIsValid;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;



//This Class is used to verify that the Trello API is working

public class First_Test {

    private Properties prop = new Properties();
    private String SECRET_KEY;
    private OkHttpClient client;
    private ObjectMapper objectMapper;

    @BeforeTest
    public void beforeAllTests() throws IOException {
        // Load the properties file
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("secrets.properties");
        prop.load(stream);

        // Get API key and token from properties file
        String API_KEY = prop.getProperty("api_key");
        String TOKEN = prop.getProperty("token");

        // Construct the SECRET_KEY
        SECRET_KEY = "token=" + TOKEN + "&key=" + API_KEY;

        // Initialize OkHttpClient and ObjectMapper
        client = new OkHttpClient();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void firstMethod() throws IOException {
        // Construct the URL with SECRET_KEY
        String url = "https://api.trello.com/1/members/me?" + SECRET_KEY;

        // Create the request
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        // Execute the request and get the response
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        // Print the response for debugging purposes
        System.out.println(responseBody);

        // Validate the response
        CheckResponseIsValid.checkResponseCode(response, 200);
        CheckResponseIsValid.checkResponseBodyContains(responseBody, "username");

        // Deserialize the response into a Board object
        Board board = objectMapper.readValue(responseBody, Board.class);

        // Print the Board object details
        System.out.println("Board ID: "      + board.getId());
        System.out.println("Board Name: " + board.getName());
        System.out.println("Board URL: " + board.getUrl());
    }
}
