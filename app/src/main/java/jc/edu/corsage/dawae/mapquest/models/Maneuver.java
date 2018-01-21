package jc.edu.corsage.dawae.mapquest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by j3chowdh on 1/20/2018.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class Maneuver {
    public double distance;
    public String narrative;
    public StartPoint startPoint;
    public String directionName;
    public int turnType;
}
