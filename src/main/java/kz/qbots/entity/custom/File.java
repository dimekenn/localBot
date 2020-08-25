package kz.qbots.entity.custom;

import lombok.Data;

@Data
public class File {
    private int id;
    private String img;
    private String doc;
    private String audio;
    private String video;
    private int idReport;
    private boolean done;
}
