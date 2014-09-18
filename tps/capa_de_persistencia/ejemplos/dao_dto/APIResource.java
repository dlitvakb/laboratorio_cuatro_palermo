package resources;

import java.util.HashMap;
import java.util.Map;

import models.APIModel;
import utils.CouldNotSaveException;
import utils.NotFoundException;
import dao.DAO;

public abstract class APIResource<T extends APIModel> {
    protected abstract DAO<T> getDAO();

    public Map<String, Object> all() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", 200);
        map.put("data", this.getDAO().all());
        return map;
    }

    public Map<String, Object> fetch(Long id) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            map.put("data", this.getDAO().byId(id));
            map.put("code", 200);
        } catch (NotFoundException e) {
            map.put("message", e.getMessage());
            map.put("code", 404);
        }
        return map;
    }

    public Map<String, Object> update(T model) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            map.put("data", this.getDAO().save(model));
            map.put("code", 200);
        } catch (NotFoundException e) {
            map.put("code", 404);
            map.put("message", e.getMessage());
        }
        return map;
    }

    public Map<String, Object> save(T model) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            map.put("data", this.getDAO().save(model));
            map.put("code", 201);
        } catch (CouldNotSaveException e) {
            map.put("code", 409);
            map.put("message", "Could not save " + this.getDAO().getTable());
        }
        return map;
    }

    public Map<String, Object> remove(Long id) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            this.getDAO().remove(id);
            map.put("code", 200);
        } catch (NotFoundException e) {
            map.put("message", e.getMessage());
            map.put("code", 500);
        }

        return map;
    }
}
