package iminto.db;
import java.sql.Connection;
import java.sql.SQLException;

public class DbTest {
public static void main(String[] args) throws SQLException {
	ConnectionTool ct = new ConnectionTool();
	Connection con = null;
	try {
		con = ct.getConnection();
		System.out.println(con);
	} finally {
		ct.close();
	}
}
}
