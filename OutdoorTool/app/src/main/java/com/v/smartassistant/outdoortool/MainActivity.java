package com.v.smartassistant.outdoortool;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.v.smartassistant.outdoortool.Activities.AngleSquareActivity;
import com.v.smartassistant.outdoortool.Activities.LevelActivity;
import com.v.smartassistant.outdoortool.Activities.NoiseActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button bt1, bt2, bt3, bt4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        bt1 = (Button) findViewById(R.id.level);
        bt1.setOnClickListener(this);

        bt2 = (Button) findViewById(R.id.angle);
        bt2.setOnClickListener(this);

        bt3 = (Button) findViewById(R.id.noise);
        bt3.setOnClickListener(this);

        bt4 = (Button) findViewById(R.id.magnifier);
        bt4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.level:
                openFace(LevelActivity.class);
                break;
            case R.id.angle:
                openFace(AngleSquareActivity.class);
                break;
            case R.id.noise:
                openFace(NoiseActivity.class);
                break;
            case R.id.magnifier:
                openFace(MagnifierActivity.class);
                break;
        }
    }

    private void openFace(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

}