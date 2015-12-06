package shenry.tebot.telegramclient.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
public class Chat {
    @JsonProperty("id")
    @Getter
    @Setter
    private int id;

    @JsonProperty("type")
    @Getter
    @Setter
    private ChatType type;

    @JsonProperty("title")
    @Getter
    @Setter
    private String title;

    @JsonProperty("username")
    @Getter
    @Setter
    private String username;

    @JsonProperty("first_name")
    @Getter
    @Setter
    private String firstName;

    @JsonProperty("last_name")
    @Getter
    @Setter
    private String lastName;
}
