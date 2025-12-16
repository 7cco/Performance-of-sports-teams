import model.Player;
import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:baseball.db";
    private Connection conn;

    public void connect() throws SQLException {
        conn = DriverManager.getConnection(DB_URL);
        conn.setAutoCommit(false);
        System.out.println("Connected to SQLite");
    }

    public void createTables() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS teams (
                    team_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    abbreviation TEXT UNIQUE NOT NULL
                );
                """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS positions (
                    position_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT UNIQUE NOT NULL
                );
                """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS players (
                    player_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    team_id INTEGER NOT NULL,
                    position_id INTEGER NOT NULL,
                    height_in INTEGER NOT NULL,
                    weight_lb INTEGER NOT NULL,
                    age REAL NOT NULL,
                    FOREIGN KEY (team_id) REFERENCES teams(team_id),
                    FOREIGN KEY (position_id) REFERENCES positions(position_id)
                );
                """);
        }
        System.out.println("Tables created");
    }

    public void insertData(List<Player> players) throws SQLException {
        Set<String> teams = new HashSet<>();
        Set<String> positions = new HashSet<>();
        for (Player p : players) {
            teams.add(p.getTeam());
            positions.add(p.getPosition());
        }

        Map<String, Integer> teamIds = insertAndGetIds("teams", "abbreviation", teams);
        Map<String, Integer> posIds = insertAndGetIds("positions", "name", positions);

        String sql = """
            INSERT INTO players (name, team_id, position_id, height_in, weight_lb, age)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Player p : players) {
                pstmt.setString(1, p.getName());
                pstmt.setInt(2, teamIds.get(p.getTeam()));
                pstmt.setInt(3, posIds.get(p.getPosition()));
                pstmt.setInt(4, p.getHeightInches());
                pstmt.setInt(5, p.getWeightLbs());
                pstmt.setDouble(6, p.getAge());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            conn.commit();
        }
        System.out.println("Inserted " + players.size() + " players");
    }

    private Map<String, Integer> insertAndGetIds(String table, String col, Set<String> values) throws SQLException {
        String idCol = table.endsWith("s") ? table.substring(0, table.length() - 1) + "_id" : table + "_id";

        String insertSQL = "INSERT OR IGNORE INTO " + table + " (" + col + ") VALUES (?)";
        String selectSQL = "SELECT " + col + ", " + idCol + " FROM " + table;

        try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {
            for (String v : values) {
                ps.setString(1, v);
                ps.addBatch();
            }
            ps.executeBatch();
        }

        Map<String, Integer> map = new HashMap<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            while (rs.next()) {
                map.put(rs.getString(1), rs.getInt(2));
            }
        }
        return map;
    }

    public Connection getConnection() { return conn; }

    public void close() throws SQLException {
        if (conn != null) conn.close();
    }
}