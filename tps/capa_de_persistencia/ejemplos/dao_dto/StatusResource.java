package resources;

import models.Status;
import dao.DAO;
import dao.StatusDAO;

public class StatusResource extends APIResource<Status> {
    @Override
    protected DAO<Status> getDAO() {
        return new StatusDAO();
    }
}
