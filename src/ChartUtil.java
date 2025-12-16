import model.TeamStats;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import java.util.List;

public class ChartUtil {

    public static void showAvgAgeChart(List<TeamStats> stats) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (TeamStats s : stats) {
            dataset.addValue(s.getAvgAge(), "Средний возраст", s.getAbbreviation());
        }

        // Создаём столбчатую диаграмму
        JFreeChart chart = ChartFactory.createBarChart(
                "Средний возраст игроков по командам",
                "Команда",
                "Возраст (лет)",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        // Показываем окно
        ChartFrame frame = new ChartFrame("График: средний возраст", chart);
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}