package com.example.myapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;


import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button myButton;

    @Override
protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    myButton = findViewById(R.id.myButton);

    try {

        InputStream input = getAssets().open("config.yml");

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(input);

        Map<String, Object> app = (Map<String, Object>) data.get("buttonTitles");
        String name = (String) app.get("main");

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
            }
        });

    } catch (Exception e) {
        e.printStackTrace();
    }
}
}