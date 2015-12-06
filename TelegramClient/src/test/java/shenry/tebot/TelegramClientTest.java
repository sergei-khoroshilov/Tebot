package shenry.tebot;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import shenry.tebot.telegramclient.HttpTelegramClient;
import shenry.tebot.telegramclient.types.*;

import java.io.IOException;
import java.net.Proxy;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Created by shenry on 02.10.2015.
 */
public class TelegramClientTest {

    private final String API_KEY = "SOME_KEY";

    private final RestTemplate restTemplate = new RestTemplate();
    private final MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);

    @Test
    public void getMe_Success() throws IOException {
        HttpTelegramClient client = getClient();
        String getMeUrl = getCommandUrl("/getMe");
        String responseJson = "{\"ok\":true,\"result\":{\"id\":108090617,\"first_name\":\"FirstName\",\"last_name\":\"LastName\",\"username\":\"UserName\"}}";
        User expectedUser = new User(108090617, "FirstName", "LastName", "UserName");

        mockServer.expect(requestTo(getMeUrl))
                  .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        User actualUser = client.getMe();

        mockServer.verify();
        //ssert.assertEquals(true, response.isOk());
        //Assert.assertEquals("", response.getDescription());
        Assert.assertEquals(expectedUser, actualUser);
    }

    @Test
    public void getUpdates_Success() throws IOException {
        HttpTelegramClient client = getClient();
        String getUpdatesUrl = getCommandUrl("/getUpdates");
        String responseJson = "{\"ok\":true,\"result\":[{\"update_id\":115393929,\n" +
                "\"message\":{\"message_id\":7,\"from\":{\"id\":85226090,\"first_name\":\"shenry\"},\"chat\":{\"id\":85226090,\"first_name\":\"shenry\",\"type\":\"private\"},\"date\":1446384241,\"text\":\"456\"}}]}";

        User expectedFrom = new User();
        expectedFrom.setId(85226090);
        expectedFrom.setFirstName("shenry");

        Chat expectedChat = new Chat();
        expectedChat.setId(85226090);
        expectedChat.setFirstName("shenry");
        expectedChat.setType(ChatType.PRIVATE);

        Message expectedMessage = new Message();
        expectedMessage.setId(7);
        expectedMessage.setDate(1446384241);
        expectedMessage.setSender(expectedFrom);
        expectedMessage.setChat(expectedChat);
        expectedMessage.setText("456");

        Update expectedUpdate = new Update();
        expectedUpdate.setId(115393929);
        expectedUpdate.setMessage(expectedMessage);

        List<Update> expectedUpdates = Arrays.asList(expectedUpdate);

        mockServer.expect(requestTo(getUpdatesUrl))
                  .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        List<Update> actualUpdates = client.getUpdates();

        mockServer.verify();
        //Assert.assertEquals(true, response.isOk());
        //Assert.assertEquals("", response.getDescription());
        Assert.assertEquals(expectedUpdates, actualUpdates);
    }

    private HttpTelegramClient getClient() {

        return new HttpTelegramClient(API_KEY) {

            @Override
            protected RestTemplate getRestTemplate(Proxy proxy) {
                return restTemplate;
            }
        };

        //return new TelegramClient(API_KEY);
    }

    private String getCommandUrl(String command) {
        return "https://api.telegram.org/bot" + API_KEY + command;
    }
}
