package model;

public class TeamStats {
    private String abbreviation;
    private double avgAge;
    private double avgHeight;
    private double avgWeight;

    public TeamStats(String abbreviation, double avgAge, double avgHeight, double avgWeight) {
        this.abbreviation = abbreviation;
        this.avgAge = avgAge;
        this.avgHeight = avgHeight;
        this.avgWeight = avgWeight;
    }

    public String getAbbreviation() { return abbreviation; }
    public double getAvgAge() { return avgAge; }
    public double getAvgHeight() { return avgHeight; }
    public double getAvgWeight() { return avgWeight; }

    @Override
    public String toString() {
        return String.format("%-4s | avgH=%.1f in | avgW=%.1f lb | avgA=%.2f y",
                abbreviation, avgHeight, avgWeight, avgAge);
    }
}