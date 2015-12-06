package shenry.tebot.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Created by shenry on 01.10.2015.
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
