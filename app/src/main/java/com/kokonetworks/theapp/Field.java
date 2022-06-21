package com.kokonetworks.theapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

class Field extends LinearLayout {
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Handler timeHandler = new Handler(Looper.getMainLooper());
    private final SquareButton[] circles = new SquareButton[9];
    private TextView tvTimer;
    private Button btPause;
    private int currentCircle;
    private Listener listener;

    private int score;
    private Mole mole;

    private boolean isRunning;
    public boolean isClicked = true;

    private final int ACTIVE_TAG_KEY = 873374234;
    private final int FALSE_TAG_KEY = 873374235;

    public Field(Context context) {
        super(context);
        initializeViews(context);
    }

    public Field(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public Field(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    public Field(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializeViews(context);
    }

    public int totalCircles() {
        return circles.length;
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_field, this, true);

        circles[0] = (SquareButton) findViewById(R.id.hole1);
        circles[1] = (SquareButton) findViewById(R.id.hole2);
        circles[2] = (SquareButton) findViewById(R.id.hole3);
        circles[3] = (SquareButton) findViewById(R.id.hole4);
        circles[4] = (SquareButton) findViewById(R.id.hole5);
        circles[5] = (SquareButton) findViewById(R.id.hole6);
        circles[6] = (SquareButton) findViewById(R.id.hole7);
        circles[7] = (SquareButton) findViewById(R.id.hole8);
        circles[8] = (SquareButton) findViewById(R.id.hole9);
        tvTimer = (TextView) findViewById(R.id.tvTimer);
        btPause = findViewById(R.id.btPause);

        btPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseGame();
            }
        });
    }


    private void resetScore() {
        score = 0;
        isRunning = true;
    }

    public void startGame() {
        resetScore();
        resetCircles();
        for (SquareButton squareButton : circles) {
            squareButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean active = (boolean) view.getTag(ACTIVE_TAG_KEY);
                    if (active && isRunning) {
                        score += mole.getCurrentLevel() * 2;
                        listener.onScoreChanged(score);
                    } else {
                        view.setEnabled(false);
                        mole.stopHopping();
                        listener.onGameEnded(score);
                        isRunning = false;
                    }
                    isClicked = true;
                }
            });
        }

        mole = new Mole(this);
        mole.startHopping();
    }

    public int getCurrentCircle() {
        return currentCircle;
    }

    private void resetCircles() {
        for (SquareButton squareButton : circles) {
            squareButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hole_inactive));
            squareButton.setTag(ACTIVE_TAG_KEY, false);
            squareButton.setEnabled(true);
        }
    }

    public void setActive(int index) {
        if (isClicked) {
            mainHandler.post(() -> {
                resetCircles();
                setFalseCircle(index);
                circles[index].setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hole_active));
                circles[index].setTag(ACTIVE_TAG_KEY, true);
                currentCircle = index;
            });
            isClicked = false;
        } else
            stopGame();
    }

    public void setFalseCircle(int index) {
        circles[circles.length - index].setBackground(ContextCompat.getDrawable(getContext(), R.drawable.false_circle_drawable));
        circles[circles.length - index].setTag(FALSE_TAG_KEY, true);
    }

    public void stopGame() {
        mole.stopHopping();
        listener.onGameEnded(score);
        isRunning = false;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void updateTimer(long l) {
        timeHandler.post(() -> {
            tvTimer.setText(String.valueOf(l));
        });
    }

    public interface Listener {
        void onGameEnded(int score);

        void onScoreChanged(int score);

        void onLevelChange(int level);
    }

    private void pauseGame() {

    }
}