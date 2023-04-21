package com.kraken.wakala.dtos;

public class Exercise {
    String name, description, suitableFor, unit;
    int number;

    public Exercise() {
    }

    public Exercise(String name, String description, String suitableFor, String unit, int number) {
        this.name = name;
        this.description = description;
        this.suitableFor = suitableFor;
        this.unit = unit;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSuitableFor() {
        return suitableFor;
    }

    public void setSuitableFor(String suitableFor) {
        this.suitableFor = suitableFor;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
