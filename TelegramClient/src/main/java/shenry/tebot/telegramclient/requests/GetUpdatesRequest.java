package shenry.tebot.telegramclient.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by shenry on 02.10.2015.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
public class GetUpdatesRequest {
    /**
     * Identifier of the first update to be returned
     */
    @JsonProperty("offset")
    @Getter
    @Setter
    private Integer offset;

    /**
     * Limits the number of updates to be retrieved
     */
    @JsonProperty("limit")
    @Getter
    @Setter
    private Integer limit;

    /**
     * Timeout in seconds for long polling
     */
    @JsonProperty("timeout")
    @Getter
    @Setter
    private Integer timeout;
}
