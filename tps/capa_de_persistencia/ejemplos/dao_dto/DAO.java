package dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.APIModel;

import org.apache.commons.lang3.text.WordUtils;

import utils.CouldNotSaveException;
import utils.NotFoundException;

public abstract class DAO<T extends APIModel> {
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:"
                + System.getProperty("user.dir") + "/issues.db");
        PreparedStatement stmt = connection
                .prepareStatement("PRAGMA foreign_keys = ON");
        stmt.execute();
        stmt.close();

        return connection;
    }

    protected abstract T parseResults(ResultSet rs) throws SQLException;

    protected PreparedStatement createStatement() throws SQLException {
        return this.createStatement(new ArrayList<String>());
    }

    protected PreparedStatement createStatement(List<String> conditions)
            throws SQLException {
        String sql = "SELECT * FROM " + this.getTable();

        Boolean first = true;
        for (String c : conditions) {
            if (first) {
                sql += " WHERE " + c + " = ?";
                first = false;
                continue;
            }

            sql += " AND " + c + " = ?";
        }
        return this.getConnection().prepareStatement(sql);
    }

    protected T doParseResultsMultiple(ResultSet rs) throws SQLException {
        return this.parseResults(rs);
    }

    protected T doParseResultsSingle(ResultSet rs) throws SQLException {
        return this.parseResults(rs);
    }

    protected List<T> parseMultiple(ResultSet rs) throws SQLException {
        List<T> results = new ArrayList<T>();

        while (rs.next()) {
            results.add(this.doParseResultsMultiple(rs));
        }

        rs.close();

        return results;
    }

    protected T parseSingle(ResultSet rs) throws SQLException {
        T result = this.doParseResultsSingle(rs);

        rs.close();

        return result;
    }

    public List<T> all() {
        try {
            PreparedStatement statement = this.createStatement();
            List<T> objects = this.parseMultiple(statement.executeQuery());
            statement.close();
            return objects;
        } catch (SQLException e) {
            throw new NotFoundException(e);
        }
    }

    public T byId(Long id) {
        try {
            List<String> args = new ArrayList<String>();
            args.add("id");
            PreparedStatement statement = this.createStatement(args);
            statement.setLong(1, id);
            T object = this.parseSingle(statement.executeQuery());
            statement.close();
            return object;
        } catch (SQLException e) {
            throw new NotFoundException(WordUtils.capitalize(this.getTable())
                    + " Not Found", e);
        }
    }

    public abstract String getTable();

    public T save(T model) {
        try {
            if (model.hasId()) {
                return this.doUpdate(model);
            } else {
                return this.doSave(model);
            }
        } catch (SQLException e) {
            throw new CouldNotSaveException(e);
        }
    }

    public void remove(Long id) {
        try {
            PreparedStatement statement = this.getConnection()
                    .prepareStatement(
                            "DELETE FROM " + this.getTable() + " WHERE id = ?");
            statement.setLong(1, id);
            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            throw new NotFoundException(e);
        }
    }

    private T doSave(T model) throws SQLException {
        String sql = "INSERT INTO " + this.getTable() + " "
                + this.buildColumns(model.getColumns()) + " VALUES "
                + this.buildValues(model.getColumns(), model);

        PreparedStatement statement = this.getConnection()
                .prepareStatement(sql);

        statement.executeUpdate();

        Long id = statement.getGeneratedKeys().getLong(1);
        model.setId(id);

        statement.close();

        return model;
    }

    private T doUpdate(T model) throws SQLException {
        PreparedStatement statement = this.getConnection().prepareStatement(
                "UPDATE " + this.getTable() + " SET " + this.buildUpdate(model)
                        + " WHERE id = ?");

        statement.setLong(1, model.getId());

        statement.executeUpdate();

        statement.close();

        return model;
    }

    private String buildValue(String column, T model)
            throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        Field field = this.getAccessibleField(column, model);

        if (field.getType() == boolean.class) {
            return field.getBoolean(model) ? "1" : "0";
        } else if (field.getType() == String.class) {
            Object value = field.get(model);
            if (value == null) {
                return "null";
            } else {
                return "\"" + (String) value + "\"";
            }
        } else if (field.getType() == long.class) {
            return String.valueOf(field.getLong(model));
        }

        throw new RuntimeException("Field type not known");
    }

    private Field getAccessibleField(String column, T model)
            throws NoSuchFieldException, SecurityException {
        Field field = model.getClass().getDeclaredField(column);
        field.setAccessible(true);
        return field;
    }

    private String buildUpdate(T model) {
        String result = "";
        String separator = ", ";

        boolean first = true;
        for (String column : model.getColumns()) {

            try {
                if (this.getAccessibleField(column, model).get(model) != null) {
                    result += (!first ? separator : "") + column + " = "
                            + this.buildValue(column, model);
                    first = false;
                }
            } catch (NoSuchFieldException | IllegalAccessException
                    | RuntimeException e) {
                continue;
            }
        }

        return result;
    }

    private String buildValues(List<String> columns, T model) {
        String result = "(";
        String separator = ", ";

        boolean first = true;
        for (String column : model.getColumns()) {
            try {
                result += (!first ? separator : "")
                        + this.buildValue(column, model);
                first = false;
            } catch (NoSuchFieldException | SecurityException
                    | IllegalArgumentException | IllegalAccessException e) {
                continue;
            }
        }

        result += ")";

        return result;
    }

    private String buildColumns(List<String> columns) {
        String separator = ", ";
        String result = "(";

        boolean first = true;

        for (String c : columns) {
            result += (!first ? separator : "") + c;
            first = false;
        }
        result += ")";

        return result;
    }
}
