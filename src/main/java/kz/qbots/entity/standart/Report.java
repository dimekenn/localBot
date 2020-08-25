package kz.qbots.entity.standart;

import lombok.Data;

import java.util.Date;

@Data
public class Report {
    private int id;
    private long chat_id;
    private String nameOfClient;
    private String text;
    private String audio;
    private String photo;
    private String video;
    private long chatIdManager;
    private long chatIdDeveloper;
    private int idStatus;
    private Date date;
}
