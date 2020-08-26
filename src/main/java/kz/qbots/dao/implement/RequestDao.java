package kz.qbots.dao.implement;

import kz.qbots.dao.AbstractDao;
import kz.qbots.entity.custom.Request;
import kz.qbots.entity.standart.Report;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RequestDao extends AbstractDao<Request> {

    public void insert(Request request){
        sql = "INSERT INTO PUBLIC.REQUEST(CHAT_ID,FULL_NAME,PHONE_NUMBER) VALUES(?,?,?)";
        getJdbcTemplate().update(sql,setParam(request.getChatId(),request.getFullName(),
                request.getPhoneNumber()));
    }

    public int getLastId(){
        sql = "SELECT MAX(ID) FROM REQUEST";
        return getJdbcTemplate().queryForObject(sql,Integer.class);
    }

    public Request getById(int id){
        sql = "SELECT * FROM REQUEST WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id), this::mapper);
    }


    @Override
    protected Request mapper(ResultSet rs, int index) throws SQLException {
        Request request = new Request();
        request.setId(rs.getInt(1));
        request.setChatId(rs.getLong(2));
        request.setFullName(rs.getString(3));
        request.setPhoneNumber(rs.getString(4));
        return request;
    }
}
