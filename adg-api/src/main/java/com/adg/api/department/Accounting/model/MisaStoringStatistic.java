package com.adg.api.department.Accounting.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.21 23:01
 */
@Data
@Builder
public class MisaStoringStatistic {

    private int page;
    private long runningTime; // ms
    private int recordCounts;
    private int savedRecordCounts;

}
