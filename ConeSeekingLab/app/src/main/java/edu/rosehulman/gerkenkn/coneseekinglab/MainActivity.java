package edu.rosehulman.gerkenkn.coneseekinglab;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import edu.rosehulman.me435.FieldGpsListener;
import edu.rosehulman.me435.FieldOrientationListener;

public class MainActivity extends AppCompatActivity implements FieldGpsListener,FieldOrientationListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public void onLocationChanged(double x, double y, double heading, Location location) {

    }

    @Override
    public void onSensorChanged(double fieldHeading, float[] orientationValues) {

    }
}
