import model.Player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {

    public static List<Player> parse(String filePath) throws IOException {
        List<Player> players = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", 6);
                if (parts.length < 6) {
                    System.err.println("Skip line: " + line);
                    continue;
                }

                try {
                    String name = parts[0];
                    String team = parts[1];
                    String position = parts[2];
                    int height = Integer.parseInt(parts[3].trim());
                    int weight = Integer.parseInt(parts[4].trim());
                    double age = Double.parseDouble(parts[5].trim());

                    players.add(new Player(name, team, position, height, weight, age));
                } catch (Exception e) {
                    System.err.println("Parse error: " + line + e.getMessage());
                }
            }
        }
        return players;
    }
}