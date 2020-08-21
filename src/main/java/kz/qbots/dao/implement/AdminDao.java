package kz.qbots.dao.implement;


import kz.qbots.dao.AbstractDao;
import kz.qbots.entity.standart.Admin;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDao extends AbstractDao<Admin> {

    public boolean isAdmin(long chatId) {
        sql = "SELECT count(*) FROM ADMIN WHERE CHAT_ID = ?";
        int count = getJdbcTemplate().queryForObject(sql, setParam(chatId), Integer.class);
        if (count > 0) return true;
        return false;
    }

    public boolean isMainAdmin(long chatId) {
        sql = "SELECT count(*) FROM ADMIN WHERE CHAT_ID = ?";
        int count = getJdbcTemplate().queryForObject(sql, setParam(chatId), Integer.class);
        if (count > 0) return true;
        return false;
    }

    @Override
    protected Admin mapper(ResultSet rs, int index) throws SQLException {
        Admin admin = new Admin();
        admin.setId(rs.getInt(1));
        admin.setUser_id(rs.getLong(2));
        admin.setComment(rs.getString(3));
        return admin;
    }
}
