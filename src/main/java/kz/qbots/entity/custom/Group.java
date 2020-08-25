package kz.qbots.entity.custom;

import lombok.Data;

@Data
public class Group {

    private int     id;
    private String  names;
    private long    chatId;
    private String  userName;
}
