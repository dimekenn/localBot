package kz.qbots.entity.standart;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {
    private int    id;
    private long   chatId;
    private String phone;
    private String fullName;
    private String userName;
    private long   CompanyId;
}
