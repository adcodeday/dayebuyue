package org.lu.zhaodazi.websocket.domain;

import lombok.Data;

@Data
public class WSBaseReq {
    private Integer type;

    private String data;
}