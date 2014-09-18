package dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import models.Status;

public class StatusDAO extends DAO<Status> {
    @Override
    protected Status parseResults(ResultSet rs) throws SQLException {
        Status status = new Status();
        status.setId(rs.getLong("id"));
        status.setName(rs.getString("name"));

        return status;
    }

    @Override
    public String getTable() {
        return "status";
    }
}
