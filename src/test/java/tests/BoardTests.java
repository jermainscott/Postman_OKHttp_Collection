package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Board;
import okhttp3.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import utils.CheckResponseIsValid;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BoardTests {

    private Properties prop = new Properties();
    private String SECRET_KEY;
    private String boardId;
    private OkHttpClient client;
    private ObjectMapper objectMapper;

    @BeforeTest
    public void beforeAllTests() throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("secrets.properties");
        prop.load(stream);
        String API_KEY = prop.getProperty("api_key");
        String TOKEN = prop.getProperty("token");
        SECRET_KEY = "token=" + TOKEN + "&key=" + API_KEY;

        client = new OkHttpClient();
        objectMapper = new ObjectMapper();
    }

    private String getSecretKey() {
        return SECRET_KEY;
    }




    @Test
    public void createBoardTest() throws IOException {
        String boardName = "Test_Board";
        String url = "https://api.trello.com/1/boards/?name=" + boardName + "&" + getSecretKey();

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", null))
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        if (response.isSuccessful()) {
            Board createdBoard = objectMapper.readValue(responseBody, Board.class);
            boardId = createdBoard.getId();
            System.out.println("Created board ID: " + boardId);
            CheckResponseIsValid.checkResponseBodyContains(responseBody, boardName);
        } else {
            System.err.println("Error: " + responseBody);
        }
        CheckResponseIsValid.checkResponseCode(response, 200);
    }





    @Test(dependsOnMethods = {"createBoardTest"})
    public void getBoardByIdTest() throws IOException {
        System.out.println("Getting board with ID: " + boardId);
        String url = "https://api.trello.com/1/boards/" + boardId + "?" + getSecretKey();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        if (response.isSuccessful()) {
            Board board = objectMapper.readValue(responseBody, Board.class);
            System.out.println("Board name: " + board.getName());
            CheckResponseIsValid.checkResponseBodyContains(responseBody, boardId);
        } else {
            System.err.println("Error: " + responseBody);
        }
        CheckResponseIsValid.checkResponseCode(response, 200);
    }




    @Test(dependsOnMethods = {"getBoardByIdTest"})
    public void updateBoardTest() throws IOException {
        String updatedName = "Updated_TestBoard";
        String url = "https://api.trello.com/1/boards/" + boardId + "?name=" + updatedName + "&" + getSecretKey();

        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create("", null))
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        if (response.isSuccessful()) {
            Board updatedBoard = objectMapper.readValue(responseBody, Board.class);
            System.out.println("Updated board name: " + updatedBoard.getName());
            CheckResponseIsValid.checkResponseBodyContains(responseBody, updatedName);
        } else {
            System.err.println("Error: " + responseBody);
        }
        CheckResponseIsValid.checkResponseCode(response, 200);
    }




    @Test(dependsOnMethods = {"updateBoardTest"})
    public void deleteBoardTest() throws IOException {
        String url = "https://api.trello.com/1/boards/" + boardId + "?" + getSecretKey();

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        if (response.isSuccessful()) {
            System.out.println("Board deleted successfully");
            boardId = null;
        } else {
            System.err.println("Error: " + responseBody);
        }
        CheckResponseIsValid.checkResponseCode(response, 200);
    }
}
