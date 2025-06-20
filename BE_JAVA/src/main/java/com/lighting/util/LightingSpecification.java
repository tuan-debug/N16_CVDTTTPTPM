package com.lighting.util;

/**
 * Immutable lighting specification class used as flyweight
 * This contains the technical specifications for lighting products
 * that can be shared among multiple products
 */
public class LightingSpecification {
    // Intrinsic state - shared across many product instances
    private final String bulbType;
    private final int wattage;
    private final String colorTemperature;
    private final int lumens;
    private final int lifeHours;
    private final String energyRating;
    
    public LightingSpecification(String bulbType, int wattage, String colorTemperature, 
                             int lumens, int lifeHours, String energyRating) {
        this.bulbType = bulbType;
        this.wattage = wattage;
        this.colorTemperature = colorTemperature;
        this.lumens = lumens;
        this.lifeHours = lifeHours;
        this.energyRating = energyRating;
    }

    public String getBulbType() {
        return bulbType;
    }

    public int getWattage() {
        return wattage;
    }

    public String getColorTemperature() {
        return colorTemperature;
    }

    public int getLumens() {
        return lumens;
    }

    public int getLifeHours() {
        return lifeHours;
    }

    public String getEnergyRating() {
        return energyRating;
    }
    
    /**
     * Get the technical specifications as a formatted string
     */
    public String getSpecificationText() {
        return String.format(
            "Bulb Type: %s, Wattage: %dW, Color Temperature: %s, " +
            "Brightness: %d lumens, Life Hours: %d hours, Energy Rating: %s",
            bulbType, wattage, colorTemperature, lumens, lifeHours, energyRating
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LightingSpecification that = (LightingSpecification) o;

        if (wattage != that.wattage) return false;
        if (lumens != that.lumens) return false;
        if (lifeHours != that.lifeHours) return false;
        if (!bulbType.equals(that.bulbType)) return false;
        if (!colorTemperature.equals(that.colorTemperature)) return false;
        return energyRating.equals(that.energyRating);
    }

    @Override
    public int hashCode() {
        int result = bulbType.hashCode();
        result = 31 * result + wattage;
        result = 31 * result + colorTemperature.hashCode();
        result = 31 * result + lumens;
        result = 31 * result + lifeHours;
        result = 31 * result + energyRating.hashCode();
        return result;
    }
}
