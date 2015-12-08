package shenry.tebot.telegramclient.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Base class for all responses
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ResponseBase<T> {
    @JsonProperty("ok")
    @Getter
    @Setter
    private boolean ok;

    @JsonProperty("description")
    @Getter
    @Setter
    private String description = "";

    @JsonProperty("result")
    @Getter
    @Setter
    private T content;
}
