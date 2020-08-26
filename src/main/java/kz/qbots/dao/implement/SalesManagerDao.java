package kz.qbots.dao.implement;

import kz.qbots.dao.AbstractDao;
import kz.qbots.entity.standart.SalesManager;
import kz.qbots.entity.standart.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SalesManagerDao extends AbstractDao<SalesManager> {

    public void insert(SalesManager salesManager){
        sql = "INSERT INTO SALES_MANAGER(CHAT_ID,FULL_NAME,USER_NAME,PHONE_NUMBER) VALUES(?,?,?,?)";
        getJdbcTemplate().update(sql,setParam(salesManager.getChatId(),salesManager.getFullName(),salesManager.getUserName(),
                salesManager.getPhone()));
    }

    public boolean isRegistered(long chatId) {
        sql = "SELECT count(*) FROM SALES_MANAGER WHERE CHAT_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(chatId), Integer.class) > 0;
    }

    public SalesManager getSMByChatId(long chatId) {
        sql = "SELECT * FROM SALES_MANAGER WHERE CHAT_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(chatId), this::mapper);
    }

    @Override
    protected SalesManager mapper(ResultSet rs, int index) throws SQLException {
        SalesManager salesManager = new SalesManager();
        salesManager.setId(rs.getInt(1));
        salesManager.setChatId(rs.getLong(2));
        salesManager.setFullName(rs.getString(3));
        salesManager.setUserName(rs.getString(4));
        salesManager.setPhone(rs.getString(5));
        return salesManager;
    }
}
