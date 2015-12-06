package shenry.tebot.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
public class SendMessageRequest {
    /**
     * Unique identifier for the target chat or username of the target channel
     * (in the format @channelusername
     */
    @JsonProperty("chat_id")
    @Getter
    @Setter
    private String chatId;

    /**
     * Text of the message to be sent
     */
    @JsonProperty("text")
    @Getter
    @Setter
    private String text;

    /**
     * (Optional) Send Markdown, if you want Telegram apps to show bold, italic
     * and inline URLs in your bot's message
     */
    @JsonProperty("parse_mode")
    @Getter
    @Setter
    private String parseMode;

    /**
     * (Optional) Disables link previews for links in this message
     */
    @JsonProperty("disable_web_page_preview")
    @Getter
    @Setter
    private Boolean disableWebPagePreview;

    /**
     * (Optional) If the message is a reply, ID of the original message
     */
    @JsonProperty("reply_to_message_id")
    @Getter
    @Setter
    private Integer replyToMessageId;
}
