package shenry.tebot.telegramclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import shenry.tebot.telegramclient.types.Message;
import shenry.tebot.telegramclient.types.Update;
import shenry.tebot.telegramclient.types.User;
import shenry.tebot.telegramclient.requests.*;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;

public class HttpTelegramClient implements TelegramClient {
    private static final Logger logger = LoggerFactory.getLogger(HttpTelegramClient.class);

    private static final String DEFAULT_API_ADDRESS = "https://api.telegram.org";

    private final String apiAddress;
    private final String token;

    private final String urlTemplate;
    private final RestTemplate restTemplate;

    // region Constructors

    public HttpTelegramClient(String token) {
        this(DEFAULT_API_ADDRESS, token, null);
    }

    public HttpTelegramClient(String token, Proxy proxy) {
        this(DEFAULT_API_ADDRESS, token, proxy);
    }

    public HttpTelegramClient(String apiAddress, String token, Proxy proxy) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be empty");
        }

        this.apiAddress = apiAddress;
        this.token = token;

        urlTemplate = String.format("%s/bot%s", apiAddress, token);
        restTemplate = getRestTemplate(proxy);

        // Because we need to get description from response
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            protected boolean hasError(HttpStatus statusCode) {
                return false;
            }});
    }

    // For unit testing
    protected RestTemplate getRestTemplate(Proxy proxy) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        if (proxy != null) {
            factory.setProxy(proxy);
        }

        return new RestTemplate(factory);
    }

    // endregion

    // region Builder

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String apiAddress = DEFAULT_API_ADDRESS;
        private String token;
        private Proxy proxy;

        public Builder setApiAddress(String apiAddress) {
            this.apiAddress = apiAddress;
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public Builder setProxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public HttpTelegramClient build() {
            return new HttpTelegramClient(apiAddress, token, proxy);
        }
    }

    // endregion

    // region TelegramClient methods

    @Override
    public User getMe() throws IOException {
        return get(TelegramMethod.GET_ME, UserResponse.class);
    }

    @Override
    public List<Update> getUpdates(GetUpdatesRequest request) throws IOException {
        return post(TelegramMethod.GET_UPDATES, request, GetUpdatesResponse.class);
    }

    @Override
    public List<Update> getUpdates() throws IOException {
        return getUpdates(new GetUpdatesRequest());
    }

    @Override
    public Message sendMessage(SendMessageRequest request) throws IOException {
        return post(TelegramMethod.SEND_MESSAGE, request, SendMessageResponse.class);
    }

    // endregion

    // region Internal request methods

    private <T, RT extends ResponseBase<T>> T get(TelegramMethod method, Class<RT> responseType) throws IOException {
        String commandUrl = getCommandUrl(method);
        RT response = restTemplate.getForObject(commandUrl, responseType);

        return processResponse(response);
    }

    private <T, RT extends ResponseBase<T>> T post(TelegramMethod method, Object request, Class<RT> responseType) throws IOException {
        String commandUrl = getCommandUrl(method);
        RT response = restTemplate.postForObject(commandUrl, request, responseType);

        return processResponse(response);
    }

    private <T> T processResponse(ResponseBase<T> response) throws IOException {
        if (!response.isOk()) {
            throw new IOException(response.getDescription());
        }

        return response.getContent();
    }

    private String getCommandUrl(TelegramMethod method) {
        return urlTemplate + method.getValue();
    }

    // endregion
}
