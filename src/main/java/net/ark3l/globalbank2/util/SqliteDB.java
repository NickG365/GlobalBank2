package net.ark3l.globalbank2.util;

import net.ark3l.globalbank2.GlobalBank;
import net.ark3l.globalbank2.methods.MiscMethods;
import org.bukkit.Location;

import java.sql.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SqliteDB {
	private static Connection connection;
	public final static Logger logger = Logger.getLogger("Minecraft");

	public static synchronized Connection getConnection() {
		if (connection == null) {
			connection = createConnection();
		}
		return connection;
	}

	public static void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "[GlobalBank]" + e);
		}
	}

	private static Connection createConnection() {
		try {

			Class.forName("org.sqlite.JDBC");
			Connection ret = DriverManager.getConnection("jdbc:sqlite:"
					+ GlobalBank.plugin.getDataFolder() + "/Data/GlobalBank.sqlite");
			ret.setAutoCommit(false);
			return ret;
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, "[GlobalBank]" + e);
			return null;
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "[GlobalBank]" + e);
		}
		return null;
	}

	public static void prepare() {
		Connection conn = null;
		Statement st = null;
		try {
			conn = SqliteDB.getConnection();
			st = conn.createStatement();
			st.executeUpdate("CREATE TABLE IF NOT EXISTS NPCLocations (id INTEGER PRIMARY KEY, bankName VARCHAR(80) NOT NULL, loc TEXT NOT NULL);");
			conn.commit();
		} catch (SQLException e) {
			logger.log(Level.SEVERE,
					"[GlobalBank] Cannot connect to Sqlite Database");
			logger.log(Level.SEVERE, "[GlobalBank] " + e);
		}
	}


	public static boolean newBanker(String bankname, Location l) {
		Connection conn = null;
		Statement st = null;
		try {
			conn = getConnection();
			st = conn.createStatement();
			st.executeUpdate("INSERT INTO NPCLocations (bankName,loc) VALUES ('"
					+ bankname + "','" + MiscMethods.stringFromLoc(l) + "')");
			conn.commit();
		} catch (SQLException e) {
			logger.severe("[GlobalBank] Unable to add row to database " + e);
			return false;
		}
		return true;

	}

	public static boolean delBanker(String bankname) {
		Connection conn = null;
		Statement st = null;
		try {
			conn = getConnection();
			st = conn.createStatement();
			st.executeUpdate("DELETE FROM NPCLocations WHERE bankname='"
					+ bankname + "'");
			conn.commit();
		} catch (SQLException e) {
			logger.severe("[GlobalBank] Unable to remove row to database " + e);
			return false;
		}
		return true;

	}

	public static HashMap<Location, String> getBankers() {
		Connection conn = null;
		Statement st = null;
		HashMap<Location, String> npcs = new HashMap<Location, String>();
		try {
			conn = getConnection();
			st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM NPCLocations");
			while (rs.next()) {
				npcs.put(MiscMethods.locFromString(rs.getString("loc")), rs.getString("bankname"));
			}
			conn.commit();
		} catch (SQLException e) {
			logger.severe("[GlobalBank] Unable to get row from database " + e);
			return npcs;
		}
		return npcs;

	}
}
