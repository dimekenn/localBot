package kz.qbots.entity.standart;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Keyboard {
    private int     id;
    private String  button_ids;
    private boolean inline;
    private String  comment;
}
