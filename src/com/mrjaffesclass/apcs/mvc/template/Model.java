package com.mrjaffesclass.apcs.mvc.template;

import com.mrjaffesclass.apcs.messenger.*;
import java.util.Arrays;
import java.util.Random;

/**
 * The model represents the data that the app uses.
 * @author Roger Jaffe
 * @version 1.0
 */
public class Model implements MessageHandler {

  // Messaging system for the MVC
  private final Messenger mvcMessaging;

  // Model's data variables
  private int gridSize;
  private int numMines;
  private boolean[][] mineGrid;
  
  private int score;
  private int lives;
  
  

  /**
   * Model constructor: Create the data representation of the program
   * @param messages Messaging class instantiated by the Controller for 
   *   local messages between Model, View, and controller
   */
  public Model(Messenger messages) {
    mvcMessaging = messages;
  }
  
  /**
   * Initialize the model here and subscribe to any required messages
   */
  public void init() {
    mvcMessaging.subscribe("view:changeButton", this);
    mvcMessaging.subscribe("view:newGameClicked", this);
    mvcMessaging.subscribe("view:gameButtonClick", this);
  
    setGridSize(8);
    setNumMines(10);
    randomizeMines(getNumMines());
    
  }
  
  public void randomizeMines(int number) {
    mineGrid = new boolean[getGridSize()][getGridSize()];
    for (int i=0; i<getGridSize(); i++)
    {
        for (int j=0; j<getGridSize(); j++)
        {
            mineGrid[i][j] = false;
        }
    }
    //Arrays.fill(mineGrid,Boolean.FALSE);
    Random randomGenerator = new Random();
    for (int idx = 1; idx <= number; ++idx){
        int x = randomGenerator.nextInt(getGridSize());
        int y = randomGenerator.nextInt(getGridSize());
        mineGrid[x][y] = Boolean.TRUE;
    }
    score = 0;
    lives = 3;
    
    mvcMessaging.notify("model:StartGame", gridSize, true);
    mvcMessaging.notify("model:ScoreChanged", score, true);
    mvcMessaging.notify("model:LivesChanged", lives, true);
    
    
  }
  
  
  
          
  
  @Override
  public void messageHandler(String messageName, Object messagePayload) {
    if (messagePayload != null) {
      System.out.println("MSG: received by model: "+messageName+" | "+messagePayload.toString());
    } else {
      System.out.println("MSG: received by model: "+messageName+" | No data sent");
    }
    
    if (messageName == "view:gameButtonClick") {
        MessagePayload payload = (MessagePayload)messagePayload;
        int pCol = payload.getField();
        int pRow = payload.getDirection();
        if (mineGrid[pCol][pRow] == true) {
            //hit a bomb
            mvcMessaging.notify("model:hitABomb", new MessagePayload(pCol, pRow), true);
            lives = lives - 1;
            mvcMessaging.notify("model:LivesChanged", lives, true);
        }
        else {
            //hit a safe spot
            mvcMessaging.notify("model:hitASafeSpot", new MessagePayload(pCol, pRow), true);
            score = score + 1;
            mvcMessaging.notify("model:ScoreChanged", score, true);
        }
        if (lives == 0) 
        {
            mvcMessaging.notify("model:GAMEOVER", lives, true);
        }
    }
    else if (messageName == "view:newGameClicked") {
        randomizeMines(getNumMines());
    }
    else {
        MessagePayload payload = (MessagePayload)messagePayload;
        int field = payload.getField();
        int direction = payload.getDirection();
    
        if (direction == Constants.UP) {
          if (field == 1) {
            setGridSize(getGridSize()+Constants.FIELD_1_INCREMENT);
          } else {
            setNumMines(getNumMines()+Constants.FIELD_2_INCREMENT);
          }
        } else {
          if (field == 1) {
            setGridSize(getGridSize()-Constants.FIELD_1_INCREMENT);
          } else {
            setNumMines(getNumMines()-Constants.FIELD_2_INCREMENT);
          }      
        }
     }
  }

  /**
   * Getter function for variable 1
   * @return Value of gridSize
   */
  public int getGridSize() {
    return gridSize;
  }

  /**
   * Setter function for variable 1
   * @param v New value of gridSize
   */
  public void setGridSize(int v) {
    gridSize = v;
    // When we set a new value to variable 1 we need to also send a
    // message to let other modules know that the variable value
    // was changed
    mvcMessaging.notify("model:gridSizeChanged", gridSize, true);
  }
  
  /**
   * Getter function for variable 1
   * @return Value of numMines
   */
  public int getNumMines() {
    return numMines;
  }
  
  /**
   * Setter function for variable 2
   * @param v New value of variable 2
   */
  public void setNumMines(int v) {
    numMines = v;
    // When we set a new value to variable 2 we need to also send a
    // message to let other modules know that the variable value
    // was changed
    mvcMessaging.notify("model:numMinesChanged", numMines, true);
  }

}
