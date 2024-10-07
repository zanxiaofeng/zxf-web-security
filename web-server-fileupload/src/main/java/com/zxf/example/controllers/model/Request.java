package com.zxf.example.controllers.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Request {
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String content;
}
