package shenry.tebot.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class User {
    @JsonProperty("id")
    @Getter
    @Setter
    private int id;

    @JsonProperty("first_name")
    @Getter
    @Setter
    private String firstName;

    @JsonProperty("last_name")
    @Getter
    @Setter
    private String lastName;

    @JsonProperty("username")
    @Getter
    @Setter
    private String username;
}
