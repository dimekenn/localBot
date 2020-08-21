package kz.qbots.dao.implement;


import kz.qbots.dao.AbstractDao;
import kz.qbots.entity.standart.Language;
import kz.qbots.entity.standart.LanguageUser;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LanguageUserDao extends AbstractDao<LanguageUser> {

    public void insertOrUpdate(LanguageUser e) {
        if (isRegistered(e.getChatId())) {
            update(e);
        } else {
            insert(e);
        }
    }

    private void insert(LanguageUser e) {
        sql = "INSERT INTO PUBLIC.lang_user (chat_id, lang_id) VALUES (?, ?)";
        getJdbcTemplate().update(sql, e.getChatId(), e.getLanguage().getId());
    }

    private void update(LanguageUser e) {
        sql = "UPDATE PUBLIC.lang_user SET lang_id = ? WHERE chat_id = ?";
        getJdbcTemplate().update(sql, e.getLanguage().getId(), e.getChatId());
    }

    public LanguageUser getByChatId(long chatId) {
        sql = "SELECT * FROM PUBLIC.lang_user WHERE chat_id = ?";
        LanguageUser langUser = null;
        try {
            langUser = getJdbcTemplate().queryForObject(sql, setParam(chatId), this::mapper);
        } catch (Exception e) {
        }
        return langUser;
    }

    public boolean isRegistered(long chatId) {
        sql = "SELECT COUNT(*) FROM PUBLIC.lang_user WHERE chat_id = ?";
        if (getJdbcTemplate().queryForObject(sql, setParam(chatId), Integer.class) != 0) {
            return true;
        }
        return false;
    }

    @Override
    protected LanguageUser mapper(ResultSet rs, int i) throws SQLException {
        LanguageUser entity = new LanguageUser();
        entity.setChatId(rs.getLong(1));
        entity.setLanguage(Language.getById(rs.getInt(2)));
        return entity;
    }
}
