package fi.tuni.game2048;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Shows menu for player
 */
public class MainActivity extends AppCompatActivity {
    int mode = 1;
    int colorMode = 0;
    ImageView pic;
    TextView title;
    public MainActivity() {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pic = findViewById(R.id.imageView);
        pic.setImageResource(R.drawable.four_four);
        title = findViewById(R.id.mode);
        title.setText("4x4");

    }

    /**
     * starts game
     * @param v view
     */
    public void playGame(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        Bundle b = new Bundle();
        b.putInt("mode", mode);
        b.putInt("color", colorMode);
        intent.putExtras(b);
        startActivity(intent);
    }

    /**
     * Shows high score screen
     * @param v view
     */
    public void highScores(View v) {
        Intent intent = new Intent(this, HighScoreActivity.class);
        startActivity(intent);
    }

    /**
     * changes the image of mode
     * @param v
     */
    public void left(View v) {
        if (mode > 0) {
            mode--;
        }
        pic.setImageResource(setImage());
    }

    /**
     * changes the image of mode
     * @param v
     */
    public void right(View v) {
        if (mode < 4) {
            mode++;
        }
        pic.setImageResource(setImage());
    }

    /**
     * sets Image according to mode
     * @return mode id
     */
    private int setImage() {
        switch (mode) {
            case 0: {
                colorMode = 0;
                title.setText("3x3");
                return R.drawable.three_three;
            }
            case 1: {
                colorMode = 0;
                title.setText("4x4");
                return R.drawable.four_four;
            }
            case 2: {
                colorMode = 0;
                title.setText("5x5");
                return R.drawable.five_five;
            }
            case 3: {
                colorMode = 1;
                title.setText("Color");
                return R.drawable.color_mode;
            }
            case 4: {
                colorMode = 2;
                title.setText("Black");
                return R.drawable.color_ff;
            }
        }
        return 0;
    }
}
