package indrocraft.utils.sqlUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import indrocraft.utils.sqlUtils.SQLLogger.Severity;




public class SQLUtils {

    public Connection connection;

    private final Connection conn;

    private boolean conn() {
        return conn == null;
    }

    /*
     *  +-----------------------------------------------+
     *  |                 SQL Connector:                |
     *  |   This section connects to the database and   |
     *  |    sets the credentials for the connection    |
     *  +-----------------------------------------------+
     */


    public final String HOST;
    public final String PORT;
    public final String USERNAME;
    public final String PASSWORD;
    public final String DATABASE;
    public SQLLogger logger;


    /**
     * @param database What is the name of the database you want to connect to?
     * @param host     Where is the database being hosted? If it's on this machine set this parameter to localhost.
     * @param port     What port is the server running on, the default port for MySQL is 3306.
     * @param username What user do you want to access the database as? Should be set to 'root' if possible.
     * @param PASSWORD What password does that username use? If none just leave as an empty string "".
     */
    public SQLUtils(String database, String host, String port, String username, String PASSWORD) {
        this.PASSWORD = PASSWORD;
        this.USERNAME = username;
        this.DATABASE = database;
        this.HOST = host;
        this.PORT = port;

        this.logger = new SQLLogger();

        conn = getConnection();
        if (conn == null) {
            this.logger.log(Severity.SEVERE, "Connection unsuccessful!");
        }
    }


    public Connection getConnection() {
        connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://"
                            + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=false"
                    , USERNAME, PASSWORD);
            this.logger.log(Severity.INFO,"Database Connected");
        } catch (SQLException e) {
            printSQLException(e);
            return null;
        }
        return connection;
    }

    public void closeConnection(Connection connArg) {
        System.out.println("Closing the Database...");
        try {
            connArg.close();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                //e.printStackTrace(System.err);
                String message = " SQLSTATE" + ((SQLException) e).getSQLState() + "\n" + "Error Code: "
                        + ((SQLException) e).getErrorCode() + "\n" +"Error Code: " + ((SQLException) e).getErrorCode()
                        + "\n" + "Message: " + e.getMessage() + "\n"  + "Error at line: " +
                        e.getStackTrace()[e.getStackTrace().length - 1]
                        + "\n" +"The database is not connected! please ensure that the login credentials are " +
                        "correct and the database is running!";
                this.logger.log(Severity.SEVERE, message);



            }
        }
    }

    /*
     *  +-----------------------------------------------+
     *  |                   SQL Utils:                  |
     *  |    methods for setting data, getting data     |
     *  | as well as some smaller methods like countRows|
     *  +-----------------------------------------------+
     */

    /**
     * @param value     Data to be saved in form of a string, number will be converted depending on dataType
     * @param idColumn  identifier column to check
     * @param id        String that the id column is checked against
     * @param column    column to insert data
     * @param tableName table to insert data
     */
    public void updateData(Object value, String idColumn, String id, String column, String tableName) {
        if (conn()) {
            logger.connError();
            return;
        }
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE "+ tableName +" SET "+ column +"=? WHERE "+ idColumn+"=?");
            ps = prepareStatement(ps,id,1);
            ps.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    /**
     *
     * @param column Selected Column
     * @param idColumn Identifying Column
     * @param idEquals Identifier
     * @param tableName Table Name
     * @return Object Data Type From Table
     */
    public Object getData(String column, String idColumn, String idEquals, String tableName){
        if (conn()) {
            logger.connError();
            return null;
        }
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT "+ column + " FROM "+ tableName +" WHERE ?="+ idColumn);
            ps = prepareStatement(ps,idEquals,1);
            ResultSet rs = ps.executeQuery();
            Object info;
            rs.next();
            info = rs.getObject(column);
            return info;

        } catch (SQLException e) {
            printSQLException(e);
            return null;
        }


    }
    public enum Operation {
        GTE(">="),
        LTE("<="),
        GT(">"),
        LT("<"),
        ALL("*");

        Operation(String sqlOperator){
            this.sqlOperator = sqlOperator;
        }

        private final String sqlOperator;
    }

    /**
     *
     * @param column Selected Column
     * @param idColumn Identifying Column
     * @param idEquals Identifier
     * @param tableName Table Name
     * @param operation Use Operation Enumn e.g. Operation.GTE = ">="
     * Note, usage of ALL selects all in a row
     * @return List Of Objects
     */
    public List<Object> getData(String column, String idColumn, String idEquals, String tableName, Operation operation){
        if (conn()) {
            logger.connError();
            return null;
        }


        try {
            PreparedStatement ps = null;
            if (!(operation == Operation.ALL)){
                ps = conn.prepareStatement("SELECT "+ column +" FROM "+ tableName +" WHERE " +
                    idColumn+ operation+"?");
            } else{
                ps = conn.prepareStatement("SELECT * FROM "+ tableName +" WHERE " +
                        idColumn+"=?");
            }
            prepareStatement(ps,idEquals,1);
            ResultSet rs = ps.executeQuery();
            List<Object> info = new  ArrayList<Object>();
            while (rs.next()) {
                info.add(rs.getObject(column));
            }
            return info;
        } catch (SQLException e) {
            printSQLException(e);
            return new ArrayList<Object>();
        }


    }
    private PreparedStatement prepareStatement(PreparedStatement ps,Object value,int loc) throws SQLException{
        if (value instanceof Integer){
            ps.setInt(loc, (Integer) value);
        } else if(value instanceof Float){
            ps.setFloat(loc, (Float) value);
        }else if (value instanceof  Double){
            ps.setDouble(loc, (Double) value);
        } else {
            ps.setString(loc, (String) value);
        }
        return  ps;
    }



    /**
     * @param name     What name do you want to u se for the table
     * @param idColumn This is the unique ID column generally 'NAME'
     */
    public void createTable(String name, String idColumn) {
        if (conn()) {
            logger.connError();
            return;
        }
        try {
            PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + name + " (" + idColumn
                    + " VARCHAR(100),PRIMARY KEY (" + idColumn + "))");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param id        This is the name of the column
     * @param dataType  What data type do u want to use
     * @param tableName The name of the table you want to put the column into
     */
    public void createColumn(String id, String dataType, String tableName) {
        if (conn()) {
            logger.connError();
            return;
        }
        try {
            if (!columnExists(id, tableName)) {
                PreparedStatement ps = conn.prepareStatement("ALTER TABLE " + tableName + " ADD " + id + " "
                        + dataType + " NOT NULL");
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param idColumn  What is the ID column used in this table generally "NAME"
     * @param idEquals  What should the id of this row be? What is the name?
     * @param tableName What table dp you want to inset into?
     */
    public void createRow(String idColumn, String idEquals, String tableName) {
        if (conn()) {
            logger.connError();
            return;
        }
        if (!rowExists(idColumn, idEquals, tableName)) {
            try {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + tableName);
                ResultSet results = ps.executeQuery();
                results.next();
                PreparedStatement ps2 = conn.prepareStatement("INSERT IGNORE INTO " + tableName + " ("
                        + idColumn + ") VALUE (?)");
                ps2.setString(1, idEquals);
                ps2.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                e.printStackTrace();
            }
        }
    }

    /**
     * @param idColumn  What column in this table is unique generally the "NAME" column
     * @param test      What do u want to test the idColumn for
     * @param tableName In what table
     * @return Returns true if it exists and false if it does not
     */
    public boolean rowExists(String idColumn, String test, String tableName) {
        if (conn()) {
            logger.connError();
            return false;
        }
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + tableName + " WHERE " + idColumn
                    + "=?");
            ps.setString(1, test);

            ResultSet results = ps.executeQuery();
            //row is found
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param name      Name of the column you are checking for
     * @param tableName name of the target table
     * @return returns true if the column exists else returns false
     */
    public boolean columnExists(String name, String tableName) {
        if (conn()) {
            logger.connError();
            return false;
        }
        try {
            PreparedStatement ps = conn.prepareStatement("show columns from " + tableName +
                    " where field = ?");
            ps.setString(1, name);
            ResultSet resultSet = ps.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countRows(String tableName) {
        if (conn()) {
            logger.connError();
            return 0;
        }
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT count(*) FROM " + tableName);
            ResultSet rs = ps.executeQuery();
            int info;
            if (rs.next()) {
                info = rs.getInt("count(*)");
                return info;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param column    What is the name of the column you want to alter
     * @param dataType  What data type do u want to set it to
     * @param tableName In what table?
     */
    public void setDataType(String column, String dataType, String tableName) {
        if (conn()) {
            logger.connError();
            return;
        }
        try {
            PreparedStatement ps = conn.prepareStatement("ALTER TABLE " + tableName + " MODIFY " + column + " "
                    + dataType);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(String idColumn, String idEquals, String tableName) {
        if (conn()) {
            logger.connError();
            return;
        }
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM " + tableName + " WHERE " + idColumn + "=?");
            ps.setString(1, idEquals);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isNum(String type, String num) {
        try {
            if (type.equalsIgnoreCase("int")) {
                int i = Integer.parseInt(num);
                return num.equals(String.valueOf(i));
            } else if (type.equalsIgnoreCase("float")) {
                float i = Float.parseFloat(num);
                return num.equals(String.valueOf(i));
            } else if (type.equalsIgnoreCase("double")) {
                double i = Double.parseDouble(num);
                return num.equals(String.valueOf(i));
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    /**
     * Deprecated Use getData()
     * @param column    What column is the desired cell in
     * @param idColumn  What is the id column used for this table
     * @param idEquals  What id are you looking for?
     * @param tableName What is the name of the table
     * @return returns the number value of the specified cell
     */
    @Deprecated
    public int getInt(String column, String idColumn, String idEquals, String tableName) {
        if (conn()) {
            logger.connError();
            return 0;
        }
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT " + column + " FROM " + tableName + " WHERE "
                    + idColumn + "=?");
            ps.setString(1, idEquals);
            ResultSet rs = ps.executeQuery();
            int info;
            if (rs.next()) {
                info = rs.getInt(column);
                return info;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Deprecated Use getData()
     * @param column    What column is the desired cell in
     * @param idColumn  What is the id column used for this table
     * @param idEquals  What id are you looking for?
     * @param tableName What is the name of the table
     * @return returns the number value of the specified cell
     */
    @Deprecated
    public String getString(String column, String idColumn, String idEquals, String tableName) {
        if (conn()) {
            logger.connError();
            return null;
        }
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT `" + column + "` FROM " + tableName + " WHERE "
                    + idColumn + "=?");
            ps.setString(1, idEquals);
            ResultSet rs = ps.executeQuery();
            String info;
            if (rs.next()) {
                info = rs.getString(column);
                return info;
            }
        } catch (SQLSyntaxErrorException e) {
            try {
                PreparedStatement p = conn.prepareStatement("UPDATE " + tableName + " SET `" + column
                        + "`=' ' WHERE " + idColumn + "=" + idEquals);
                p.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Deprecated Use getData()
     * @param column    What column is the desired cell in
     * @param idColumn  What is the id column used for this table
     * @param idEquals  What id are you looking for?
     * @param tableName What is the name of the table
     * @return returns the number value of the specified cell
     */
    @Deprecated
    public double getDouble(String column, String idColumn, String idEquals, String tableName) {
        if (conn()) {
            logger.connError();
            return 0;
        }
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT " + column + " FROM " + tableName + " WHERE "
                    + idColumn + "=?");
            ps.setString(1, idEquals);
            ResultSet rs = ps.executeQuery();
            double info;
            if (rs.next()) {
                info = rs.getDouble(column);
                return info;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Deprecated Use getData()
     * @param column    What column is the desired cell in
     * @param idColumn  What is the id column used for this table
     * @param idEquals  What id are you looking for?
     * @param tableName What is the name of the table
     * @return returns the number value of the specified cell
     */
    @Deprecated
    public float getFloat(String column, String idColumn, String idEquals, String tableName) {
        if (conn()) {
            logger.connError();
            return 0;
        }
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT " + column + " FROM " + tableName + " WHERE "
                    + idColumn + "=?");
            ps.setString(1, idEquals);
            ResultSet rs = ps.executeQuery();
            float info;
            if (rs.next()) {
                info = rs.getFloat(column);
                return info;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
