package com.adg.api.department.Accounting.service;

import com.adg.api.department.Accounting.enums.MisaModel;
import com.adg.api.department.Accounting.enums.MisaOrderType;
import com.adg.api.department.Accounting.enums.Module;
import com.adg.api.department.Accounting.enums.SlackAuthor;
import com.adg.api.department.Accounting.model.MisaPayload;
import com.adg.api.department.Accounting.model.MisaSyncDTO;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.JsonUtils;
import com.merlin.asset.core.utils.MapUtils;
import com.merlin.asset.core.utils.ParserUtils;
import com.merlin.mapper.MerlinMapper;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.11 01:28
 */
@Log4j2
public abstract class AbstractMisaService<T extends AbstractDTO, E, ID, M extends MerlinMapper<E>, R extends JpaRepository<E, ID>> {

    protected final int PAGE_SIZE = 50;

    protected M mapper;
    protected R repository;

    public AbstractMisaService(M mapper, R repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @Autowired
    private SlackService slackService;

    @Autowired
    protected MisaWebClientService misaWebClientService;

    protected MisaPayload callMisaRestApi(int page) {
        return MisaPayload.toPayload(this.misaWebClientService.get((uriBuilder -> uriBuilder
                .path(this.getMisaModel().uri)
                .queryParam("page", ParserUtils.toString(page))
                .queryParam("pageSize", ParserUtils.toString(PAGE_SIZE))
                .queryParam("orderBy", this.getMisaModel().orderByField)
                .build()
        )));
    }

    protected Map<String, Object> fetch() {
        List<Map<String, Object>> misaStoringStatistics = new ArrayList<>();
        MisaPayload payload = this.callMisaRestApi(1);
        int totalPages = payload.getTotalPages();
        int totalRecords = payload.getTotalRecords();
        int currentPage = this.getMisaModel().orderType == MisaOrderType.ASC
                ? 1
                : totalPages;
        log.info("Starting to fetch/store data. Start page: {}", currentPage);
        long t1 = System.currentTimeMillis();
        switch (this.getMisaModel().orderType) {
            case ASC: {
                do {

                    Map<String, Object> misaStoringStatistic = this.handleLoop(currentPage);
                    misaStoringStatistics.add(misaStoringStatistic);

                    currentPage++;
                } while (currentPage <= totalPages);
                break;
            } case DESC: {
                do {
                    Map<String, Object> misaStoringStatistic = this.handleLoop(currentPage);
                    misaStoringStatistics.add(misaStoringStatistic);

                    currentPage--;
                } while (currentPage > 0);
                break;
            }
        }
        log.info("Fetch/store data done. Total duration {}", DateTimeUtils.getRunningTimeInSecond(t1));

        return MapUtils.ImmutableMap()
                .put("totalRecords", totalRecords)
                .put("totalPages", totalPages)
                .put("stats", misaStoringStatistics)
                .build();
    }

    @SneakyThrows
    protected Map<String, Object> fetchV2() {

        final ExecutorService executorService = Executors.newFixedThreadPool(2);

        List<Map<String, Object>> misaStoringStatistics = new ArrayList<>();
        MisaPayload payload = this.callMisaRestApi(1);
        int totalPages = payload.getTotalPages();
        int totalRecords = payload.getTotalRecords();
        int currentPage = this.getMisaModel().orderType == MisaOrderType.ASC
                ? 1
                : totalPages;
        log.info("Starting to fetch/store data. Start page: {}", currentPage);
        long t1 = System.currentTimeMillis();
        boolean isFinish = true;
        List<Future> futures = new ArrayList<>();
        while (isFinish) {
            int finalCurrentPage = currentPage;
            Future future = executorService.submit(() -> {
                Map<String, Object> misaStoringStatistic = this.handleLoop(finalCurrentPage);
                misaStoringStatistics.add(misaStoringStatistic);
            });
            futures.add(future);

            if (this.getMisaModel().orderType == MisaOrderType.ASC) {
                currentPage++;
                isFinish = currentPage > totalPages;
            } else {
                currentPage--;
                isFinish = currentPage == totalPages;
            }
        }

        long t2 = System.currentTimeMillis();

        while (!futures.stream().allMatch(Future::isDone)) {
            Thread.sleep(500);
            log.info("Wait for {}", DateTimeUtils.getRunningTimeInSecond(t2));
        }

        executorService.shutdown();

        log.info("Fetch/store data done. Total duration {}", DateTimeUtils.getRunningTimeInSecond(t1));
        return MapUtils.ImmutableMap()
                .put("totalRecords", totalRecords)
                .put("totalPages", totalPages)
                .put("fullFlowDuration", DateTimeUtils.getRunningTimeInSecond(t1))
                .put("stats", misaStoringStatistics)
                .build();
    }

    public Map<String, Object> handleLoop( int currentPage) {
        long t1 = System.currentTimeMillis();
        log.info("Starting to fetch/store page {}", currentPage);
        MisaPayload currentPayload = this.callMisaRestApi(currentPage);
        long t2 = System.currentTimeMillis();
        Map<String, Object> misaStoringStatistic = this.save(currentPayload);
        log.info("Page {} stats. Fetch duration: {}. Store duration: {}", currentPage, DateTimeUtils.getRunningTimeInSecond(t2, t1), DateTimeUtils.getRunningTimeInSecond(t2));

        return MapUtils.ImmutableMap()
                .putAll(misaStoringStatistic)
                .put("apiCallingDuration", t2 - t1)
                .put("page", currentPage)
                .build();
    }

    public void sync(MisaSyncDTO misaSyncDTO) {
        Map<String, Object> statistic = this.fetchV2();
        this.sendSlack(statistic, misaSyncDTO);
    }

    private void sendSlack(Map<String, Object> statistic, MisaSyncDTO misaSyncDTO) {

        int totalRecords = MapUtils.getInt(statistic, "totalRecords");
        int totalPages = MapUtils.getInt(statistic, "totalPages");
        List<Map<String, Object>> stats = MapUtils.getListMapStringObject(statistic, "stats");
        int processedRecords = 0;
        int totalStoringDuration = 0;
        int totalApiCallingDuration = 0;

        int maxStoringDuration = 0;
        int maxApiCallingDuration = 0;
        int maxStoringDurationPage = 1;
        int maxApiCallingDurationPage = 1;
        for (Map<String, Object> stat : stats) {
            int storingDuration =  MapUtils.getInt(stat, "storingTime");
            int apiCallingDuration = MapUtils.getInt(stat, "apiCallingDuration");
            int currentPage = MapUtils.getInt(stat, "currentPage");
            processedRecords += MapUtils.getInt(stat, "savedRecordCounts");
            totalStoringDuration += MapUtils.getInt(stat, "storingTime");
            totalApiCallingDuration += MapUtils.getInt(stat, "apiCallingDuration");

            maxStoringDuration = Math.max(maxStoringDuration, storingDuration);
            maxApiCallingDuration = Math.max(maxApiCallingDuration, apiCallingDuration);
            if (maxStoringDuration == storingDuration) maxStoringDurationPage = currentPage;
            if (maxApiCallingDuration == apiCallingDuration) maxApiCallingDurationPage = currentPage;

        }

        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.UP);
        double avgStoringDuration = stats.size() == 0
                ? 0
                : totalStoringDuration / (stats.size() * 1.0);

        double avgApiCallingDuration = stats.size() == 0
                ? 0
                : totalApiCallingDuration / (stats.size() * 1.0);


        String message = new StringBuilder()
                .append("Request body:")
                .append(String.format("```%s```", JsonUtils.toJson(misaSyncDTO))).append("\n")
                .append("Statistic Info:")
                .append("```").append("\n")
                .append("---- GENERAL STATS").append("\n")
                .append(String.format("Processed/Total records: %s/%s", processedRecords, totalRecords)).append("\n")
                .append(String.format("Processed/Total pages: %s/%s", stats.size(), totalPages)).append("\n")
                .append(String.format("Success rate: %s%%", Math.round(processedRecords / (totalRecords * 1.0) * 100))).append("\n")
                .append(String.format("Full flow duration: %s%%", MapUtils.getString(statistic,"fullFlowDuration"))).append("\n").append("\n")
                .append("---- MISA API CALLING STATS").append("\n")
                .append(String.format("Total duration: %s seconds", df.format(totalApiCallingDuration / 1000.0) )).append("\n")
                .append(String.format("Average duration: %s seconds / page", df.format(avgApiCallingDuration / 1000.0))).append("\n")
                .append(String.format("Longest duration: %s seconds (page %s)", df.format(maxApiCallingDuration / 1000.0), maxApiCallingDurationPage)).append("\n").append("\n")
                .append("---- STORING DB STATS").append("\n")
                .append(String.format("Total duration: %s seconds", df.format(totalStoringDuration / 1000.0) )).append("\n")
                .append(String.format("Average duration: %s seconds / page", df.format(avgStoringDuration / 1000.0) )).append("\n")
                .append(String.format("Longest duration: %s seconds (page %s)", df.format(maxStoringDuration / 1000.0), maxStoringDurationPage)).append("\n")
                .append("```")
                .toString();

        this.slackService.sendReport(
                Module.SYNC_MISA_API,
                SlackAuthor.LUAN_PHAN,
                DateTimeUtils.convertZonedDateTimeToFormat(DateTimeUtils.fromEpochMilli(misaSyncDTO.getId(), "Asia/Ho_Chi_Minh"), "Asia/Ho_Chi_Minh", DateTimeUtils.FMT_01),
                String.format("%s processing has been done",
                        this.getMisaModel().name()),
                message
        );
    }

    protected Map<String, Object> save(MisaPayload payload) {
        long t1 = System.currentTimeMillis();
        List<T> dtos = this.parsePayload(payload);
        List<E> entities = dtos.stream().map(dto -> this.mapper.toEntity(dto)).collect(Collectors.toList());
        List<E> savedEntities = this.repository.saveAll(entities);
        return MapUtils.ImmutableMap()
                .put("recordCounts", entities.size())
                .put("savedRecordCounts", savedEntities.size())
                .put("storingTime", (System.currentTimeMillis() - t1))
                .build();
    }

    private List<T> parsePayload(MisaPayload payload) {
        List<Map<String, Object>> data = payload.getData();
        return data.stream().map(item -> JsonUtils.fromJson(JsonUtils.toJson(item), this.getDtoClass())).collect(Collectors.toList());
    }

    protected Class<T> getDtoClass() {
        return (Class<T>) this.getMisaModel().dtoClass;
    }

    protected abstract MisaModel getMisaModel();

}
