package jc.edu.corsage.dawae;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import eu.kudan.kudan.ARAPIKey;
import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARArbiTrack;
import eu.kudan.kudan.ARGyroPlaceManager;
import eu.kudan.kudan.ARImageNode;
import eu.kudan.kudan.ARLightMaterial;
import eu.kudan.kudan.ARMeshNode;
import eu.kudan.kudan.ARModelImporter;
import eu.kudan.kudan.ARModelNode;
import eu.kudan.kudan.ARNode;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import jc.edu.corsage.dawae.kudan.Kudan;
import jc.edu.corsage.dawae.mapquest.MapQuest;
import jc.edu.corsage.dawae.mapquest.models.Kirito;
import jc.edu.corsage.dawae.mapquest.models.Route;
import jc.edu.corsage.dawae.utils.Utils;

public class MainActivity extends ARActivity implements GestureDetector.OnGestureListener {
    private String TAG = "MainActivity";

    private String API_KEY = "cMPCUQgMNIDiqcJ5FxDD/0pZdSuZto+29RmsYGgHpS+F95hm46+DIr5KbSFRy3+LTcj3kqGaNzDwpmKDVzQsK0hKUAuu1c1+W+nab95tcDrriHyGHiCiZYBQXZmJSXUxF7ff75yQe4wInQMHB6w26ixMSzlAOA1b56CbV06VUDm1X7D3SiBdxQpbBnVx27I38qvA1b1hb7c3frwzXiOgw1fag3LDeob7UimJaj4TG28IGMitn8GvVxKzEd6FmZWP7P/P0/lMGyeIOqoo6bd5cnfNoNNJIdEmbzi952BeU0qSUu46xj7wbN9mHOXqtETtQSJ9Dt1a0QotD8FUL+WOAIyvnYraHkDdnM772S8gKSG6m6S3RMwrcuWJR2Uo9c38INgd37kcFe5tvTJizYtmQoPX0aowLHTmJaxWhVfrWqFXLSA0kj4u8onwGncLpS+RC5QA5yM1lkORoouLHrKOXOWmp4veq9pU1QOIjSkPZo1hqNd7PcoB8R7jEA21ACgvE+j5/LZes940xCqb9CrvpDbmEmfwxpk0A6+FjPoFOFRh0W25WzSt1vCv6cffP/J9gbHEVHL9YKMg6kT8f6hNUQkM4ysU3IfRaE92i3aJ/H0ovPUGDO/lhy/aNKKu6kpB4jne+bBbofJSSN5zn3gNNh6lkC6JvaWU5JG6oX19s5Q=";

    private GestureDetectorCompat gestureDetect;
    private CompositeDisposable mCompositeDisposable;

    private TextView mainDirectionText;

    private String locationTo;

    private MapQuest mapQuest;
    private Kudan kudan;

    private Kirito kirito;

    public static Quaternion floorOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location from intent.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            locationTo = extras.getString("TO_LOCATION");
            Log.d(TAG, "Location recieved: " + locationTo);
        }

        // Create gesture recogniser to start and stop ArbiTrack
        gestureDetect = new GestureDetectorCompat(this, this);

        mCompositeDisposable = new CompositeDisposable();

        ARAPIKey key = ARAPIKey.getInstance();
        key.setAPIKey(API_KEY);

        mapQuest = new MapQuest();
        kudan = new Kudan();

        // Start up the asynchronous location retrieval.
        Utils.startLocationListener(this);
        Log.d(TAG, "LAT: " + Utils.DEVICE_LATITUDE + " LONG: " + Utils.DEVICE_LONGITUDE);

        setContentView(R.layout.activity_main);
        mainDirectionText = findViewById(R.id.mainDirectionText);
        start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate   menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void changeDegreesOfArrow(float[] degrees) {
        floorOrientation = new Quaternion(degrees);
    }

    /**
     * Setup Method
     * Called when the ARActivity first loads.
     */
    @Override
    public void setup() {
        super.setup();

        // AR content to be set up here.

        // We choose the orientation of our floor node so that the target node lies flat on the floor. We rotate the node by -90 degrees about the x axis
        //float[] angles = { -(float)Math.PI/2.0f, 0.0f, 0.0f};

        // 1st is z, returns flat/not flat
        // 2nd is x, returns left or right
        // 3rd is y, returns up or down

        // TODO: make full copy of angles
        // 0 - right
        // 1 - left
        // 2 - forward
        // 3 - backwards

        float[][] temp = {
                { (float)Math.PI/2.0f, (float)Math.PI/2.0f, 0.0f },
                { -(float)Math.PI/2.0f, -(float)Math.PI/2.0f, 0.0f },
                { (float)Math.PI/2.0f, (float)Math.PI, 0.0f },
                { (float)Math.PI/2.0f, 0.0f, 0.0f }
        };

        float[] angles = { (float)Math.PI/2.0f, 0.0f, 0.0f };

        floorOrientation = new Quaternion(angles);

        // Create a target node. A target node is a node whose position is used to determine the initial position of arbitrack's world when arbitrack is started
        // The target node in this case is an image node of the Kudan Cow
        Vector3f floorScale = new Vector3f(0.5f,0.5f,0.5f);
        ARImageNode floorTarget = kudan.createImageNode("arrow-right.png",floorOrientation,floorScale);
       // ARModelImporter importer = new ARModelImporter();
       // importer.loadFromAsset("arrow.jet");
       // ARModelNode knuckles = importer.getNode();

        //ARLightMaterial material = new ARLightMaterial();
        //material.setColour(0, 128, 0);

        //for (ARMeshNode meshNode : importer.getMeshNodes()) {
          //  meshNode.setMaterial(material);
        //}


        // Add our target node to the gyroplacemanager's world
        // The position of the target node is used to determine the initial position of arbitrack's world
        kudan.addNodeToGyroPlaceManager(floorTarget);
        //addNodeToGyroPlaceManager(knuckles);


        // Create an image node to place in arbitrack's world
        // We can choose the tracking node to have the same orientation as the target node
        Vector3f trackingScale = new Vector3f(1.0f,1.0f,1.0f);
        ARImageNode trackingImageNode = kudan.createImageNode("floor.jpg", floorOrientation, trackingScale);

        // Set up arbitrack
        kudan.setUpArbiTrack(floorTarget, trackingImageNode);
       // setUpArbiTrack(knuckles, trackingImageNode);
    }

    public void changeTextOfDirection(String text) {
        mainDirectionText.setText(text);
    }

    /* Gesture Events */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetect.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "Single Tap Up Event.");

        // TODO: Stuff.

        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    /* START DIS PLZ IS GETTING LATE */
    private void handleResponse(Kirito kirito) {
        Log.d(TAG, "" + kirito.route.legs.get(0).maneuvers.get(0).narrative);
        this.kirito = kirito;
        kudan.addRoutes(kirito.route);
        mainDirectionText.setText(kirito.route.legs.get(0).maneuvers.get(0).narrative);
        kudan.start();
    }

    private void handleError(Throwable error) {
        Log.d(TAG, "ERROR");
        Log.d(TAG, error.getMessage());
    }

    private void start() {
        Log.d(TAG, "START :: Retrieving JSON response on : http://www.mapquestapi.com/directions/v2/route?key=hVFeNPqxGPaxUWVMBVFyTzIkyOXsRGDK&from=" + Utils.DEVICE_LONGITUDE + "," + Utils.DEVICE_LATITUDE + "&to=" + locationTo);
        mCompositeDisposable.add(mapQuest.getRoute(Utils.DEVICE_LONGITUDE + "," + Utils.DEVICE_LATITUDE, locationTo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }


}
