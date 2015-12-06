package shenry.tebot.telegramclient.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Update {
    @JsonProperty("update_id")
    @Getter
    @Setter
    private int id;

    @JsonProperty("message")
    @Getter
    @Setter
    private Message message;
}
