package com.glqdlt.helper.slackincomfingwebhookappender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Date 2019-11-08
 *
 * @author glqdlt
 */
public class RestfulAppender extends AppenderBase<ILoggingEvent> {

    private final RestTemplateBuilder builder;
    private Integer poolSize;
    private String webhookUrl;
    private Emoji emoji;

    public void setEmoji(Emoji emoji) {
        this.emoji = emoji;
    }

    public Emoji getEmoji() {
        return emoji;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public Integer getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(Integer poolSize) {
        this.poolSize = poolSize;
    }

    public RestfulAppender() {
        this.builder = new RestTemplateBuilder().
                setConnectTimeout(Duration.ofSeconds(30));

    }

    /**
     * emoji list.. 는 아래를 참고.
     *
     * @param eventObject
     * @see <a href='https://www.webfx.com/tools/emoji-cheat-sheet/'>https://www.webfx.com/tools/emoji-cheat-sheet/</a>
     */
    @Override
    protected void append(ILoggingEvent eventObject) {
        if (eventObject.getLevel().equals(Level.ERROR)) {
            try {
                RestTemplate z = builder.build();
                Map<String, String> payload = new LinkedHashMap<>();
                payload.put("text", eventObject.getMessage());
                payload.put("icon_emoji", Optional.ofNullable(getEmoji().getCode()).orElse(":ghost:"));
                RequestEntity requestEntity;
                requestEntity = RequestEntity.post(new URI(getWebhookUrl()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(payload);
                z.exchange(requestEntity, String.class);
            } catch (RuntimeException | URISyntaxException e) {
//                  TODO 이거 에러 캐치 어케 되는지 로그백 설계를 좀 봐야할듯
                throw new RuntimeException(e);
            }
        }

    }

}
