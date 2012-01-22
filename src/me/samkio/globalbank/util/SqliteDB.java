package me.samkio.globalbank.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.samkio.globalbank.GlobalBank;
import me.samkio.globalbank.methods.MiscMethods;

import org.bukkit.Location;

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
			st.executeUpdate("CREATE TABLE IF NOT EXISTS Slots (id INTEGER PRIMARY KEY, name VARCHAR(80) NOT NULL, slot INTEGER NOT NULL, items TEXT NOT NULL);");
			st.executeUpdate("CREATE TABLE IF NOT EXISTS NPCLocations (id INTEGER PRIMARY KEY, bankName VARCHAR(80) NOT NULL, loc TEXT NOT NULL);");
			st.executeUpdate("CREATE TABLE IF NOT EXISTS ActiveSlotIds (id INTEGER PRIMARY KEY, name VARCHAR(80) NOT NULL, slots TEXT NOT NULL);");
			conn.commit();
		} catch (SQLException e) {
			logger.log(Level.SEVERE,
					"[GlobalBank] Cannot connect to Sqlite Database");
			logger.log(Level.SEVERE, "[GlobalBank] " + e);
		}
	}

	public static String getSlotString(String name, int slot) {
		Connection conn = null;
		Statement st = null;
		String code = null;
		try {
			conn = getConnection();
			st = conn.createStatement();
			ResultSet rs = st
					.executeQuery("SELECT items FROM Slots WHERE name=('"
							+ name + "') AND slot=('" + slot + "')");
			while (rs.next()) {
				code = rs.getString("items");
			}
			conn.commit();
			return code;
		} catch (SQLException e) {
			logger.severe("[GlobalBank] Unable to get row database" + e);
		}
		return code;

	}

	public static ArrayList<Integer> getAvailiableSlots(String name) {
		Connection conn = null;
		Statement st = null;
		ArrayList<Integer> is = new ArrayList<Integer>();
		try {
			conn = getConnection();
			st = conn.createStatement();
			ResultSet rs = st
					.executeQuery("SELECT slots FROM ActiveSlotIds WHERE name=('"
							+ name + "')");
			while (rs.next()) {
				String code = rs.getString("slots");
				String[] split = code.split(",");
				for (String s : split) {
					is.add(Integer.parseInt(s));
				}
			}
			conn.commit();
			return is;
		} catch (SQLException e) {
			logger.severe("[GlobalBank] Unable to get row database" + e);
		}
		return is;
	}

	public static void updateSlotList(String name, ArrayList<Integer> is) {
		Connection conn = null;
		Statement st = null;
		String s = "";
		for (int i : is) {
			s = s + i + ",";
		}
		s.substring(0, s.length() - 1);
		try {
			conn = getConnection();
			st = conn.createStatement();
			st.executeUpdate("UPDATE ActiveSlotIds set slots= '" + s
					+ "' WHERE name='" + name + "'");
			conn.commit();
			return;
		} catch (SQLException e) {
			logger.severe("[GlobalBank] Unable to set row in database" + e);
		}
		return;
	}

	public static void update(String name, String string, int slot) {
		Connection conn = null;
		Statement st = null;
		try {
			conn = getConnection();
			st = conn.createStatement();
			st.executeUpdate("UPDATE Slots set items= '" + string
					+ "' WHERE name='" + name + "'AND slot=('" + slot + "')");
			conn.commit();
		} catch (SQLException e) {
			logger.severe("[GlobalBank] Unable to update row database" + e);
		}

	}

	public static boolean contains(String name, int slot) {
		boolean contains = false;

		Connection conn = null;
		Statement st = null;
		try {
			conn = getConnection();
			st = conn.createStatement();
			ResultSet rs = st
					.executeQuery("SELECT name FROM Slots WHERE name=('" + name
							+ "')AND slot=('" + slot + "')");
			while (rs.next()) {
				contains = true;
			}
			conn.commit();
		} catch (SQLException e) {
			logger.severe("[GlobalBank] Unable to get row database" + e);
		}
		return contains;
	}

	public static boolean containsSlotIds(String name) {
		boolean contains = false;

		Connection conn = null;
		Statement st = null;
		try {
			conn = getConnection();
			st = conn.createStatement();
			ResultSet rs = st
					.executeQuery("SELECT name FROM ActiveSlotIds WHERE name=('"
							+ name + "')");
			while (rs.next()) {
				contains = true;
			}
			conn.commit();
		} catch (SQLException e) {
			logger.severe("[GlobalBank] Unable to get row database" + e);
		}
		return contains;
	}

	public static boolean newRow(String name, String code, int slot) {
		Connection conn = null;
		Statement st = null;
		try {
			conn = getConnection();
			st = conn.createStatement();
			st.executeUpdate("INSERT INTO Slots (name,slot,items) VALUES ('"
					+ name + "','" + slot + "','" + code + "')");
			conn.commit();
		} catch (SQLException e) {
			logger.severe("[GlobalBank] Unable to add row to database " + e);
			return false;
		}
		return true;

	}

	public static boolean newRowSlotIdList(String name, ArrayList<Integer> is) {
		Connection conn = null;
		Statement st = null;
		String s = "";
		if(!is.isEmpty()){
		for (int i : is) {
			s = s + i + ",";
		}
		s.substring(0, s.length() - 1);
		}
		try {
			conn = getConnection();
			st = conn.createStatement();
			st.executeUpdate("INSERT INTO ActiveSlotIds (name,slots) VALUES ('"
					+ name + "','" + s + "')");
			conn.commit();
		} catch (SQLException e) {
			logger.severe("[GlobalBank] Unable to add row to database " + e);
			return false;
		}
		return true;

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
				npcs.put(MiscMethods.locFromString(rs.getString("loc")),rs.getString("bankname"));
			}
			conn.commit();
		} catch (SQLException e) {
			logger.severe("[GlobalBank] Unable to get row from database " + e);
			return npcs;
		}
		return npcs;

	}
}
