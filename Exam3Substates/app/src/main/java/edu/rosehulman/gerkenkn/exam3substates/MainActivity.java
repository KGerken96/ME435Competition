package edu.rosehulman.gerkenkn.exam3substates;

import android.app.Activity;
import android.location.Location;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import edu.rosehulman.me435.AccessoryActivity;
import edu.rosehulman.me435.FieldGpsListener;
import edu.rosehulman.me435.NavUtils;

public class MainActivity extends AccessoryActivity implements FieldGpsListener {

    private TextView mBall1Textview,mBall2Textview,mBall3Textview,mHighLevelStateTextview,
    mMissionSubstateTextview,mGpsTextview,mTargetXYTextview,mTargetHeadingTextview,mTurnAmountTextview,
    mCommandTextview;

    private static final String TAG = "OneGoodGps";
    private static final double NO_HEADING_KNOWN = 360.0;
    private int mGpsCounter = 0;
    private double mCurrentGpsX, mCurrentGpsY, mCurrentGpsHeading, mCurrentCalculatedGpsHeading;
    private double mCurrentSensorHeading;
    private double mTargetX, mTargetY;

    public enum highLevelState{ READY_FOR_MISSION,INITIAL_STRAIGHT,NEAR_BALL_MISSION,FAR_BALL_MISSION,HOME_CONE_MISSION,
    WAITING_FOR_PICKUP,SEEKING_HOME}

    public enum missionSubstate{INACTIVE, GPS_SEEKING, IMAGE_REC_SEEKING,OPTIONAL_SCRIPT}

    private Handler mCommandHandler = new Handler();
    private Timer mTimer;
    public static final int LOOP_INTERVAL_MS = 100;






    private highLevelState mHighLevelState = highLevelState.READY_FOR_MISSION;
    private missionSubstate mMissionSubstate = missionSubstate.INACTIVE;
    private long mHighLevelStateStartTime;
    private long mSubstateStartTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBall1Textview=findViewById(R.id.ball_1_textview);
        mBall2Textview=findViewById(R.id.ball_2_textview);
        mBall3Textview=findViewById(R.id.ball_3_textview);
        mHighLevelStateTextview = findViewById(R.id.high_level_state_textview);
        mMissionSubstateTextview=findViewById(R.id.mission_substate_textview);
        mGpsTextview=findViewById(R.id.gps_textview);
        mTargetXYTextview=findViewById(R.id.target_xy_textview);
        mTargetHeadingTextview=findViewById(R.id.target_heading_textview);
        mTurnAmountTextview=findViewById(R.id.turn_amount_textview);
        mCommandTextview=findViewById(R.id.command_textview);

        setHighLevelState(highLevelState.READY_FOR_MISSION);
        setSubstate(missionSubstate.INACTIVE);


    }

    private void setHighLevelState(highLevelState newHLState){
        mHighLevelStateStartTime=System.currentTimeMillis();
        mHighLevelStateTextview.setText(newHLState.name());

        switch(newHLState){
            case READY_FOR_MISSION:
                setSubstate(missionSubstate.INACTIVE);
                break;
            case INITIAL_STRAIGHT:
                break;
            case NEAR_BALL_MISSION:
                setSubstate(missionSubstate.GPS_SEEKING);
                onLocationChanged(60,-25,-30,null);
                break;
            case FAR_BALL_MISSION:
                onLocationChanged(90,-50,-46,null);
                //mTargetXYTextview.setText("(240, 50)");
                mTargetX=240;
                mTargetY=50;

                mTargetXYTextview.setText("("+(int)mTargetX+", "+(int)mTargetY+")");
                break;
            case HOME_CONE_MISSION:
                onLocationChanged(240,50,45,null);
               // mTargetXYTextview.setText("(0, 0)");
                mTargetX=0;
                mTargetY=0;

                mTargetXYTextview.setText("("+(int)mTargetX+", "+(int)mTargetY+")");
                break;
            case WAITING_FOR_PICKUP:
                setSubstate(missionSubstate.INACTIVE);
                break;
            case SEEKING_HOME:
                break;

        }

        mHighLevelState=newHLState;
    }

    private void setSubstate(missionSubstate newSubstate){
        mSubstateStartTime=System.currentTimeMillis();

        if(newSubstate==missionSubstate.INACTIVE){
            mMissionSubstateTextview.setText("---");
        }else {
            mMissionSubstateTextview.setText(newSubstate.name());
        }


        switch (newSubstate){

            case INACTIVE:

                break;
            case GPS_SEEKING:

                break;
            case IMAGE_REC_SEEKING:
                break;
            case OPTIONAL_SCRIPT:
                if(mHighLevelState==highLevelState.NEAR_BALL_MISSION) {
                    nearBallScript();
                }else if(mHighLevelState==highLevelState.FAR_BALL_MISSION){
                    farBallScript();
                }else if(mHighLevelState==highLevelState.HOME_CONE_MISSION||mHighLevelState==highLevelState.SEEKING_HOME){
                    homeConeScript();
                }
                break;
        }

        mMissionSubstate=newSubstate;

    }

    private void loop(){
        //DONE update the ui to show the time in the current state

        if(mHighLevelState!=highLevelState.READY_FOR_MISSION){
            mHighLevelStateTextview.setText(""+mHighLevelState+" "+getStateTimeMS()/1000);
        }

        if(mMissionSubstate!=missionSubstate.INACTIVE){
            mMissionSubstateTextview.setText(""+mMissionSubstate+" "+getSubstateTimeMS()/1000);
        }

        if(mMissionSubstate!=missionSubstate.GPS_SEEKING){

            mTargetHeadingTextview.setText("---");
            mTurnAmountTextview.setText("---");
            mCommandTextview.setText("---");
        }

        switch(mHighLevelState){
            case READY_FOR_MISSION:
                break;
            case INITIAL_STRAIGHT:
                if (getStateTimeMS()>4000){
                    setHighLevelState(highLevelState.NEAR_BALL_MISSION);
                }
                break;
            case NEAR_BALL_MISSION:
                break;
            case FAR_BALL_MISSION:
                break;
            case HOME_CONE_MISSION:
                break;
            case WAITING_FOR_PICKUP:
                if(getStateTimeMS()>5000){
                    setHighLevelState(highLevelState.SEEKING_HOME);
                    setSubstate(missionSubstate.GPS_SEEKING);
                }
                break;
            case SEEKING_HOME:
                if(getStateTimeMS()>5000){
                    setHighLevelState(highLevelState.WAITING_FOR_PICKUP);
                    setSubstate(missionSubstate.INACTIVE);
                }
                break;

        }

        switch (mMissionSubstate){


            case INACTIVE:
                break;
            case GPS_SEEKING:

                mCurrentCalculatedGpsHeading = Math.round(NavUtils.getTargetHeading(mCurrentGpsX,
                        mCurrentGpsY,mTargetX,mTargetY));
                mTargetHeadingTextview.setText(""+(int)mCurrentCalculatedGpsHeading+"°");
                double leftTurnAmount = Math.round(NavUtils.getLeftTurnHeadingDelta(mCurrentGpsHeading,mCurrentCalculatedGpsHeading));
                double rightTurnAmount = Math.round(NavUtils.getRightTurnHeadingDelta(mCurrentGpsHeading,mCurrentCalculatedGpsHeading));

                if(leftTurnAmount<rightTurnAmount){
                    mTurnAmountTextview.setText("Left "+(int)leftTurnAmount+"°");
                    mCommandTextview.setText("WHEEL SPEED FORWARD "+(int)(255-leftTurnAmount)+" FORWARD 255");
                }else{
                    mTurnAmountTextview.setText("Right "+(int)rightTurnAmount+"°");
                    mCommandTextview.setText("WHEEL SPEED FORWARD 255 FORWARD "+(int)(255-rightTurnAmount));
                }

                if(getSubstateTimeMS()>15000){
                    setSubstate(missionSubstate.OPTIONAL_SCRIPT);
                }
                break;
            case IMAGE_REC_SEEKING:
                break;
            case OPTIONAL_SCRIPT:
                break;
        }
    }

    private long getStateTimeMS(){
        return System.currentTimeMillis()-mHighLevelStateStartTime;
    }
    private long getSubstateTimeMS(){return System.currentTimeMillis()-mSubstateStartTime;}

    private void nearBallScript(){
        //remove red ball at time=?
        //change state to far ball mission at 4s, substate set to gps seeking
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBall1Textview.setText("---");
                setHighLevelState(highLevelState.FAR_BALL_MISSION);
                setSubstate(missionSubstate.GPS_SEEKING);
            }
        }, 4000);
    }

    private void farBallScript(){
        //remove blue ball at 4s
        //remove white ball at 8s
        //set state to home cone mission at 8s, substate to gps seeking
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBall3Textview.setText("---");
            }
        }, 4000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBall2Textview.setText("---");
                setHighLevelState(highLevelState.HOME_CONE_MISSION);
                setSubstate(missionSubstate.GPS_SEEKING);
            }
        }, 8000);
    }

    private void homeConeScript(){
        //stop the robot
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
               setHighLevelState(highLevelState.WAITING_FOR_PICKUP);
               //setSubstate(missionSubstate.GPS_SEEKING);
            }
        }, 1000);

    }

    @Override
    public void onLocationChanged(double x, double y, double heading, Location location) {
        mGpsCounter++;
        mCurrentGpsX=x;
        mCurrentGpsY=y;
        mCurrentGpsHeading=NO_HEADING_KNOWN;
        String gpsInfo = getString(R.string.xy_format,x,y);
        if (heading <=180.0 && heading >-180.0){
            gpsInfo+= " " + getString(R.string.degrees_format, heading);
            mCurrentGpsHeading=heading;
            //mFieldOrientation.setCurrentFieldHeading(heading);
            //mCurrentSensorHeading=heading;
//            if (mState==State.WAITING_FOR_GPS){
//                setState(State.DRIVING_HOME);
//            }
        }else{
            gpsInfo+= "?°";
        }
        gpsInfo+="    " + mGpsCounter;

        mGpsTextview.setText(gpsInfo);
        //mSensorOrientationTextView.setText(getString(R.string.degrees_format, heading));
    }


    @Override
    protected void onStart() {
        super.onStart();
        mTimer= new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        loop();
                    }
                });
            }
        }, 0, LOOP_INTERVAL_MS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTimer.cancel();
        mTimer=null;
    }

    public void handleGo(View view){
        if(mHighLevelState==highLevelState.READY_FOR_MISSION){
            setHighLevelState(highLevelState.INITIAL_STRAIGHT);
        }
        mTargetX=90;
        mTargetY=-50;
        //mTargetXYTextview.setText("(90, -50)");
        mTargetXYTextview.setText("("+(int)mTargetX+", "+(int)mTargetY+")");
    }
    public void handleReset(View view){
        setHighLevelState(highLevelState.READY_FOR_MISSION);
        mBall1Textview.setText("Red\nBall");
        mBall2Textview.setText("White\nBall");
        mBall3Textview.setText("Blue\nBall");
        mTargetXYTextview.setText("---");
        mGpsTextview.setText("---");
        mTargetHeadingTextview.setText("---");
        mTurnAmountTextview.setText("---");
        mCommandTextview.setText("---");

    }
    public void handleNotSeen(View view){
        if(mMissionSubstate==missionSubstate.IMAGE_REC_SEEKING){
            setSubstate(missionSubstate.GPS_SEEKING);
        }

    }
    public void handleSeenSmall(View view){
        if(mMissionSubstate==missionSubstate.GPS_SEEKING){
            setSubstate(missionSubstate.IMAGE_REC_SEEKING);
        }

    }
    public void handleSeenBig(View view){
        if(mMissionSubstate==missionSubstate.IMAGE_REC_SEEKING){

                setSubstate(missionSubstate.OPTIONAL_SCRIPT);

        }

    }

    public void handleMissionComplete(View view){
        if(mHighLevelState==highLevelState.WAITING_FOR_PICKUP){
            setHighLevelState(highLevelState.READY_FOR_MISSION);
            mBall1Textview.setText("Red\nBall");
            mBall2Textview.setText("White\nBall");
            mBall3Textview.setText("Blue\nBall");
        }

    }


}


