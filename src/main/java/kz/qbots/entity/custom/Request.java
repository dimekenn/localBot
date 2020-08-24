package kz.qbots.entity.custom;

import lombok.Data;

@Data
public class Request {
    private int id;
    private long chatId;
    private String fullName;
    private String phoneNumber;
}
