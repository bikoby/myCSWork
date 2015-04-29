import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Part of the {@link LoginServer} example. Handles all database-related
 * actions.
 * 
 * @see LoginServer
 * @see DatabaseConnector
 */
public class LoginDatabaseHandler {

	/** A logger for debugging. */
	private static final Logger log = LogManager.getLogger();

	/** Makes sure only one database handler is instantiated. */
	private static LoginDatabaseHandler singleton = new LoginDatabaseHandler();

	/** Used to determine if tables are already setup. */
	private static final String TABLES_SQL = "SHOW TABLES LIKE 'login_users';";

	private static final String CHECKSNIPPET_SQL = "SHOW TABLES LIKE 'Snippet';";

	/** Used to create necessary tables for this example. */
	private static final String CREATE_SQL = "CREATE TABLE login_users ("
			+ "userid INTEGER AUTO_INCREMENT PRIMARY KEY, "
			+ "username VARCHAR(32) NOT NULL UNIQUE, "
			+ "password CHAR(64) NOT NULL, " + "usersalt CHAR(32) NOT NULL);";

	private static final String CREATE_HIS = "CREATE TABLE History ("
			+ "userid INTEGER AUTO_INCREMENT PRIMARY KEY, "
			+ "username VARCHAR(32) NOT NULL  , "
			+ "his VARCHAR(200) NOT NULL , time VARCHAR(200) NOT NULL );";

	private static final String CREATE_LAST = "CREATE TABLE Last ("
			+ "userid INTEGER AUTO_INCREMENT PRIMARY KEY, "
			+ "username VARCHAR(32) NOT NULL  , "
			+ "lastTime VARCHAR(200) NOT NULL  );";

	private static final String CREATE_OPTION = "CREATE TABLE UserOption ("
			+ "userid INTEGER AUTO_INCREMENT PRIMARY KEY, "
			+ "username VARCHAR(32) NOT NULL  , "
			+ "historyOption VARCHAR(32) DEFAULT 'true' ,"
			+ "theme INTEGER DEFAULT 1 );";

	private static final String CREATE_SNIPPET = "CREATE TABLE Snippet ("
			+ "html VARCHAR(200) NOT NULL PRIMARY KEY , "
			+ "snippet VARCHAR(400) );";

	/** Used to insert a new user into the database. */
	private static final String REGISTER_SQL = "INSERT INTO login_users (username, password, usersalt) "
			+ "VALUES (?, ?, ?);";

	private static final String HISTORY_SQL = "INSERT INTO History (username, his, time) "
			+ "VALUES (?, ?, ?);";

	private static final String LAST_SQL = "INSERT INTO Last (username, lastTime) "
			+ "VALUES (?, ?);";

	private static final String DEFAULTUSEROPTION_SQL = "INSERT INTO UserOption (username,theme) "
			+ "VALUES (?,?);";

	private static final String USEROPTION_SQL = "INSERT INTO UserOption (username, historyOption, theme) "
			+ "VALUES (?, ?, ?);";

	private static final String ADDSNIPPET_SQL = "INSERT INTO Snippet (html, snippet) "
			+ "VALUES (?, ?);";

	/** Used to determine if a username already exists. */
	private static final String USER_SQL = "SELECT username FROM login_users WHERE username = ?";

	private static final String LASTCHECK_SQL = "SELECT lastTime FROM Last WHERE username = ?";

	private static final String OPTION1_SQL = "SELECT historyOption FROM UserOption WHERE username = ?";

	private static final String OPTION2_SQL = "SELECT theme FROM UserOption WHERE username = ?";

	/** Used to retrieve the salt associated with a specific user. */
	private static final String SALT_SQL = "SELECT usersalt FROM login_users WHERE username = ?";

	private static final String GetSNIPPET_SQL = "SELECT snippet FROM Snippet WHERE html = ?";

	/** Used to authenticate a user. */
	private static final String AUTH_SQL = "SELECT username FROM login_users "
			+ "WHERE username = ? AND password = ?";

	/** Used to remove a user from the database. */
	private static final String DELETE_SQL = "DELETE FROM login_users WHERE username = ?";

	private static final String DELETE_HIS = "DELETE FROM History WHERE username = ?";

	private static final String DELETE_LAST = "DELETE FROM Last WHERE username = ?";

	private static final String DELETE_USEROPTION = "DELETE FROM UserOption WHERE username = ?";

	private static final String CLEAR_SNIPPET = "DELETE FROM Snippet";

	/** Used to configure connection to database. */
	private DatabaseConnector db;

	/**
	 * Initializes a database handler for the Login example. Private constructor
	 * forces all other classes to use singleton.
	 */
	private LoginDatabaseHandler() {
		Status status = Status.OK;

		try {
			this.db = new DatabaseConnector();

			if (!db.testConnection()) {
				status = Status.CONNECTION_FAILED;
			} else {
				status = this.setupTables();
			}
		} catch (FileNotFoundException e) {
			status = Status.MISSING_CONFIG;
		} catch (IOException e) {
			status = Status.MISSING_VALUES;
		}

		// We cannot move on if the database handler fails, so exit
		if (status != Status.OK) {
			log.fatal(status.message());
			System.exit(-status.ordinal());
		}
	}

	/**
	 * Gets the single instance of the database handler.
	 * 
	 * @return instance of the database handler
	 */
	public static LoginDatabaseHandler getInstance() {
		return singleton;
	}

	/**
	 * Checks if necessary table exists in database, and if not tries to create
	 * it.
	 * 
	 * @return {@link Status.OK} if table exists or create is successful
	 */
	private Status setupTables() {
		Status status = Status.ERROR;
		ResultSet results = null;

		try (Connection connection = db.getConnection();
				Statement statement = connection.createStatement();) {
			// check if table exists in database
			statement.executeQuery(TABLES_SQL);
			results = statement.getResultSet();

			if (!results.next()) {
				System.out.println("Creating tables...");

				// create table and check if successful
				statement.executeUpdate(CREATE_SQL);
				statement.executeUpdate(CREATE_HIS);
				statement.executeUpdate(CREATE_LAST);
				statement.executeUpdate(CREATE_OPTION);
				this.registerUser("koby", "245467364");
				statement.executeQuery(TABLES_SQL);

				results = statement.getResultSet();
				status = (results.next()) ? Status.OK : Status.CREATE_FAILED;
			} else {
				log.debug("Tables found.");
				status = Status.OK;
			}
		} catch (Exception ex) {
			status = Status.CREATE_FAILED;
			log.debug(status, ex);
		}

		return status;
	}

	/**
	 * Checks if the snippet table is in the database. If so, clean the table.
	 * If not, create a table. Then add snippet in the table.
	 * 
	 * @return status
	 */
	public Status setupSnippet() {
		Status status = Status.ERROR;
		ResultSet results = null;

		try (Connection connection = db.getConnection();
				Statement statement = connection.createStatement();) {
			// check if table exists in database
			statement.executeQuery(CHECKSNIPPET_SQL);
			results = statement.getResultSet();

			if (!results.next()) {

				// create table and check if successful
				statement.executeUpdate(CREATE_SNIPPET);

				results = statement.getResultSet();
				status = (results.next()) ? Status.OK : Status.CREATE_FAILED;
			} else {
				clearSnippet(statement);
				status = Status.OK;
			}
		} catch (Exception ex) {
			status = Status.CREATE_FAILED;

		}

		return status;
	}

	/**
	 * Clean all informations in the table.
	 * 
	 * @param statement
	 * @return status
	 */
	private Status clearSnippet(Statement statement) {
		Status status = Status.ERROR;
		try {
			statement.executeUpdate(CLEAR_SNIPPET);
			status = Status.OK;
		} catch (SQLException e) {
			System.out.println("Fail to clear snippet");
		}
		return status;
	}

	/**
	 * Add snippet in the table.
	 * 
	 * @param html
	 * @param snippet
	 * @return status
	 */
	public Status addSnippet(String html, String snippet) {
		Status status = Status.ERROR;
		try (Connection connection = db.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(ADDSNIPPET_SQL);) {
			statement.setString(1, html);
			statement.setString(2, snippet);
			statement.executeUpdate();
			status = Status.OK;
		} catch (Exception e) {
			System.out.println("Fail to add Snippet");
			e.printStackTrace();
		}

		return status;
	}

	/**
	 * Get snippet matching the html.
	 * 
	 * @param html
	 * @return status
	 */
	public String getSnippet(String html) {
		try (Connection connection = db.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(GetSNIPPET_SQL);) {
			statement.setString(1, html);
			statement.executeQuery();

			ResultSet results = statement.getResultSet();
			return results.next() ? results.getString("snippet") : "";
		} catch (Exception e) {
			System.out.println("Fail to get Snippet");
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * Tests if a user already exists in the database. Requires an active
	 * database connection.
	 * 
	 * @param connection
	 *            - active database connection
	 * @param user
	 *            - username to check
	 * @return Status.OK if user does not exist in database
	 * @throws SQLException
	 */
	private Status duplicateUser(Connection connection, String user)
			throws SQLException {
		Status status = Status.ERROR;

		try (PreparedStatement statement = connection
				.prepareStatement(USER_SQL);) {
			statement.setString(1, user);
			statement.executeQuery();

			ResultSet results = statement.getResultSet();
			status = results.next() ? Status.DUPLICATE_USER : Status.OK;
		}

		return status;
	}

	/**
	 * Tests if a user already exists in the database.
	 * 
	 * @see #duplicateUser(Connection, String)
	 * @param user
	 *            - username to check
	 * @return Status.OK if user does not exist in database
	 */
	public Status duplicateUser(String user) {
		Status status = Status.ERROR;

		try (Connection connection = db.getConnection();) {
			status = duplicateUser(connection, user);
		} catch (SQLException e) {
			status = Status.CONNECTION_FAILED;
			log.debug(e.getMessage(), e);
		}

		return status;
	}

	/**
	 * Registers a new user, placing the username, password hash, and salt into
	 * the database if the username does not already exist.
	 * 
	 * @param newuser
	 *            - username of new user
	 * @param newpass
	 *            - password of new user
	 * @return {@link Status.OK} if registration successful
	 * @throws SQLException
	 */
	private Status registerUser(Connection connection, String newuser,
			String newpass) throws SQLException {
		Status status = Status.ERROR;

		byte[] saltbyte = StringUtilities.randomBytes(16);
		String usersalt = StringUtilities.encodeHex(saltbyte, 32);
		String passhash = StringUtilities.getHash(newpass, usersalt);

		try (PreparedStatement statement = connection
				.prepareStatement(REGISTER_SQL);) {
			statement.setString(1, newuser);
			statement.setString(2, passhash);
			statement.setString(3, usersalt);
			statement.executeUpdate();
			status = Status.OK;
		}

		return status;
	}

	/**
	 * Registers a new user, placing the username, password hash, and salt into
	 * the database if the username does not already exist.
	 * 
	 * @param newuser
	 *            - username of new user
	 * @param newpass
	 *            - password of new user
	 * @return {@link Status.OK} if registration successful
	 */
	public Status registerUser(String newuser, String newpass) {
		Status status = Status.ERROR;

		log.debug("Registering " + newuser + ".");

		// make sure we have non-null and non-emtpy values for login
		if (StringUtilities.checkString(newuser)
				|| StringUtilities.checkString(newpass)) {
			status = Status.INVALID_LOGIN;
			log.debug(status);
			return status;
		}

		// try to connect to database and test for duplicate user
		try (Connection connection = db.getConnection();) {
			status = duplicateUser(connection, newuser);

			// if okay so far, try to insert new user
			if (status == Status.OK) {
				status = registerUser(connection, newuser, newpass);
			}
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			log.debug(status, ex);
		}

		return status;
	}

	/**
	 * Upate the user's history.
	 * 
	 * @param connection
	 * @param user
	 * @param his
	 * @param time
	 * @return status
	 * @throws SQLException
	 */
	private Status updateHis(Connection connection, String user, String his,
			String time) throws SQLException {
		Status status = Status.ERROR;

		try (PreparedStatement statement = connection
				.prepareStatement(HISTORY_SQL);) {
			statement.setString(1, user);
			statement.setString(2, his);
			statement.setString(3, time);
			statement.executeUpdate();
			status = Status.OK;
		}

		return status;
	}

	/**
	 * Check whether the history and time is null or empty, then call updateHis.
	 * 
	 * @param user
	 * @param his
	 * @param string
	 * @return status
	 */
	public Status updateHis(String user, String his, String string) {
		Status status = Status.ERROR;

		log.debug("Registering " + user + ".");

		// make sure we have non-null and non-emtpy values for login
		if (StringUtilities.checkString(user)
				|| StringUtilities.checkString(his)) {
			status = Status.INVALID_LOGIN;
			log.debug(status);
			return status;
		}
		// try to connect to database and test for duplicate user
		try (Connection connection = db.getConnection();) {
			// status = duplicateUser(connection, user);
			status = Status.OK;
			// if okay so far, try to insert new user
			if (status == Status.OK) {
				status = updateHis(connection, user, his, string);
			}
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			log.debug(status, ex);
		}

		return status;
	}

	/**
	 * Update the last logged in time of the user.
	 * 
	 * @param user
	 * @param time
	 * @return status
	 * @throws SQLException
	 */
	public Status updateLast(String user, String time) throws SQLException {
		Status status = lastLoginCheck(user);
		if (status != Status.OK) {
			status = clearLast(user);
		}
		try (Connection connection = db.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(LAST_SQL);) {
			statement.setString(1, user);
			statement.setString(2, time);
			statement.executeUpdate();
			status = Status.OK;
		}

		return status;
	}

	/**
	 * Remove the last logged in time.
	 * 
	 * @param username
	 * @return status
	 * @throws SQLException
	 */
	private Status clearLast(String username) throws SQLException {
		Status status = Status.ERROR;
		try (Connection connection = db.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(DELETE_LAST);) {
			statement.setString(1, username);
			statement.executeUpdate();
			status = Status.OK;
		}

		return status;
	}

	/**
	 * Check whether the last login is existed or not.
	 * 
	 * @param user
	 * @return status
	 * @throws SQLException
	 */
	private Status lastLoginCheck(String user) throws SQLException {
		Status status = Status.ERROR;

		try (Connection connection = db.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(LASTCHECK_SQL);) {
			statement.setString(1, user);
			statement.executeQuery();

			ResultSet results = statement.getResultSet();
			status = results.next() ? Status.DUPLICATE_USER : Status.OK;
		}

		return status;
	}

	/**
	 * Get the last log in time.
	 * 
	 * @param user
	 * @return last
	 * @throws SQLException
	 */
	public String getLast(String user) throws SQLException {
		String last = null;
		try (Connection connection = db.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(LASTCHECK_SQL);) {
			statement.setString(1, user);
			statement.executeQuery();

			ResultSet results = statement.getResultSet();
			last = results.next() ? results.getString("lastTime") : null;

		}
		return last;
	}

	/**
	 * Set the user with the default option.
	 * 
	 * @param user
	 * @return status
	 */
	public Status defautOption(String user) {
		Status status = Status.ERROR;
		try (Connection connection = db.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(DEFAULTUSEROPTION_SQL);) {
			statement.setString(1, user);
			statement.setInt(2, 1);
			statement.executeUpdate();

			status = Status.OK;

		} catch (Exception e) {
			System.out.println("Set default option fail");
		}
		return status;
	}

	/**
	 * Get the history option of the user.
	 * 
	 * @param user
	 * @return last
	 */
	public String getHistoryOption(String user) {
		String last = "ture";
		try (Connection connection = db.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(OPTION1_SQL);) {
			statement.setString(1, user);
			statement.executeQuery();

			ResultSet results = statement.getResultSet();
			last = results.next() ? results.getString("historyOption") : "true";

		} catch (Exception e) {
			System.out.println("Get history option fail");
		}
		return last;
	}

	/**
	 * Get the theme option of the user.
	 * 
	 * @param user
	 * @return last
	 */
	public int getTheme(String user) {
		int last = 1;
		try (Connection connection = db.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(OPTION2_SQL);) {
			statement.setString(1, user);
			statement.executeQuery();

			ResultSet results = statement.getResultSet();
			last = results.next() ? Integer
					.parseInt(results.getString("theme")) : 1;

		} catch (Exception e) {
			System.out.println("Get theme fail");
		}
		return last;
	}

	/**
	 * Change the history option of the user.
	 * 
	 * @param user
	 * @param option
	 * @return status
	 * @throws SQLException
	 */
	public Status changeHistoryOption(String user, String option)
			throws SQLException {
		Status status = Status.ERROR;
		try (Connection connection = db.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(DELETE_USEROPTION);
				PreparedStatement statement2 = connection
						.prepareStatement(USEROPTION_SQL);) {
			statement.setString(1, user);
			statement.executeUpdate();
			statement2.setString(1, user);
			statement2.setString(2, option);
			statement2.setInt(3, getTheme(user));
			statement2.executeUpdate();
			status = Status.OK;
		}

		return status;
	}

	/**
	 * Change the theme option of user.
	 * 
	 * @param user
	 * @param option
	 * @return
	 */
	public Status changeThemeOption(String user, int option) {
		Status status = Status.ERROR;
		try (Connection connection = db.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(DELETE_USEROPTION);
				PreparedStatement statement2 = connection
						.prepareStatement(USEROPTION_SQL);) {
			statement.setString(1, user);
			statement.executeUpdate();
			statement2.setString(1, user);
			statement2.setString(2, getHistoryOption(user));
			statement2.setInt(3, option);
			statement2.executeUpdate();

			status = Status.OK;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Change theme fail");
		}

		return status;
	}

	/**
	 * Gets the salt for a specific user.
	 * 
	 * @param connection
	 *            - active database connection
	 * @param user
	 *            - which user to retrieve salt for
	 * @return salt for the specified user or null if user does not exist
	 * @throws SQLException
	 *             if any issues with database connection
	 */
	private String getSalt(Connection connection, String user)
			throws SQLException {
		String salt = null;

		try (PreparedStatement statement = connection
				.prepareStatement(SALT_SQL);) {
			statement.setString(1, user);
			statement.executeQuery();

			ResultSet results = statement.getResultSet();
			salt = results.next() ? results.getString("usersalt") : null;
		}

		return salt;
	}

	/**
	 * Checks if the provided username and password match what is stored in the
	 * database. Requires an active database connection.
	 * 
	 * @param username
	 *            - username to authenticate
	 * @param password
	 *            - password to authenticate
	 * @return {@link Status.OK} if authentication successful
	 * @throws SQLException
	 */
	private Status authenticateUser(Connection connection, String username,
			String password) throws SQLException {

		Status status = Status.ERROR;

		try (PreparedStatement statement = connection
				.prepareStatement(AUTH_SQL);) {
			String usersalt = getSalt(connection, username);
			String passhash = StringUtilities.getHash(password, usersalt);

			statement.setString(1, username);
			statement.setString(2, passhash);
			statement.executeQuery();

			ResultSet results = statement.getResultSet();
			status = results.next() ? status = Status.OK : Status.INVALID_LOGIN;
		}

		return status;
	}

	/**
	 * Checks if the provided username and password match what is stored in the
	 * database. Must retrieve the salt and hash the password to do the
	 * comparison.
	 * 
	 * @param username
	 *            - username to authenticate
	 * @param password
	 *            - password to authenticate
	 * @return {@link Status.OK} if authentication successful
	 */
	public Status authenticateUser(String username, String password) {
		Status status = Status.ERROR;

		log.debug("Authenticating user " + username + ".");

		try (Connection connection = db.getConnection();) {
			status = authenticateUser(connection, username, password);
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			log.debug(status, ex);
		}

		return status;
	}

	/**
	 * Removes a user from the database if the username and password are
	 * provided correctly.
	 * 
	 * @param username
	 *            - username to remove
	 * @param password
	 *            - password of user
	 * @return {@link Status.OK} if removal successful
	 * @throws SQLException
	 */
	private Status removeUser(Connection connection, String username,
			String password) throws SQLException {
		Status status = Status.ERROR;

		try (PreparedStatement statement = connection
				.prepareStatement(DELETE_SQL);) {
			statement.setString(1, username);
			int count = statement.executeUpdate();
			status = (count == 1) ? Status.OK : Status.INVALID_USER;
		}

		return status;
	}

	/**
	 * Removes a user from the database if the username and password are
	 * provided correctly.
	 * 
	 * @param username
	 *            - username to remove
	 * @param password
	 *            - password of user
	 * @return {@link Status.OK} if removal successful
	 */
	public Status removeUser(String username, String password) {
		Status status = Status.ERROR;

		log.debug("Removing user " + username + ".");

		try (Connection connection = db.getConnection();) {
			status = authenticateUser(connection, username, password);

			if (status == Status.OK) {
				status = removeUser(connection, username, password);
			}
		} catch (Exception ex) {
			status = Status.CONNECTION_FAILED;
			log.debug(status, ex);
		}

		return status;
	}

	/**
	 * Change the password of the user.
	 * 
	 * @param username
	 * @param newP
	 * @param oldP
	 * @return status
	 */
	public Status changePassword(String username, String newP, String oldP) {
		Status status = Status.ERROR;
		status = removeUser(username, oldP);
		if (status == Status.OK) {
			status = registerUser(username, newP);
		}
		return status;
	}

	/**
	 * Clear the History table.
	 * 
	 * @param username
	 * @return
	 * @throws SQLException
	 */
	public Status clearHistory(String username) throws SQLException {
		Status status = Status.ERROR;
		try (Connection connection = db.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(DELETE_HIS);) {
			statement.setString(1, username);
			statement.executeUpdate();
			status = Status.OK;
		}

		return status;
	}

	/**
	 * Get all history of the user.
	 * 
	 * @param SELECT
	 * @param orderby
	 * @return
	 */
	public ResultSet getResultSet(String SELECT, String orderby) {
		Connection connection;
		ResultSet results = null;
		try {
			connection = db.getConnection();
			Statement statement = connection.createStatement();
			results = statement.executeQuery(SELECT + " " + orderby);
		} catch (SQLException e) {
			System.out.println("Programme Error");
		}
		return results;
	}
}
