package models;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import utils.DAOField;
import dao.DAO;

public abstract class APIModel {
    @DAOField
    protected Long id;

    protected abstract DAO<? extends APIModel> getDAO();

    public abstract <T extends APIModel> T fromJson(String json);

    public abstract <T extends APIModel> T fromJson(Long id, String json);

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public boolean hasId() {
        return this.id != null;
    }

    public List<String> getColumns() {
        Field[] fields = this.getClass().getDeclaredFields();
        List<String> columns = new ArrayList<String>();

        for (Field f : fields) {
            if (f.isAnnotationPresent(DAOField.class)) {
                columns.add(f.getName());
            }
        }

        return columns;
    }
}
