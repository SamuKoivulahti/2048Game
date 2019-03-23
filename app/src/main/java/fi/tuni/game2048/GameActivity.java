package fi.tuni.game2048;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private final int size = 4;
    private List<TextView> blocks;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        blocks = new ArrayList<>(size*size);
        createGameGrid(size);
        TableLayout layout = findViewById(R.id.tableLayout);
        layout.setOnTouchListener(new OnSwipeTouchListener(GameActivity.this) {
            public void onSwipeTop() {
                //Toast.makeText(GameActivity.this, "top", Toast.LENGTH_SHORT).show();
                checkSum(getColumns(size), SwipeDirections.UP);
            }
            public void onSwipeRight() {
                //Toast.makeText(GameActivity.this, "right", Toast.LENGTH_SHORT).show();
                checkSum(getRows(size), SwipeDirections.RIGHT);
            }
            public void onSwipeLeft() {
                //Toast.makeText(GameActivity.this, "left", Toast.LENGTH_SHORT).show();
                checkSum(getRows(size), SwipeDirections.LEFT);
            }
            public void onSwipeBottom() {
                //Toast.makeText(GameActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                checkSum(getColumns(size), SwipeDirections.DOWN);
            }

        });
    }

    public void createGameGrid(int size) {
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
            for (int j = 0; j < size; j++) {
                GradientDrawable gd = new GradientDrawable();
                // gd.setColor(0xFF00FF00);
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
                    block.setText(probabilityGenerate());
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

    public List<List<Integer>> getRows(int size) {
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

    public List<List<Integer>> getColumns(int size) {
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
                    if (value != 0) {
                        blocks.get(index++).setText(String.valueOf(value));
                    } else {
                        blocks.get(index++).setText("");
                    }
                }
            }
            generateNewRandomBlock();
        }
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
        System.out.printf("array : %s , existing : %s", Arrays.toString(array), Arrays.toString(existing));
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
                    j++;
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
