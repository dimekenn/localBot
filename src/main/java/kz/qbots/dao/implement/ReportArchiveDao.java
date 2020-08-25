package kz.qbots.dao.implement;

import kz.qbots.dao.AbstractDao;
import kz.qbots.entity.standart.ReportArchive;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ReportArchiveDao extends AbstractDao<ReportArchive> {

    public void insert(String text, int reportId) {
        sql = "INSERT INTO REPORT_ARCHIVE (TEXT, REPORT_ID) VALUES (?,?)";
        getJdbcTemplate().update(sql, text, reportId);
    }

    public void insert(String text, int reportId, String date) {
        sql = "INSERT INTO REPORT_ARCHIVE (TEXT, REPORT_ID, DATE) VALUES (?,?,?)";
        getJdbcTemplate().update(sql, text, reportId, date);
    }

    public List<ReportArchive> getReportList(int id) {
        sql = "SELECT * FROM REPORT_ARCHIVE WHERE REPORT_ID = ?";
        return getJdbcTemplate().query(sql, setParam(id), this::mapper);
    }

    public ReportArchive getReportArchive(int id) {
        sql = "SELECT * FROM REPORT_ARCHIVE WHERE REPORT_ID = ?";
        try {
            return getJdbcTemplate().queryForObject(sql, setParam(id), this::mapper);
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect result size: expected 1, actual 0")) return null;
            return null;
        }
    }



    @Override
    protected ReportArchive mapper(ResultSet rs, int index) throws SQLException {
        ReportArchive reportArchive = new ReportArchive();
        reportArchive.setId(rs.getInt(1));
        reportArchive.setText(rs.getString(2));
        reportArchive.setReportId(rs.getInt(3));
        reportArchive.setDate(rs.getString(4));
        return null;
    }
}
