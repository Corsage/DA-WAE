package jc.edu.corsage.dawae.kudan;

import android.util.Log;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import eu.kudan.kudan.ARArbiTrack;
import eu.kudan.kudan.ARGyroPlaceManager;
import eu.kudan.kudan.ARImageNode;
import eu.kudan.kudan.ARNode;
import jc.edu.corsage.dawae.mapquest.collections.TurnType;
import jc.edu.corsage.dawae.mapquest.models.Maneuver;
import jc.edu.corsage.dawae.mapquest.models.Route;
import jc.edu.corsage.dawae.mapquest.models.StartPoint;
import jc.edu.corsage.dawae.utils.Utils;

/**
 * Created by j3chowdh on 1/20/2018.
 */

public class Kudan {
    private String TAG = "Kudan";

    private String API_KEY = "cMPCUQgMNIDiqcJ5FxDD/0pZdSuZto+29RmsYGgHpS+F95hm46+DIr5KbSFRy3+LTcj3kqGaNzDwpmKDVzQsK0hKUAuu1c1+W+nab95tcDrriHyGHiCiZYBQXZmJSXUxF7ff75yQe4wInQMHB6w26ixMSzlAOA1b56CbV06VUDm1X7D3SiBdxQpbBnVx27I38qvA1b1hb7c3frwzXiOgw1fag3LDeob7UimJaj4TG28IGMitn8GvVxKzEd6FmZWP7P/P0/lMGyeIOqoo6bd5cnfNoNNJIdEmbzi952BeU0qSUu46xj7wbN9mHOXqtETtQSJ9Dt1a0QotD8FUL+WOAIyvnYraHkDdnM772S8gKSG6m6S3RMwrcuWJR2Uo9c38INgd37kcFe5tvTJizYtmQoPX0aowLHTmJaxWhVfrWqFXLSA0kj4u8onwGncLpS+RC5QA5yM1lkORoouLHrKOXOWmp4veq9pU1QOIjSkPZo1hqNd7PcoB8R7jEA21ACgvE+j5/LZes940xCqb9CrvpDbmEmfwxpk0A6+FjPoFOFRh0W25WzSt1vCv6cffP/J9gbHEVHL9YKMg6kT8f6hNUQkM4ysU3IfRaE92i3aJ/H0ovPUGDO/lhy/aNKKu6kpB4jne+bBbofJSSN5zn3gNNh6lkC6JvaWU5JG6oX19s5Q=";

    public static Route route;

    public enum KUDAN_STATES { REST, START, INTERSECTION, REACHED }
    public static KUDAN_STATES kudanStates;

    public static float[] degrees;

    public static int UPPER_BOUND;
    public static int CURRENT_MANUEVER;

    public static String DIRECTION_STRING;

    public Kudan() {
        kudanStates = KUDAN_STATES.REST;
        degrees = new float[] { 0.0f, 0.0f, 0.0f };
        UPPER_BOUND = -1;
        CURRENT_MANUEVER = -1;
        DIRECTION_STRING = "KEK";
        route = null;
    }

    public void addRoutes(Route route) {
        Kudan.route = route;
        UPPER_BOUND = route.legs.get(0).maneuvers.size();
    }

    public void start() {
        CURRENT_MANUEVER = 0;
        kudanStates = KUDAN_STATES.START;
    }

    /** Contains the vectors required to transform an image in the four basic directions.
     * 0 - Right
     * 1 - Left
     * 2 - Forward
     * 3 - Backward
     */
    private float[][] directionalVectors = {
            { (float)Math.PI/2.0f, (float)Math.PI/2.0f, 0.0f },
            { -(float)Math.PI/2.0f, -(float)Math.PI/2.0f, 0.0f },
            { (float)Math.PI/2.0f, (float)Math.PI, 0.0f },
            { (float)Math.PI/2.0f, 0.0f, 0.0f }
    };

    /* Public Methods */

    float[] setDirectionalVector(Maneuver maneuver) {
        float[] temp = { 0.0f, 0.0f, 0.0f };
        switch(maneuver.turnType) {
            case 0:
                 temp =  new float[] { (float)Math.PI/2.0f, (float)Math.PI, 0.0f };
                break;
            case 1: temp = new float[] { (float)Math.PI/2.0f, (float)Math.PI/3.0f, 0.0f };
                break;
            case 2:
                break;
            case 3:
                temp =  new float[] { (float)Math.PI/2.0f, (float)Math.PI/2.0f, 0.0f };
                break;
            case 4:
                temp =  new float[] { (float)Math.PI/2.0f, 0.0f, 0.0f };
                break;
            case 5:
                temp =  new float[] { -(float)Math.PI/2.0f, -(float)Math.PI/2.0f, 0.0f };
                break;
            case 6:
                break;
            case 7:
                break;
        }

        return temp;
    }

    /** Creates an image based on rotation and position.
     * @param imageName
     * @param orientation
     * @param scale
     */
    public ARImageNode createImageNode(String imageName, Quaternion orientation, Vector3f scale)
    {
        ARImageNode imageNode = new ARImageNode(imageName);
        imageNode.setOrientation(orientation);
        imageNode.setScale(scale);

        return imageNode;
    }

    /** Adds the node to the Gyroplace manager.
     * @param node
     */
    public void addNodeToGyroPlaceManager(ARNode node)
    {
        // The gyroplacemanager positions it's world on a plane that represents the floor.
        // You can adjust the floor depth (The distance between the device and the floor) using ARGyroPlaceManager's floor depth variable.
        // The default floor depth is -150
        ARGyroPlaceManager gyroPlaceManager = ARGyroPlaceManager.getInstance();
        gyroPlaceManager.initialise();
        gyroPlaceManager.getWorld().addChild(node);
    }

    /** Sets up the ArbiTrack (Kudan specific feature).
     * Locks a node in place by tracking a set of feature points in the environment.
     * @param targetNode
     * @param childNode
     */
    public void setUpArbiTrack(ARNode targetNode, ARNode childNode)
    {
        // Get the arbitrack manager and initialise it
        ARArbiTrack arbiTrack = ARArbiTrack.getInstance();
        arbiTrack.initialise();

        // Set it's target node
        arbiTrack.setTargetNode(targetNode);

        // Add the tracking image node to the arbitrack world
        arbiTrack.getWorld().addChild(childNode);
    }

    /**
     * A L G O R I T H M
     * This is the part where we don't
     * know what we are doing.
     */


}
