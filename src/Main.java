import model.Player;
import model.TeamStats;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String csvPath = "Показатели спортивных команд.csv";

        try {
            // 1. Parse
            List<Player> players = CSVParser.parse(csvPath);
            System.out.println("Parsed " + players.size() + " players");

            // 2. DB
            DatabaseManager db = new DatabaseManager();
            db.connect();
            db.createTables();
            db.insertData(players);

            // 3. Analytics
            AnalyticsService svc = new AnalyticsService(db.getConnection());

            // Задача 1
            System.out.println("\nСредний возраст по командам:");
            List<TeamStats> stats1 = svc.getAvgAgeByTeam();
            stats1.forEach(System.out::println);
            ChartUtil.showAvgAgeChart(stats1);

            // Задача 2
            String maxHeightTeam = svc.getTeamWithMaxAvgHeight();
            System.out.println("\nКоманда с самым высоким средним ростом: " + maxHeightTeam);
            System.out.println("Топ-5 самых высоких игроков в " + maxHeightTeam + ":");
            svc.getTop5Tallest(maxHeightTeam).forEach(p -> System.out.println("  " + p));

            // Задача 3
            TeamStats candidate = svc.getTeamInBounds();
            System.out.println("\nКоманда по критериям (рост 74–78, вес 190–210, max возраст):");
            if (candidate != null) {
                System.out.println("  " + candidate);
            } else {
                System.out.println("не найдена");
            }

            db.close();
            System.out.println("\nDone.");

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}