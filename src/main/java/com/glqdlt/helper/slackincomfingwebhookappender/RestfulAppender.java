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
 * <p>
 * 간단한 슬랙 인커밍 봇에 웹훅 쏴주는 로거
 *
 * @author glqdlt
 */
public class RestfulAppender extends AppenderBase<ILoggingEvent> {

    private final RestTemplateBuilder builder;
    private String webhookUrl;
    private Emoji emoji;

    /**
     * 이모지 설정 관련 처리 자세한 것은 ReadMe 예제 참고
     * <pre>
     *         <emoji>
     *             <code>:pig:</code>
     *         </emoji>
     * </pre>
     *
     * @param emoji
     */
    public void setEmoji(Emoji emoji) {
        this.emoji = emoji;
    }

    public Emoji getEmoji() {
        return emoji;
    }

    /**
     * 웹훅 주소 필수
     * <pre>
     *      <webhookUrl>https://hooks.slack.com/services/...</webhookUrl>
     * </pre>
     *
     * @return
     */
    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public RestfulAppender() {
        this.builder = new RestTemplateBuilder().
                setConnectTimeout(Duration.ofSeconds(30));

    }

    /**
     * emoji list.. 는 아래를 참고.
     *
     * @param event 뭐 다들 아는 로그백에서 던져주는 이벤트 객체
     * @see <a href='https://www.webfx.com/tools/emoji-cheat-sheet/'>https://www.webfx.com/tools/emoji-cheat-sheet/</a>
     */
    @Override
    final protected void append(ILoggingEvent event) {
//        TODO Level 을 속성으로 받아서 처리할수있게?
        if (event.getLevel().equals(Level.ERROR)) {
            try {
                RestTemplate z = builder.build();
                Map<String, String> payload = makePayloads(event.getMessage());
                RequestEntity requestEntity;
                requestEntity = RequestEntity.post(new URI(getWebhookUrl()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(payload);
                z.exchange(requestEntity, String.class);
            } catch (RuntimeException | URISyntaxException e) {
                //    TODO fail 시에 retry 전략 어케 할건가 고민좀
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * 슬랙에 발송 될 메세지 제네레이터
     * <pre>
     *     payloads == {text : {Throwable.getMessage()} , icon_emoji : {Emoji.getCode()}}
     * </pre>
     *
     * @param message ILoggingEvent.getMessage() 에서 던져진 문자열
     * @return
     */
    private Map<String, String> makePayloads(String message) {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("text", message);
        payload.put("icon_emoji", Optional.ofNullable(getEmoji().getCode()).orElse(":ghost:"));
        return payload;
    }

}
