package com.adg.api.department.Accounting.service;

import com.adg.api.department.Accounting.enums.Module;
import com.adg.api.department.Accounting.enums.SlackAuthor;
import com.merlin.asset.core.utils.JsonUtils;
import com.merlin.asset.core.utils.SlackUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.22 15:38
 */
@Service
public class SlackService {

    private final static Logger logger = LoggerFactory.getLogger(SlackService.class);
    private WebClient webClient;

    private static final String SLACK_DOMAIN = "https://hooks.slack.com";
    private static final String REPORT_URI = "services/T034C0KDVHD/B037A3TTXK9/RXaLYCgt14cf7ebNsmEysgJp";
    private static final String NOTIFICATION_URI = "services/T034C0KDVHD/B037A2YEYBH/5fhQqrxAAZbYQF9plTnGMzgu";

    @PostConstruct
    public void init() {
        HttpClient httpClient = HttpClient
                .create()
                .responseTimeout(Duration.ofSeconds(30));

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(SLACK_DOMAIN)
                .build();
    }

    public void sendReport(Module module, SlackAuthor slackAuthor, String logId, String title, String message) {
        Map<String, Object> body = SlackUtils.buildBody(module.name(), slackAuthor.id, logId, title, message);

        this.webClient
                .post()
                .uri(REPORT_URI)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(JsonUtils.toJson(body)), String.class)
                .retrieve()
                .bodyToMono(String.class).block();
    }

    public void sendNotification(Module module, SlackAuthor slackAuthor, String logId, String title, String message) {

        Map<String, Object> body = SlackUtils.buildBody(module.name(), slackAuthor.id, logId, title, message);

        this.webClient
                .post()
                .uri(NOTIFICATION_URI)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(JsonUtils.toJson(body)), String.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
