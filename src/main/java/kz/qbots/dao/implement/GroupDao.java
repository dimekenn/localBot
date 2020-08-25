package kz.qbots.dao.implement;


import kz.qbots.dao.AbstractDao;
import kz.qbots.entity.custom.Group;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GroupDao extends AbstractDao<Group> {

    public List<Group> getGroups() {
        sql = "SELECT  * FROM GROUPS ORDER BY ID";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public Group getGroupToId(int id) {
        sql = "SELECT * FROM GROUPS WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id), this::mapper);
    }

    public boolean isExist(long chatId) {
        sql = "SELECT COUNT (CHAT_ID) FROM GROUPS WHERE CHAT_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(chatId), Integer.class) > 0;
    }

    public void update(Group g) {
        sql = "UPDATE GROUPS SET NAME = ? , CHAT_ID = ? , GROUP_NAME = ?  WHERE CHAT_ID = ?";
        getJdbcTemplate().update(sql, g.getNames(), g.getChatId(), g.getUserName(), g.getChatId());
    }

    public void insert(Group group) {
        sql = "INSERT INTO GROUPS(NAME,CHAT_ID,GROUP_NAME)VALUES (?,?,?)";
        getJdbcTemplate().update(sql, group.getNames(), group.getChatId(), group.getUserName());
    }

    public Group getGroupToChatId(long chatId) {
        sql = "SELECT * FROM GROUPS WHERE CHAT_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(chatId), this::mapper);
    }

    @Override
    protected Group mapper(ResultSet rs, int index) throws SQLException {
        Group entity = new Group();
        entity.setId(rs.getInt(1));
        entity.setNames(rs.getString(2));
        entity.setChatId(rs.getLong(3));
        entity.setUserName(rs.getString(4));
        return entity;
    }
}
