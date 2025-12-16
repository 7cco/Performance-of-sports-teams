package model;

public class Player {
    private String name;
    private String team;
    private String position;
    private int heightInches;
    private int weightLbs;
    private double age;

    public Player(String name, String team, String position, int heightInches, int weightLbs, double age) {
        this.name = name;
        this.team = clean(team);
        this.position = clean(position);
        this.heightInches = heightInches;
        this.weightLbs = weightLbs;
        this.age = age;
    }

    private String clean(String s) {
        return s.trim().replaceAll("^\"+|\"+$", "").replaceAll("\"{2,}", "\"").replaceAll("^\"|\"$", "");
    }

    public String getName() { return name; }
    public String getTeam() { return team; }
    public String getPosition() { return position; }
    public int getHeightInches() { return heightInches; }
    public int getWeightLbs() { return weightLbs; }
    public double getAge() { return age; }

    @Override
    public String toString() {
        return String.format("%-20s | %4d in | %4d lb | %.2f y", name, heightInches, weightLbs, age);
    }
}