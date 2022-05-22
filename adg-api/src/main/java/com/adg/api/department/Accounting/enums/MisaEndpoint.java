package com.adg.api.department.Accounting.enums;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.09 16:41
 */
public enum MisaEndpoint {

    ACCOUNT("/account");
    public final String uri;

    MisaEndpoint(String uri) {
        this.uri = uri;
    }
}
