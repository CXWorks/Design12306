package connector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/*
  Design12306/connector/MySQL.java
  @author cxworks
  2016-11-06
*/

public class MySQL {
	
	private HikariDataSource ds;
	
	public MySQL() {
		
		ds = new HikariDataSource();
        ds.setMaximumPoolSize(20);
        ds.setDriverClassName("org.mariadb.jdbc.Driver");
        ds.setJdbcUrl("jdbc:mariadb://114.55.37.133:15003/work3?useUnicode=true&characterEncoding=UTF-8");
        ds.addDataSourceProperty("user", "root");
        ds.addDataSourceProperty("password", "chicer2016");
        ds.setConnectionTimeout(60000);
        ds.setMaxLifetime(500);
        ds.setAutoCommit(false);
	}
    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }


    public void shutdown() {
        ds.close();
    }

    
}
