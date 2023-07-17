package com.shpp.p2p.cs.kdyrkach.assignment4;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Class extends WindowProgram and creates Breakout game ???
 */
public class Breakout extends WindowProgram {
    //Width and height of application window in pixels
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;
    // pause time constant ball animation
    public static final double PAUSE_BOLL_TIME = 1000 / 60.0; //60fps
    //Dimensions of the paddle
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;
    //Offset of the paddle up from the bottom
    private static final int PADDLE_Y_OFFSET = 30;
    //Number of bricks per row
    private static final int NBRICKS_PER_ROW = 10;
    //Number of rows of bricks
    private static final int NBRICK_ROWS = 10;
    //Separation between bricks
    private static final int BRICK_SEP = 4;

    // Height of a brick
    private static final int BRICK_HEIGHT = 8;

    //Radius of the ball in pixels
    private static final int BALL_RADIUS = 10;

    //Offset of the top brick row from the top
    private static final int BRICK_Y_OFFSET = 70;
    //Number of turns
    private static final int NTURNS = 3;

    private double vx, vy;//a "global variable" responsible for the speed of the ball along the x and y axes
    GRect paddle = null; //the "global variable" responsible for referring to the racket is used in many methods


    public void run() {
        createGame();
    }

    private void createGame() {
        int brickCounter = NBRICK_ROWS * NBRICKS_PER_ROW; //count of bricks
        createBrick();
        /*cycle controls the number of player attempts*/
        for (int i = 0; i < NTURNS; i++) {
            paddle = createPaddle(); //variable that stores the reference to the racket
            GOval ball = createBall(); //mine that keeps links to the ball
            /*variable that stores a reference to the inscription at the beginning of the game*/
            GLabel start = createLabelForStartGame();
            waitForClick();
            remove(start);
            getSpeedX();
            addMouseListeners();
            brickCounter = ballMove(ball, brickCounter);
            remove(ball);
            remove(paddle);
            if (brickCounter == 0) {
                break;
            }
        }
        endGame(brickCounter);
    }

    /**
     * method creates a racket and sets its parameters
     *
     * @return racket link
     */
    private GRect createPaddle() {
        GRect paddle = new GRect(
                getWidth() / 2.0 - PADDLE_WIDTH / 2.0,
                getHeight() - PADDLE_Y_OFFSET,
                PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setFilled(true);
        paddle.setColor(Color.BLACK);
        add(paddle);
        return paddle;
    }

    /**
     * method creates a ball and sets its parameters
     *
     * @return link to the ball
     */
    public GOval createBall() {
        GOval ball = new GOval(
                getWidth() / 2.0 - BALL_RADIUS,
                getHeight() / 2.0 - BALL_RADIUS,
                BALL_RADIUS * 2, BALL_RADIUS * 2);
        ball.setFilled(true);
        ball.setColor(Color.BLACK);
        add(ball);
        return ball;
    }

    /**
     * method creates a bruise from bricks and sets their parameters
     */
    private void createBrick() {
        //array of colors
        Color[] color = new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN};
        double x;
        double y = BRICK_Y_OFFSET;
        //calculating brick height
        int brickWidth = (getWidth() - (BRICK_SEP * (NBRICKS_PER_ROW - 1))) / NBRICKS_PER_ROW;
        /*building columns*/
        for (int i = 1, k = 0; i <= NBRICK_ROWS; i++) {
            x = getWidth() / 2.0 -
                    ((NBRICKS_PER_ROW / 2.0 * brickWidth) +
                            (NBRICKS_PER_ROW / 2.0 * BRICK_SEP)) + BRICK_SEP / 2.0;

            /*building rows*/
            for (int j = 0; j < NBRICKS_PER_ROW; j++) {
                GRect brick = new GRect(x, y, brickWidth, BRICK_HEIGHT);
                brick.setFilled(true);
                brick.setColor(color[k]);
                add(brick, x, y);
                x += brickWidth + BRICK_SEP;//x offset
            }


            if (i % 2 == 0) {
                k++;
                if (k == 5) {
                    k = 0;
                }

            }

            y += BRICK_HEIGHT + BRICK_SEP;//y offset
        }
    }

    /**
     * method controls the movement of the ball and processes its collision with objects
     *
     * @param ball         link to the ball
     * @param brickCounter variable responsible for counting the number of bricks on the screen
     * @return brick counter
     */
    private int ballMove(GOval ball, int brickCounter) {
        vy = 3.0; //ball speed along y axis
        GObject collider = null;
        while (!(ball.getY() + ball.getHeight() >= getHeight())) {


            if (ballHitRightOrLeftWall(ball)) {
                vx = -vx;
            }

            if (ballHitTopWall(ball)) {
                vy = -vy;
            }
            if (collider != getCollidingObject(ball)) {
                collider = getCollidingObject(ball);
                if (collider == paddle) {
                    vy = -vy;

                }
            }
            if (collider != paddle && collider != null) {
                vy = -vy;
                remove(collider);
                brickCounter--;
                if (brickCounter == 0) {
                    return brickCounter;
                }
            }
            ball.move(vx, vy);

            pause(PAUSE_BOLL_TIME);
        }
        return brickCounter;
    }

    //check for collision with the upper wall
    private boolean ballHitTopWall(GOval ball) {
        return ball.getY() <= 0;
    }

    //check for collision with the right or left wall
    private boolean ballHitRightOrLeftWall(GOval ball) {
        return ball.getX() + ball.getWidth() >= getWidth() || ball.getX() <= 0;
    }

    /**
     * method creates points around a ball that return objects
     *
     * @return object that was found by a point
     */
    private GObject getCollidingObject(GOval ball) {
        GObject[] point = new GObject[]{
                getElementAt(ball.getX() + BALL_RADIUS, ball.getY() - 1),
                getElementAt(ball.getX() - 1, ball.getY() + BALL_RADIUS),
                getElementAt(ball.getX() + 1 + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS),
                getElementAt(ball.getX() + BALL_RADIUS, ball.getY() + BALL_RADIUS * 2 + 1),
                getElementAt(ball.getX() + 2, ball.getY() + 2),
                getElementAt(ball.getX() - 2 + BALL_RADIUS * 2, ball.getY() + 2),
                getElementAt(ball.getX() + 2, ball.getY() + BALL_RADIUS * 2 - 2),
                getElementAt(ball.getX() + BALL_RADIUS * 2 - 2, ball.getY() + BALL_RADIUS * 2 - 2)

        };


        for (int i = 0; i < point.length; i++) {
            if (point[i] != null) {
                if (point[0] != null || point[3] != null) {
                } else vx = -vx;

                return point[i];
            }
        }
        return null;
    }


    /**
     * method creates an inscription about victory or defeat and sets them parameters
     */
    private void endGame(int brickCounter) {
        GLabel result;
        if (brickCounter == 0) {
            result = new GLabel("You are winner!");
            result.setFont("Verdana-30");
            result.setLocation(getWidth() / 2.0 - result.getWidth() / 2, getHeight() / 2.0);
            add(result);
        } else {
            result = new GLabel("You lose!");
            result.setFont("Verdana-30");
            result.setLocation(getWidth() / 2.0 - result.getWidth() / 2, getHeight() / 2.0);
            add(result);
        }
    }

    /**
     * method creates an inscription "Click to start the game" and sets its parameters
     *
     * @return reference to this label
     */
    private GLabel createLabelForStartGame() {
        GLabel startGame = new GLabel("Click to start the game");
        startGame.setFont("Verdana-30");
        startGame.setLocation(getWidth() / 2.0 - startGame.getWidth() / 2, BRICK_Y_OFFSET / 2.0);
        add(startGame);

        return startGame;
    }

    //method sets the x-acceleration value using a randomizer
    private RandomGenerator getSpeedX() {
        RandomGenerator rgen = RandomGenerator.getInstance();
        vx = rgen.nextDouble(1.0, 3.0);
        if (rgen.nextBoolean(0.5))
            vx = -vx;
        return rgen;
    }

    //method is responsible for controlling the racket
    public void mouseMoved(MouseEvent mouse) {
        int paddleSpeed = 1;

        if (paddleHitTheRightWall(mouse)) {
            while (mouseGoRight(mouse)) {
                paddle.move(paddleSpeed, 0);
            }
        }
        if (paddleHitTheLeftWall(mouse)) {
            while (mouseGOLeft(mouse)) {
                paddle.move(-paddleSpeed, 0);
            }
        }
    }

    //check for left mouse movement
    private boolean mouseGOLeft(MouseEvent mouse) {
        return mouse.getX() <= (paddle.getX() + (paddle.getWidth() / 2));
    }

    //checking for right mouse movement
    private boolean mouseGoRight(MouseEvent mouse) {
        return mouse.getX() >= (paddle.getX() + (paddle.getWidth() / 2));
    }

    //check for the racket to rest against the right wall
    private boolean paddleHitTheRightWall(MouseEvent mouse) {
        return mouse.getX() + paddle.getWidth() / 2 <= getWidth() - 1;
    }

    //check for the racquet to rest against the left wall
    private boolean paddleHitTheLeftWall(MouseEvent mouse) {
        return mouse.getX() - paddle.getWidth() / 2 >= 1;
    }
}