package jc.edu.corsage.dawae.mapquest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by j3chowdh on 1/20/2018.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class Leg {
    public ArrayList<Maneuver> maneuvers;
}
