package de.htwsaar.demo;

import java.util.HashMap;
import java.util.Map;

/**
 * Database simulator
 */
public class Database {
    private static Database dummyDB;

    private final Map<String, String> container;

    /**
     * Initializes the dummy database
     */
    private Database() {
        this.container = new HashMap<>();
    }

    /**
     * get the dummy database instance
     *
     * @return Database
     */
    public static Database getInstance() {
        if(Database.dummyDB == null) {
            Database.dummyDB = new Database();
        }

        return Database.dummyDB;
    }

    /**
     * Emulates an SQL insert operation
     *
     * @param key Primary Key
     * @param val Field
     * @return Success
     */
    public boolean insert(String key, String val) {
        if(this.container.get(key) != null) {
            return false;
        }

        this.container.put(key, val);
        return true;
    }

    /**
     * Emulates an SQL update operation
     *
     * @param key Primary Key
     * @param val Field
     * @return Success
     */
    public boolean update(String key, String val) {
        if(this.container.get(key) == null) {
            return false;
        }

        this.container.put(key, val);
        return true;
    }

    /**
     * Emulates an SQL delete operation
     *
     * @param key Primary Key
     * @return Success
     */
    public boolean delete(String key) {
        if(this.container.get(key) == null) {
            return false;
        }

        this.container.remove(key);
        return true;
    }

    /**
     * Emulates an SQL select operation
     *
     * @return Success
     */
    public Map<String, String> select() {
        return new HashMap<>(this.container);
    }

    /**
     * Emulates an SQL select operation
     *
     * @param key Select one row
     * @return Success
     */
    public Map<String, String> select(String key) {
        Map<String, String> resultSet = new HashMap<>();
        String val = this.container.get(key);
        if(val != null) {
            resultSet.put(key, val);
        }
        return resultSet;
    }

}
