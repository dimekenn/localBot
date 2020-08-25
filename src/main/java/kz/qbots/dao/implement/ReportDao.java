package kz.qbots.dao.implement;

import kz.qbots.dao.AbstractDao;
import kz.qbots.entity.standart.Report;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportDao extends AbstractDao<Report> {

    public void insert(Report report){
        sql = "INSERT INTO REPORT(CHAT_ID, TEXT, NAME_OF_CLIENT, AUDIO, PHOTO, VIDEO, ID_STATUS, DATE) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        getJdbcTemplate().update(sql, setParam(report.getChat_id(), report.getText(), report.getNameOfClient(), report.getAudio(), report.getPhoto(), report.getVideo(), report.getIdStatus(), report.getDate()));
    }

    public void update(Report report){
        sql = "UPDATE REPORT SET CHAT_ID_MANAGER = ?, CHAT_ID_DEV = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, report.getChatIdManager(), report.getChatIdDeveloper(), report.getId());
    }

    public void updateDev(Report report){
        sql = "UPDATE REPORT SET CHAT_ID_DEV = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, report.getChatIdDeveloper(), report.getId());
    }

    public void updateSM(Report report){
        sql = "UPDATE REPORT SET CHAT_ID_MANAGER = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, report.getChatIdManager(), report.getId());
    }

    public Report getById(int id){
        sql = "SELECT * FROM REPORT WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id), this::mapper);
    }

    public int getLastId(){
        sql = "SELECT MAX(ID) FROM REPORT";
        return getJdbcTemplate().queryForObject(sql,Integer.class);
    }

    public void updateStatus(int id, int statusId) {
        sql = "UPDATE REPORT SET ID_STATUS = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, statusId, id);
    }

    @Override
    protected Report mapper(ResultSet rs, int index) throws SQLException {
        Report report = new Report();
        report.setId(rs.getInt(1));
        report.setChat_id(rs.getLong(2));
        report.setText(rs.getString(3));
        report.setNameOfClient(rs.getString(4));
        report.setAudio(rs.getString(5));
        report.setPhoto(rs.getString(6));
        report.setVideo(rs.getString(7));
        report.setChatIdManager(rs.getLong(8));
        report.setChatIdDeveloper(rs.getLong(9));
        report.setIdStatus(rs.getInt(10));
        report.setDate(rs.getDate(11));
        return report;
    }
}
