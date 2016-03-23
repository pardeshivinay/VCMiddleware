package com.vc.util;

/**
 *
 * @author AnaMihalceanu
 */
import com.ibm.db2.jcc.DB2SimpleDataSource;
import com.ibm.nosql.json.api.BasicDBList;
import com.ibm.nosql.json.api.BasicDBObject;
import com.ibm.nosql.json.util.JSON;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public class DatabaseUtil {

    private static Connection connection = null;
    // set defaults
    private static String databaseHost = "75.126.155.153";
    private static int port = 50000;
    private static String databaseName = "SQLDB";
    private static String user = "user16192";
    private static String password = "Mra1uoOoQGRd";
    private static String url = "myurl";

    public static Connection getConnection() {

        if (connection != null) {
            return connection;
        } else {
            if (processVCAP()) {
                try {
                    System.out.println("Connecting to the database");
                    connection = getDataSource().getConnection();
                    System.out.println();
                    connection.setAutoCommit(false);
                } catch (SQLException e) {
                    System.out.println("Error connecting to database");
                    System.out.println("SQL Exception: " + e);

                }
            }
            return connection;
        }

    }

    private static DB2SimpleDataSource getDataSource() {
           DB2SimpleDataSource dataSource = new DB2SimpleDataSource();
           dataSource.setServerName(databaseHost);
           dataSource.setPortNumber(port);
           dataSource.setDatabaseName(databaseName);
           dataSource.setUser(user);
           dataSource.setPassword(password);
           dataSource.setDriverType(4);
           return dataSource;
    }
    
    private static boolean processVCAP() {
        // VCAP_SERVICES is a system environment variable
        // Parse it to obtain the for DB2 connection info
        String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
        System.out.println("VCAP_SERVICES content: " + VCAP_SERVICES);

        if (VCAP_SERVICES != null) {
            // parse the VCAP JSON structure
            BasicDBObject obj = (BasicDBObject) JSON.parse(VCAP_SERVICES);
            String thekey = null;
            Set<String> keys = obj.keySet();
            System.out.println("Searching through VCAP keys");
            // Look for the VCAP key that holds the SQLDB information
            for (String eachkey : keys) {
                System.out.println("Key is: " + eachkey);
                // Just in case the service name gets changed to lower case in the future, use toUpperCase
                if (eachkey.toUpperCase().contains("SQLDB")) {
                    thekey = eachkey;
                }
            }
            if (thekey == null) {
                System.out.println("Cannot find any SQLDB service in the VCAP; exiting");
                return false;
            }
            BasicDBList list = (BasicDBList) obj.get(thekey);
            obj = (BasicDBObject) list.get("0");
            System.out.println("Service found: " + obj.get("name"));
            // parse all the credentials from the vcap env variable
            obj = (BasicDBObject) obj.get("credentials");
            databaseHost = (String) obj.get("host");
            databaseName = (String) obj.get("db");
            port = Integer.parseInt(obj.get("port").toString());
            user = (String) obj.get("username");
            password = (String) obj.get("password");
            url = (String) obj.get("jdbcurl");
        } else {
            System.out.println("VCAP_SERVICES is null");
            return false;
        }

        return true;
    }

}