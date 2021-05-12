package com.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Rule {

    private final ItemSet prior;
    private final ItemSet result;
    private double unionSupport;
    private double confidence;
    private double lift;

    public Rule(ItemSet prior, ItemSet result) {
        this.prior = prior;
        this.result = result;
    }

    public double getUnionSupport() {
        return unionSupport;
    }

    public Rule setUnionSupport(double unionSupport) {
        this.unionSupport = unionSupport;
        return this;
    }

    public double getLift() {
        return lift;
    }

    public void setLift(double lift) {
        this.lift = lift;
    }

    public ItemSet getPrior() {
        return prior;
    }

    public ItemSet getResult() {
        return result;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String prettyPrint() {
        return Arrays.toString(getPrior().getItems()) + " --> " + Arrays.toString(getResult().getItems())
                + " {" + Map.of("Lift", formatDouble(lift), "Confidence", formatPercentage(confidence), "Support", formatPercentage(unionSupport))
                .entrySet().stream().map(en -> en.getKey() + ": " + en.getValue()).collect(Collectors.joining(", "))
                + "}";
    }

    public String csvPrint()
    {
        return Stream.of(getPrior().getItems()).collect(Collectors.joining("|"))
                + "-->"
                + Stream.of(getResult().getItems()).collect(Collectors.joining("|"))
                + ","
                + formatDouble(lift) + "," + formatPercentage(confidence) + "," + formatPercentage(unionSupport);
    }


    private String formatPercentage(double num)
    {
        return String.format("%.2f", (100*num)) + "%";
    }

    private String formatDouble(double num)
    {
        return String.format("%.2f", num);
    }
}
