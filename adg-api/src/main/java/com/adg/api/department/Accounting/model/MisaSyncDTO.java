package com.adg.api.department.Accounting.model;

import com.adg.api.department.Accounting.enums.MisaModel;
import lombok.Data;

import java.util.List;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.21 22:02
 */
@Data
public class MisaSyncDTO {

    private long id;
    private List<MisaModel> models;

    public MisaSyncDTO() {
        this.id = System.currentTimeMillis();
    }

}
