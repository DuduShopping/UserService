package com.dudu.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chaojiewang on 1/29/18.
 */
public class DatabaseHelper {
    private static final Logger logger = LogManager.getLogger(DatabaseHelper.class);

    public static DatabaseHelper getHelper() {
        return helper;
    }

    public static void setHelper(DatabaseHelper helper) {
        DatabaseHelper.helper = helper;
    }

    private static DatabaseHelper helper = new DatabaseHelper();

    private DatabaseHelper() {}

    public List<ZetaMap> execToZetaMaps(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            ArrayList<ZetaMap> maps = new ArrayList<>();
            while (rs.next())
                maps.add(new ZetaMap(rs));

            return maps;
        }
    }

    public List<ZetaMap> execToZetaMaps(Connection con, String sql, Object... parameters) throws SQLException {
        logger.info("execToZetaMap: " + sql);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 1; i <= parameters.length; i++) {
                Object param = parameters[i-1];
                if (param instanceof Character)
                    ps.setObject(i, param.toString());
                else if (param instanceof Date)
                    ps.setObject(i, new Timestamp(((Date) param).getTime()));
                else
                    ps.setObject(i, param);
            }

            return execToZetaMaps(ps);
        }
    }

    public boolean execAndCheckNotEmpty(Connection con, String sql, Object... parameters) throws SQLException {
        List<ZetaMap> zetaMapList = execToZetaMaps(con, sql, parameters);
        return zetaMapList.size() != 0;
    }

    public List<ZetaMap> execToZetaMaps(Connection con, String sql) throws SQLException {
        return execToZetaMaps(con, sql, new Object[]{});
    }

    /**
     * must be an SQL Data Manipulation Language (DML) statement, such as <code>INSERT</code>, <code>UPDATE</code> or
     * <code>DELETE</code>; or an SQL statement that returns nothing,
     * such as a DDL statement.
     *
     * @param con
     * @param sql
     * @param parameters
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
     *         or (2) 0 for SQL statements that return nothing
     * @throws SQLException
     */
    public int execUpdate(Connection con, String sql, Object... parameters) throws SQLException {
        logger.info("execUpdate: " + sql);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 1; i <= parameters.length; i++) {
                Object param = parameters[i-1];
                if (param instanceof Character)
                    ps.setObject(i, param.toString());
                else if (param instanceof Date)
                    ps.setObject(i, new Timestamp(((Date) param).getTime()));
                else
                    ps.setObject(i, param);
            }

            return ps.executeUpdate();
        }
    }

    /**
     * must be an SQL Data Manipulation Language (DML) statement, such as <code>INSERT</code>, <code>UPDATE</code> or
     * <code>DELETE</code>; or an SQL statement that returns nothing,
     * such as a DDL statement.
     *
     * @param ps
     * @return
     * @throws SQLException
     */
    public int execUpdate(PreparedStatement ps) throws SQLException {
        return ps.executeUpdate();
    }

    /**
     *
     * @param con
     * @param sql
     * @param generatedKeys
     * @param parameters
     * @return
     * @throws SQLException
     */
    public List<ZetaMap> execUpdateToZetaMaps(Connection con, String sql, String[] generatedKeys, Object... parameters) throws SQLException {
        logger.info("execUpdate: " + sql);
        try (PreparedStatement ps = con.prepareStatement(sql, generatedKeys)) {
            for (int i = 1; i <= parameters.length; i++) {
                Object param = parameters[i-1];
                if (param instanceof Character)
                    ps.setObject(i, param.toString());
                else if (param instanceof Date)
                    ps.setObject(i, new Timestamp(((Date) param).getTime()));
                else
                    ps.setObject(i, param);
            }

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            List<ZetaMap> zetaMaps = new ArrayList<>();
            while (rs.next()) {
                ZetaMap map = new ZetaMap();
                for (int i = 1; i <= generatedKeys.length; i++)
                    map.put(generatedKeys[i-1], rs.getObject(i));
                zetaMaps.add(map);
            }

            return zetaMaps;
        }
    }
}
