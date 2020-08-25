package kz.qbots.dao.implement;

import kz.qbots.dao.AbstractDao;
import kz.qbots.entity.custom.File;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FileDao extends AbstractDao<File> {

    public void insertDoc(String path, int taskId) {
        sql = "INSERT INTO FILE (DOC, REPORT_ID) VALUES(? ,?)";
        getJdbcTemplate().update(sql, path, taskId);
    }

    public void insertAudio(String path, int taskId) {
        sql = "INSERT INTO FILE (AUDIO, REPORT_ID) VALUES(? ,?)";
        getJdbcTemplate().update(sql, path, taskId);
    }

    public void insertImg(String path, int taskId) {
        sql = "INSERT INTO FILE (IMG, REPORT_ID) VALUES (?,?)";
        getJdbcTemplate().update(sql, path, taskId);
    }

    public void insertVideo(String path, int taskId) {
        sql = "INSERT INTO FILE (VIDEO, REPORT_ID) VALUES (?,?)";
        getJdbcTemplate().update(sql, path, taskId);
    }

    public void updateStatus(int taskId, boolean idStatus) {
        sql = "UPDATE FILE SET DONE = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, idStatus, taskId);
    }

    public List<File> getFilesList(int taskId) {
        sql = "SELECT * FROM FILE WHERE REPORT_ID = ?";
        return getJdbcTemplate().query(sql, setParam(taskId), this::mapper);
    }

    @Override
    protected File mapper(ResultSet rs, int index) throws SQLException {
        File file = new File();
        file.setId(rs.getInt(1));
        file.setImg(rs.getString(2));
        file.setDoc(rs.getString(3));
        file.setAudio(rs.getString(4));
        file.setVideo(rs.getString(5));
        file.setIdReport(rs.getInt(6));
        file.setDone(rs.getBoolean(7));
        return file;
    }
}
