package kz.qbots.dao.implement;

import kz.qbots.dao.AbstractDao;
import kz.qbots.entity.standart.Developer;
import kz.qbots.entity.standart.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DeveloperDao extends AbstractDao<Developer> {

    public Developer getDevByChatId(long chatId) {
        sql = "SELECT * FROM DEVELOPER WHERE CHAT_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(chatId), this::mapper);
    }

    public boolean isRegistered(long chatId) {
        sql = "SELECT count(*) FROM DEVELOPER WHERE CHAT_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(chatId), Integer.class) > 0;
    }

    public void insert(Developer developer){
        sql = "INSERT INTO DEVELOPER(CHAT_ID,FULL_NAME,USER_NAME) VALUES(?,?,?)";
        getJdbcTemplate().update(sql,setParam(developer.getChatId(),developer.getFullName(),developer.getUserName()));
    }

    @Override
    protected Developer mapper(ResultSet rs, int index) throws SQLException {
        Developer developer = new Developer();
        developer.setId(rs.getInt(1));
        developer.setChatId(rs.getLong(2));
        developer.setFullName(rs.getString(3));
        developer.setUserName(rs.getString(4));
        return developer;
    }
}
