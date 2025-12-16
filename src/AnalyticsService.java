import model.Player;
import model.TeamStats;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnalyticsService {
    private final Connection conn;

    public AnalyticsService(Connection conn) {
        this.conn = conn;
    }

    public List<TeamStats> getAvgAgeByTeam() throws SQLException {
        String sql = """
            SELECT t.abbreviation,
                   AVG(p.age) AS avg_age,
                   AVG(p.height_in) AS avg_height,
                   AVG(p.weight_lb) AS avg_weight
            FROM players p
            JOIN teams t ON p.team_id = t.team_id
            GROUP BY t.team_id
            ORDER BY avg_age DESC;
            """;

        List<TeamStats> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new TeamStats(
                        rs.getString("abbreviation"),
                        rs.getDouble("avg_age"),
                        rs.getDouble("avg_height"),
                        rs.getDouble("avg_weight")
                ));
            }
        }
        return list;
    }

    public String getTeamWithMaxAvgHeight() throws SQLException {
        String sql = """
            SELECT t.abbreviation
            FROM players p
            JOIN teams t ON p.team_id = t.team_id
            GROUP BY t.team_id
            ORDER BY AVG(p.height_in) DESC
            LIMIT 1;
            """;
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getString(1) : "—";
        }
    }

    public List<Player> getTop5Tallest(String team) throws SQLException {
        String sql = """
            SELECT p.name, p.height_in, p.weight_lb, p.age
            FROM players p
            JOIN teams t ON p.team_id = t.team_id
            WHERE t.abbreviation = ?
            ORDER BY p.height_in DESC
            LIMIT 5;
            """;
        List<Player> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, team);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Player(
                        rs.getString("name"),
                        team,
                        "", // unused
                        rs.getInt("height_in"),
                        rs.getInt("weight_lb"),
                        rs.getDouble("age")
                ));
            }
        }
        return list;
    }

    public TeamStats getTeamInBounds() throws SQLException {
        String sql = """
            SELECT t.abbreviation,
                   AVG(p.height_in) AS avg_height,
                   AVG(p.weight_lb) AS avg_weight,
                   AVG(p.age) AS avg_age
            FROM players p
            JOIN teams t ON p.team_id = t.team_id
            GROUP BY t.team_id
            HAVING 
                AVG(p.height_in) BETWEEN 74 AND 78
                AND AVG(p.weight_lb) BETWEEN 190 AND 210
            ORDER BY AVG(p.age) DESC
            LIMIT 1;
            """;
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return new TeamStats(
                        rs.getString("abbreviation"),
                        rs.getDouble("avg_age"),
                        rs.getDouble("avg_height"),
                        rs.getDouble("avg_weight")
                );
            }
        }
        return null;
    }
}