package org.lu.zhaodazi.websocket.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WSBaseResp<T> {
    private Integer type;
    private T data;
}