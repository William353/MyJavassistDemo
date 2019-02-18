package com.example.android.javassistdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

    Button button1;
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("print onCreate");

        button1 = findViewById(R.id.btn1);
        button2 = findViewById(R.id.btn2);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("button1 click");
            }
        });
        button2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == button2) {
            System.out.println("button2 click");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        System.out.println("print onResume");

    }
}
