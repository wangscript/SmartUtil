package iminto.db;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;
/**
 * DataSource的简单实现,不建议在项目中使用
 * Author:waitfox@qq.com
 * Date:2012-11-26 下午10:45:27
 */
public class ConnectionTool implements DataSource{
	private Connection connection = null;
	
	private static String driver = null;	
	private static String url = null;
	private PreparedStatement pstmt ;
	private Statement stmt ;
	ResultSet rs ;
	//字符串方式
	private String userName;
    private String password;
    private String jdbcUrl;   
	
    static {
		try {
			Properties pro = new Properties();			
			FileInputStream fis = new FileInputStream(ConnectionTool.class.getResource("/").getPath()+"\\jdbc.properties");
			pro.load(fis);
			driver = pro.getProperty("driver");
			url = pro.getProperty("url");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
     }
    
    public Connection getConnection() {
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
    
    public Connection getConnections() throws SQLException {
        return getConnections(this.userName, this.password);
    }

    public Connection getConnections(String username, String password) throws SQLException {
    	String[] drivers = {"com.mysql.jdbc.Driver","org.sqlite.JDBC", "org.h2.Driver", "org.apache.derby.jdbc.EmbeddedDriver", "org.hsqldb.jdbcDriver" };
        for (String driver : drivers) {
            try {
                Class.forName(driver);
            } catch (Throwable e) {}
        }
        return DriverManager.getConnection(this.jdbcUrl, username, password);
    }
	
	public PreparedStatement getPstmt() {
		return pstmt;
	}

	public void setPstmt(PreparedStatement pstmt) {
		this.pstmt = pstmt;
	}

	public ResultSet getRs() {
		return rs;
	}

	public void setRs(ResultSet rs) {
		this.rs = rs;
	}

	public Statement getStmt() {
		return stmt;
	}

	public void setStmt(Statement stmt) {
		this.stmt = stmt;
	}
	
	public void close() {
		if(rs != null ){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(stmt != null) {
			try {
				stmt.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(pstmt != null) {
			try {
				pstmt.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(connection != null) {
			try {
				connection.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}



	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		return null;
	}
	
}
