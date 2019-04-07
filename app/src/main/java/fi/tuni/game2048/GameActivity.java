package fi.tuni.game2048;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private final int size = 4;
    private List<TextView> blocks;
    public int score;
    private TextView scoreBoard;
    private Animation animation;
    private String[] animatedBlocks = new String[size*size];
    private boolean end = false;
    TableLayout layout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        score = 0;
        blocks = new ArrayList<>(size*size);
        createGameGrid();
        scoreBoard = findViewById(R.id.scoreBoard);
        scoreBoard.setText("Score: " + score);
        String fileContents = scoreCounter();
        FileOutputStream outputStream;

            try {
                File file = new File("highscores.txt");
                outputStream = openFileOutput(file.getName(), Context.MODE_PRIVATE);
                outputStream.write(fileContents.getBytes());
                outputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        layout = findViewById(R.id.tableLayout);
        layout.setOnTouchListener(new OnSwipeTouchListener(GameActivity.this) {
            public void onSwipeTop() {
                //Toast.makeText(GameActivity.this, "top", Toast.LENGTH_SHORT).show();
                if (!end) {
                    animation = AnimationUtils.loadAnimation(GameActivity.this, R.anim.t_to_b);
                    checkSum(getColumns(), SwipeDirections.UP);
                }
            }

            public void onSwipeRight() {
                //Toast.makeText(GameActivity.this, "right", Toast.LENGTH_SHORT).show();
                if (!end) {
                    animation = AnimationUtils.loadAnimation(GameActivity.this, R.anim.r_to_l);
                    checkSum(getRows(), SwipeDirections.RIGHT);
                }
            }

            public void onSwipeLeft() {
                //Toast.makeText(GameActivity.this, "left", Toast.LENGTH_SHORT).show();
                if (!end) {
                    animation = AnimationUtils.loadAnimation(GameActivity.this, R.anim.l_to_r);
                    checkSum(getRows(), SwipeDirections.LEFT);
                }
            }

            public void onSwipeBottom() {
                //Toast.makeText(GameActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                if (!end) {
                    animation = AnimationUtils.loadAnimation(GameActivity.this, R.anim.b_to_t);
                    checkSum(getColumns(), SwipeDirections.DOWN);
                }
            }

        });
    }

    public void createGameGrid() {
        TableLayout layout = findViewById(R.id.tableLayout);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT,
                1f/size);
        TableRow.LayoutParams blockParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT,
                1f/size);
        int width = layout.getWidth()/size;
        for (int i = 0; i < size; i++) {
            TableRow layoutV = new TableRow(this);
            GradientDrawable layoutGD = new GradientDrawable();
            layoutGD.setColor(0xFFE2E2E2);
            layoutV.setBackground(layoutGD);
            for (int j = 0; j < size; j++) {
                GradientDrawable gd = new GradientDrawable();
                gd.setColor(0xFFE2E2E2);
                gd.setCornerRadius(10);
                gd.setStroke(2, 0xFF000000);
                TextView block = new TextView(this);
                blocks.add(block);
                block.setText("");
                block.setBackground(gd);
                block.setTextSize(32f);
                block.setGravity(Gravity.CENTER);
                block.setPadding(0,0,0,0);
                block.setMaxWidth(width);
                block.setMaxHeight(width);
                block.setHeight(width);
                layoutV.addView(block, blockParams);
            }
            layout.addView(layoutV, layoutParams);
        }
        generateNewRandomBlockBeginning();

    }

    public void generateNewRandomBlockBeginning() {
        int cellCount = 2;
        while (cellCount > 0) {
            TextView block = generateRandomPosition();
            if (block.getText().equals("")) {
                block.setText(probabilityGenerate());
                GradientDrawable gd = new GradientDrawable();
                gd.setColor(generateColor(block.getText().toString()));
                gd.setStroke(2, 0xFF000000);
                gd.setCornerRadius(10);
                block.setBackground(gd);
                cellCount--;
            }
        }
    }

    public void generateNewRandomBlock() {
        int cellCount = 0;
        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).getText().toString().equals("")) {
                cellCount++;
            }
        }
        if (cellCount > 0) {
            while (true) {
                TextView block = generateRandomPosition();
                if (block.getText().equals("")) {
                    String probability = probabilityGenerate();
                    block.setText(probability);
                    GradientDrawable gd = new GradientDrawable();
                    gd.setColor(generateColor(block.getText().toString()));
                    gd.setStroke(2, 0xFF000000);
                    gd.setCornerRadius(10);
                    block.setBackground(gd);
                    block.startAnimation(AnimationUtils.loadAnimation(GameActivity.this, R.anim.appear));
                    break;
                }
            }

        }
    }

    public int generateRandomValue() {
        return (int)(Math.random()*blocks.size());
    }

    public TextView generateRandomPosition() {
        return blocks.get(generateRandomValue());
    }

    public String probabilityGenerate() {
        Random rand = new Random();
        boolean val = rand.nextInt(10)==0;
        if (val) {
            return "4";
        } else {
            return "2";
        }
    }

    public List<List<Integer>> getRows() {
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                TextView block = blocks.get(i * size + j);
                if (!(block.getText().toString()).equals("")) {
                    row.add(Integer.parseInt(block.getText().toString()));
                }
            }
            //System.out.println(row);
            result.add(row);

        }
        return result;
    }

    public List<List<Integer>> getColumns() {
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            List<Integer> columns = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                TextView block = blocks.get(i + size * j);
                if (!(block.getText().toString()).equals("")) {
                    columns.add(Integer.parseInt(block.getText().toString()));
                }
            }
            result.add(columns);

        }
        return result;
    }

    public void setNewValues(List<List<Integer>> result, boolean horizontal) {
        int index = 0;
        if (!checkIfChanged(result, horizontal)) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int value;
                    if (horizontal) {
                        value = result.get(i).get(j);
                    } else {
                        value = result.get(j).get(i);
                    }
                    GradientDrawable gd = new GradientDrawable();
                    gd.setStroke(2, 0xFF000000);
                    gd.setCornerRadius(10);
                    if (value != 0) {
                        blocks.get(index).setText(String.valueOf(value));
                        if (animatedBlocks[index] != null) {
                            blocks.get(index).startAnimation(animation);
                        }
                        gd.setColor(generateColor(blocks.get(index).getText().toString()));
                        blocks.get(index++).setBackground(gd);
                    } else {
                        blocks.get(index).setText("");
                        gd.setColor(generateColor(blocks.get(index).getText().toString()));
                        blocks.get(index++).setBackground(gd);
                    }
                }
            }
            generateNewRandomBlock();
            if (!checkEmptyBlocks()) {
                gameOver();
                if (end) {
                    gameOverPopup();
                }
            }
        }
    }

    private void gameOverPopup() {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup, null);
        // create the popup window
        final int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = false;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                String filename = "highscores.txt";
                File file = new File(getApplicationContext().getFilesDir(), filename);
                String fileContents = scoreCounter();
                FileOutputStream outputStream;

                try {
                    outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(fileContents.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setNewGameGrid();
                return true;
            }
        });
    }

    private String scoreCounter() {
        String filename = "highscores.txt";
        File file = new File(getApplicationContext().getFilesDir(), filename);
        FileInputStream inputStream;
        String value = "";
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
            value = builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] scores = value.split("#");
        String placeScoreA = "";
        boolean added = false;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i].equals("")) {
                scores[i] = "0";
            }
            if ((score > Integer.parseInt(scores[i])) && (!added)) {
                added = true;
                placeScoreA = scores[i];
                scores[i] = String.valueOf(score);
            } else if ((score > Integer.parseInt(scores[i])) && added) {
                String placeScoreB = placeScoreA;
                placeScoreA = scores[i];
                scores[i] = placeScoreB;
            }
        }

        String s = "";
        for (int i = 0; i < scores.length; i++) {
            if (i == scores.length -1) {
                s += scores[i];
            } else {
                s += scores[i] + "#";
            }
        }
        if (scores.length < 5 && !added) {
            s += "#" + score;
        }
        System.out.println(s);
        return s;
    }

    private void setNewGameGrid() {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xFFE2E2E2);
        gd.setCornerRadius(10);
        gd.setStroke(2, 0xFF000000);
        for (int i = 0; i < blocks.size(); i++) {
            blocks.get(i).setText("");
            blocks.get(i).setBackground(gd);
        }
        score = 0;
        scoreBoard.setText("Score: " + score);
        generateNewRandomBlockBeginning();
        end = false;
    }

    private void gameOver() {
        List<List<Integer>> cols = getColumns();
        List<List<Integer>> rows = getRows();
        end = true;
        List<Integer> valuesRow;
        List<Integer> valuesColumn;
        for (int i = 0; i < size; i++) {
            valuesRow = rows.get(i);
            valuesColumn = cols.get(i);
            for (int j = 0; j < size; j++) {
                if (j + 1 < valuesColumn.size() && valuesColumn.get(j).equals(valuesColumn.get(j + 1))) {
                    end = false;
                }
                if (j + 1 < valuesRow.size() && valuesRow.get(j).equals(valuesRow.get(j + 1))) {
                    end = false;
                }
            }
        }
    }

    private boolean checkEmptyBlocks() {
        boolean hasEmptyBlocks = false;
        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).getText().toString().equals("")) {
                hasEmptyBlocks = true;
            }
        }
        return hasEmptyBlocks;
    }

    private int generateColor(String value) {
        int color;
        switch (value) {
            case "": {
                color = 0xFFE2E2E2;
                break;
            }
            case "2": {
                color = 0xFFFFED8A;
                break;
            }
            case "4": {
                color = 0xFFFEC373;
                break;
            }
            case "8": {
                color = 0xFFFE9A5D;
                break;
            }
            case "16": {
                color = 0xFFFF7257;
                break;
            }
            case "32": {
                color = 0xFFFF5994;
                break;
            }
            case "64": {
                color = 0xFFFF5CEB;
                break;
            }
            case "128": {
                color = 0xFFA357FF;
                break;
            }
            case "256": {
                color = 0xFF554CFF;
                break;
            }
            case "512": {
                color = 0xFF4184FF;
                break;
            }
            case "1024": {
                color = 0xFF00ADEB;
                break;
            }
            case "2048": {
                color = 0xFF00D621;
                break;
            }
            default: {
                color = 0xFFE00041;
                break;
            }
        }
        return color;
    }

    public boolean checkIfChanged(List<List<Integer>> newList, boolean horizontal) {
        String[] array = new String[size*size];
        String[] existing = new String[size*size];
        int index = 0;
        for (int i = 0; i < newList.size(); i++) {
            List<Integer> comparable = newList.get(i);
            for (int j = 0; j < comparable.size(); j++) {
                if (horizontal) {
                    if (newList.get(i).get(j) == 0) {
                        array[index++] = "";
                    } else {
                        array[index++] = String.valueOf(newList.get(i).get(j));
                    }
                } else {
                    if (newList.get(j).get(i) == 0) {
                        array[index++] = "";
                    } else {
                        array[index++] = String.valueOf(newList.get(j).get(i));
                    }
                }
            }
        }
        for (int i = 0; i < blocks.size(); i++) {
            existing[i] = blocks.get(i).getText().toString();
        }
        for (int i = 0; i < blocks.size(); i++) {
            if (array[i].equals(existing[i])) {
                animatedBlocks[i] = null;
            } else {
                animatedBlocks[i] = array[i];
            }
        }
        System.out.println(Arrays.toString(array));
        System.out.println(Arrays.toString(existing));
        System.out.println(Arrays.toString(animatedBlocks));
        return Arrays.equals(array, existing);
    }

    public List<List<Integer>> checkDirections(List<List<Integer>> lines, boolean reverse) {
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            List<Integer> newValues = new ArrayList<>();
            List<Integer> values = lines.get(i);
            if (reverse) {
                Collections.reverse(values);
            }
            for (int j = 0; j < values.size(); j++) {
                if (j + 1 < values.size() && values.get(j).equals(values.get(j + 1))) {
                    newValues.add(values.get(j)*2);
                    score += values.get(j)*2;
                    j++;
                    scoreBoard.setText(String.valueOf("Score: " + score));
                } else {
                    newValues.add(values.get(j));
                }
            }
            for (int j = newValues.size(); j < size; j++) {
                newValues.add(0);
            }
            if (reverse) {
                Collections.reverse(newValues);
            }
            System.out.println(newValues);
            result.add(newValues);
        }
        return result;
    }

    public void checkSum(List<List<Integer>> lines, SwipeDirections direction) {
        switch (direction) {
            case LEFT: {
                setNewValues(checkDirections(lines, false), true);
                break;
            }
            case RIGHT: {
                setNewValues(checkDirections(lines, true), true);
                break;
            }
            case UP: {
                setNewValues(checkDirections(lines, false), false);
                break;
            }
            case DOWN: {
                setNewValues(checkDirections(lines, true), false);
                break;
            }
        }

    }

    private enum SwipeDirections {
        LEFT, RIGHT, UP, DOWN
    }
}
