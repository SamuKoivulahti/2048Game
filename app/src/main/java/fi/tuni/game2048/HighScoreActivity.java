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

/**
 * Shows high scores of each game mode
 */
public class HighScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        String filename = "highscoresGame2048.txt";
        File file = new File(getApplicationContext().getFilesDir(), filename);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if (file.length() != 0) {
            value += splitted[0] + "\r\n";
            value += splitted[1] + "\r\n";
            value += splitted[2] + "\r\n";
            value += splitted[3] + "\r\n";
            value += splitted[4] + "\r\n";
        }

        TextView t = findViewById(R.id.textView);
        t.setText(value);
    }
}
