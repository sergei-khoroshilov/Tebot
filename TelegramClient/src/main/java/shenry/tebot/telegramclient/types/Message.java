package shenry.tebot.telegramclient.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by shenry on 02.10.2015.
 */
@ToString
@EqualsAndHashCode
public class Message {

    @JsonProperty("message_id")
    @Getter
    @Setter
    private int id;

    @JsonProperty("from")
    @Getter
    @Setter
    private User sender;

    /**
     * Date the message was sent in Unix time
     */
    @JsonProperty("date")
    @Getter
    @Setter
    private int date;

    @JsonProperty("chat")
    @Getter
    @Setter
    private Chat chat;

    /**
     * For forwarded messages, sender of the original message
     */
    @JsonProperty("forward_from")
    @Getter
    @Setter
    private User formardFrom;

    /**
     * For forwarded messages, date the original message was sent in Unix time
     */
    @JsonProperty("forward_date")
    @Getter
    @Setter
    private int forwardDate;

    @JsonProperty("reply_to_message")
    @Getter
    @Setter
    private Message replyToMessgage;

    @JsonProperty("text")
    @Getter
    @Setter
    private String text;

    @JsonProperty("group_chat_created")
    @Getter
    @Setter
    private Boolean groupChatCreated;
}
