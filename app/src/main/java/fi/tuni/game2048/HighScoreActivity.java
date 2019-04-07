package fi.tuni.game2048;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class HighScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        String filename = "highscores.txt";
        File file = new File(getApplicationContext().getFilesDir(), filename);
        FileInputStream inputStream;
        String scores = "";
        try {
            inputStream = openFileInput(filename);
            int data = inputStream.read();
            StringBuilder builder = new StringBuilder();
            while (data != -1) {
                char aChar = (char) data;
                builder.append(aChar);
                data = inputStream.read();
            }
            inputStream.close();
            scores = builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] splitted = scores.split("#");
        String value = "";
        for (int i = 0; i < splitted.length; i++) {
            value += (i + 1) + ". " + splitted[i] + "\r\n";
        }
        TextView t = findViewById(R.id.textView);
        t.setText(value);
    }
}
