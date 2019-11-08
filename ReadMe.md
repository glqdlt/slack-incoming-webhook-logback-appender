# slack webhook appender

## Intro

에러 캐치해서 슬랙에 쏴주려고 만들어봄.

스레드 풀을 할당해서 전송하는 풀을 만들어야함

## Configuration

### async example
logback.xml
```xml
<configuration>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!-- encoders are assigned the type
             ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="REST" class="com.glqdlt.pm6.webcms.logging.RestfulAppender">
        <webhookUrl>https://hooks.slack.com/services/...</webhookUrl>
        <emoji>
            <code>:pig:</code>    
        </emoji>
    </appender>


    <appender name="ASYNC_REST"
        class="ch.qos.logback.classic.AsyncAppender">
        <discardingThreshold>0</discardingThreshold>
        <appender-ref ref="REST" />
    </appender>

    <root level="info">
        <appender-ref ref="STDOUT" />
        <appender-ref ref="ASYNC_REST"/>
    </root>
</configuration>
```

### sync example
logback.xml
```xml
<configuration>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!-- encoders are assigned the type
             ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="REST" class="com.glqdlt.pm6.webcms.logging.RestfulAppender">
        <webhookUrl>https://hooks.slack.com/services/...</webhookUrl>
        <emoji>
            <code>:pig:</code>    
        </emoji>
    </appender>

    <root level="info">
        <appender-ref ref="STDOUT" />
        <appender-ref ref="REST"/>
    </root>
</configuration>
```