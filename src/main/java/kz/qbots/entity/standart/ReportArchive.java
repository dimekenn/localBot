package kz.qbots.entity.standart;

import lombok.Data;

@Data
public class ReportArchive {
    private int     id;
    private String  text;
    private int     ReportId;
    private String  date;
}
