package models;

import utils.DAOField;

import com.google.gson.Gson;

import dao.DAO;
import dao.StatusDAO;

public class Status extends APIModel {
    @DAOField
    private String name;

    @Override
    protected DAO<Status> getDAO() {
        return new StatusDAO();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Status fromJson(String json) {
        return new Gson().fromJson(json, Status.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Status fromJson(Long id, String json) {
        Status status = new Gson().fromJson(json, Status.class);
        status.setId(id);

        return status;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
