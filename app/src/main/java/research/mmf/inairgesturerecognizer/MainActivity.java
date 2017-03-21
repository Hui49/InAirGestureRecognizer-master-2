package research.mmf.inairgesturerecognizer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Environment;
import android.content.Context;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;






import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import research.mmf.gesturelib.*;

/***
 * Demo Application
 * Author: Mingming Fan
 * Contact: fmmbupt@gmail.com
 * Contact the author for use of the code
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textView_gesture_name;
    private Button bt_start_recognition;
    private Button bt_store;
    private Button bt_delete;

    private Boolean recording_sample_gestures = true;

    private SensorManager mSensorManager;
    private DTWGestureRecognition Recognizer;

    private ArrayList<ArrayList<AccData>> templates = new ArrayList<ArrayList<AccData>>();
    private ArrayList<ArrayList<AccData>> templates1 = new ArrayList<ArrayList<AccData>>();
    private ArrayList<AccData> template1 = new ArrayList<AccData>();


    private ArrayList<String> gesture_names = new ArrayList<>();
    private ArrayList<String> gesture_names1 = new ArrayList<>();
    private ArrayList<AccData> SensorData = new ArrayList<AccData>();   //gesture data that is to be recognized
    private int gesture_id = 0;
    private Boolean StoreSensorData = false;
    private Boolean AskforStore = false;
    private Boolean Stored = false;
    private Boolean LastStoreSensorData = false;
    //   private Boolean CheckNext = false;
    private Boolean StartAct = false;
    private int count = 0;
    long tStart = Long.MAX_VALUE - 2000;
    int lastcount = 0;
    int currentcount = 0;
    private ArrayList<Float> acc = new ArrayList<>();
    boolean check = false;
    int num = 0 ;
    int num2 = 0;
    int num3 = 0;
    private MediaPlayer mPlayer = null;


    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private float mAccel1; // acceleration apart from gravity
    private float mAccelCurrent1; // current acceleration including gravity
    private float mAccelLast1; // last acceleration including gravity
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView_gesture_name = (TextView) findViewById(R.id.editText_gesture_name);

        bt_start_recognition = (Button) findViewById(R.id.button_record_gesture);
        bt_store = (Button) findViewById(R.id.button_yes);
        bt_store.setText("Save");
        bt_delete= (Button) findViewById(R.id.button_no);
        bt_delete.setText("Delete");
        bt_start_recognition.setOnClickListener(this);
        //  bt_delete.setOnClickListener();


        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mSensorManager.registerListener(acc_listener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                mSensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(acc_listener1,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                mSensorManager.SENSOR_DELAY_FASTEST);

        Recognizer = new DTWGestureRecognition();

        bt_delete.setVisibility(View.INVISIBLE);
        bt_store.setVisibility(View.INVISIBLE);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        mAccel1 = 0.00f;
        mAccelCurrent1 = SensorManager.GRAVITY_EARTH;
        mAccelLast1 = SensorManager.GRAVITY_EARTH;
        MediaPlayer mediaPlayer = new MediaPlayer();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        MediaPlayer mediaPlayerA = MediaPlayer.create(this, R.raw.soundfilea);
    }


    public void onClick(View v) {



        if (recording_sample_gestures) {
            ReadFile();
            bt_start_recognition.setText("Click to enter new templates");
        } else {
            bt_start_recognition.setText("Click to recognize gestures");
        }

        recording_sample_gestures = !recording_sample_gestures;


    }


    private SensorEventListener acc_listener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        public void onSensorChanged(SensorEvent event) {

            if(StoreSensorData)
            {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                AccData  data = new AccData(x,y,z);
                Log.d("vi", ""+x+ " "+ y +" "+z );

                Recognizer.Quantization(data);

                if(recording_sample_gestures) {
                    if(templates!=null)
                        templates.get(gesture_id).add(data);
                    template1.add(data);
                }
                else
                {
                    SensorData.add(data);
                }
            }




        }

    };
    private SensorEventListener acc_listener1 = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        public void onSensorChanged(SensorEvent event) {
            //  Log.d( "hi", "h1111");
            float x1 = event.values[0];
            float y1 = event.values[1];
            float z1 = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (z1 * z1));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter

            //   boolean check1 = false;

            mAccelLast1 = mAccelCurrent1;
            mAccelCurrent1 = (float) Math.sqrt((double) (x1 * x1 + y1 * y1 + z1 * z1));
            float delta1 = mAccelCurrent1 - mAccelLast1;
            mAccel1 = mAccel1 * 0.9f + delta1; // perform low-cut filter

//activation function
            if (count == 0) {
                if (mAccel < -4) {
                    num = num + 1;
                    //Log.d("hi", "check" + mAccel);
                }

                if (num > 5) {
                    if (mAccel > 4) {
                        num2 = num2 + 1;
                        // Log.d("hi", "check" + mAccel);

                    }
                    if (num2 > 5) {

                        if (mAccel < -4) {
                            num3 = num3 + 1;
                            // Log.d("hi", "check" + mAccel);
                        }
                        if (num3 > 5) {
                            if (Math.abs(mAccel) < 0.7) {
                                count = 1;
                                num = 0;
                                num2 = 0;
                                num3 = 0;
                            }


                        }

                    }
                }
            }

// deactivation function
            else if (count == 1) {
                if (Math.abs(mAccel1) < 0.07) {

                    num = num + 1;

                    if (num > 100) {

                        count = 0;
                        num = 0;


                    }
                }
            }
            lastcount = currentcount;
            currentcount = count;


            if (!recording_sample_gestures) {

                //record the data for recognition

                if (count == 1 && currentcount - lastcount == 1) {
                    Log.d("hi", "start recognition");
                    Vibrate(MainActivity.this, 400);
                    StoreSensorData = true;


                }
                else if (count == 0 && currentcount - lastcount == -1) {
                    Log.d("hi", "stop recognition ");
                    StoreSensorData = false;
                    // Vibrate(MainActivity.this, 400);

                    // mPlayer = MediaPlayer.create(this, R.raw.soundfilea);
//
//                    MediaPlayer mp = new MediaPlayer();
//                    try {
//                        mp.setDataSource("/res/raw/soundfilea.wav");
//                        mp.prepare();
//                        mp.start();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//

                    int WhichGesture = Recognizer.GestureRecognition(templates1, SensorData);
                    Log.d("hi", "WhichGesture" + WhichGesture);
                    String gestureRecognized = gesture_names1.get(WhichGesture);
                    //   Toast.makeText(getApplicationContext(), "It is " + WhichGesture, Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "It is " + gestureRecognized, Toast.LENGTH_LONG).show();
                    SensorData.clear();
                    getSound(gestureRecognized);

                }
            }
            else {
                if (count == 1 && currentcount - lastcount == 1) {
                    Vibrate(MainActivity.this, 400);
                    Log.d("hi", "start recording");

                    StoreSensorData = true;

                    templates.add(new ArrayList<AccData>());
                    template1.clear();


                }
                else if (count == 0 && currentcount - lastcount == -1) {
                    Log.d("hi", "stop recording ");
                    StoreSensorData = false;
                    Vibrate(MainActivity.this, 400);


                    //AskforStore = true;
                    bt_store.setVisibility(View.VISIBLE);
                    bt_delete.setVisibility(View.VISIBLE);

                    bt_store.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                            String gesture = textView_gesture_name.getText().toString().toUpperCase();
                            gesture_names.add(gesture);
                            writeToFile(gesture,template1);
                            //  ReadFile();
                            gesture_id++;
                            Toast.makeText(getApplicationContext(), "Enter the " + gesture + " template.", Toast.LENGTH_LONG).show();
                            bt_store.setVisibility(View.INVISIBLE);
                            bt_delete.setVisibility(View.INVISIBLE);
                        }
                    });

                    bt_delete.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                            templates.remove(templates.size() - 1);
                            Log.d("hi", "" +templates.size());
                            bt_store.setVisibility(View.INVISIBLE);
                            bt_delete.setVisibility(View.INVISIBLE);
                        }
                    });
//                    if(Stored) {
//                        String gesture = textView_gesture_name.getText().toString();
//                        gesture_names.add(gesture);
//                        // writeToFile(template1);
//                        //  ReadFile();
//                        gesture_id++;
//                        Toast.makeText(getApplicationContext(), "Enter the " + gesture + " template.", Toast.LENGTH_LONG).show();
//                    }
//                    else{
//                        Log.d("hi", "" +templates.size());
//                        templates.remove(templates.size() - 1);
//
//                    }
                }


            }

        }
    };

    public boolean onTouchEvent(MotionEvent event){

        if(recording_sample_gestures)
        {
            //record the data for templates
            switch(event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    Log.d("hi", "hihihi" );



//                    StoreSensorData = true;
//                    templates.add(new ArrayList<AccData>());
//                    template1.clear();
//                    return false;
                case MotionEvent.ACTION_UP:
//                    StoreSensorData = false;
//                    String gesture = textView_gesture_name.getText().toString();
//                    switch(gesture){
//                        case "A":
//                            Log.v("l","a");
//                            templateA.add(template1)  ;
//                        case "B":
//                            templateB.add(template1)  ;
//                        case "C":
//                            templateC.add(template1);
//                        case "D":
//                            templateD.add(template1) ;
//                        case "E":
//                            templateE.add(template1) ;
//                        case "F":
//                            templateF.add(template1) ;
//                        case "G":
//                            templateG.add(template1);
//                        case "H":
//                            templateH.add(template1);
//                        case "I":
//                            templateI.add(template1);
//                        case "J":
//                            templateJ.add(template1);
//                        case "K":
//                            templateK.add(template1);
//                        case "L":
//                            templateL.add(template1);
//                        case "M":
//                            templateM.add(template1);
//                        case "N":
//                            templateN.add(template1);
//                        case "O":
//                            templateO.add(template1);
//                        case "P":
//                            templateP.add(template1) ;
//                        case "Q":
//                            templateQ.add(template1);
//                        case "R":
//                            templateR.add(template1);
//                        case "S":
//                            templateS.add(template1) ;
//                        case "T":
//                            templateT.add(template1) ;
//                        case "U":
//                            templateU.add(template1) ;
//                        case "V":
//                            templateV.add(template1);
//                        case "W":
//                            templateW.add(template1);
//                        case "X":
//                            templateX.add(template1) ;
//                        case "Y":
//                            templateY.add(template1) ;
//                        case "Z":
//                            templateZ.add(template1);
//                    }
//                    gesture_names.add(gesture);
//
//                    writeToFile(template1);
//                    ReadFile();
//
//                    gesture_id++;
//                    Toast.makeText(getApplicationContext(),"Enter the " + gesture+ " template.", Toast.LENGTH_LONG).show();
//                   // Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
//                    return false;
                default:
                    break;
            }

        }





        return super.onTouchEvent(event);
    }

    public void getSound(String gesture){
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        switch (gesture) {
            case "A":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a);
                break;
            case "B":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.b);
                break;
            case "C":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.c);
                break;
            case "D":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.d);
                break;
            case "E":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.e);
                break;
            case "F":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.f);
                break;
            case "G":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.g);
                break;
            case "H":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.h);
                break;
            case "I":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.i);
                break;
            case "J":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.j);
                break;
            case "K":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.k);
                break;
            case "L":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.l);
                break;
            case "M":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.m);
                break;
            case "N":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.n);
                break;
            case "O":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.o);
                break;
            case "P":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.p);
                break;
            case "Q":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.q);
                break;
            case "R":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.r);
                break;
            case "S":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.s);
                break;
            case "T":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.t);
                break;
            case "U":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.u);
                break;
            case "V":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.v);
                break;
            case "W":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.w);
                break;
            case "X":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.x);
                break;
            case "Y":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.y);
                break;
            case "Z":
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.z);
                break;
        }
        mPlayer.start();
    }



    public void writeToFile(String Gesture_name, ArrayList<AccData> template) {
        // add-write text into file
        File dir = getFilesDir();
        File file = new File(dir, "abc5.txt");
        boolean deleted = file.delete();
        String s = "";
        try {
            FileOutputStream fileout=openFileOutput("abc4.txt", MODE_APPEND );
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            PrintWriter pw = new PrintWriter(outputWriter);
            pw.write(Gesture_name);
            pw.println();
            for (AccData data : template) {
                float x = data.getX();
                float y = data.getY();
                float z = data.getZ();
                s = x + " " + y + " "+z;
                pw.write(s);
                pw.println();
            }

            fileout.flush();
            outputWriter.close();

            //display file saved message
            Log.d("hi","File saved successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void ReadFile() {

        //reading text from file
        ArrayList<AccData> tem = new ArrayList<AccData>();
        try {

            FileInputStream fileIn=openFileInput("abc4.txt");

            InputStreamReader InputRead= new InputStreamReader(fileIn);
            BufferedReader reader = new BufferedReader(InputRead);
            Log.d("hi", ""+fileIn.available());

            // FILL BUFFER WITH DATA
            // fileIn.read(buffer);
            int i = 0 ;
            // while(reader.ready())
            String line = null;

            while ((line = reader.readLine()) != null) {
                if("ABCDEFGHIJKLMNOPQRSTUVWXYZ".contains(line)){
                    gesture_names1.add(line);
                    templates1.add(tem);
                    tem.clear();
                }
                else {
                    float[] accdata = new float[3];
                    AccData data1 = null;
                    String[] parts = line.split(" ");
                    int index = 0;
                    for (String part : parts) {
                        float in = Float.parseFloat(part);
                        accdata[i] = in;
                        i = i + 1;
                        data1 = new AccData(accdata[0],accdata[1],accdata[2]);
                    }
                    tem.add(data1);
                    Log.d("hi", ""+accdata[0]+" "+accdata[1]+accdata[2]);

                }




            }

            InputRead.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

        // Log.d("hi",ret);
        // return ret;
        //return readString;
    }


    public static void Vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }
//    public static void PlayMusic( MediaPlayer player, String sound) {
//
//        player = MediaPlayer.create(this,R.raw.fly);
//    }



    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
