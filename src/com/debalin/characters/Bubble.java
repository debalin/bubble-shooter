package com.debalin.characters;

import com.debalin.BubbleShooterManager;
import com.debalin.engine.MainEngine;
import com.debalin.util.Collision;
import com.debalin.util.Constants;
import processing.core.PVector;

import java.util.Iterator;
import java.util.Map;

public class Bubble extends MovingRectangle {

  private int bubbleID;
  private int rowID, columnID;
  private Map<Integer, Bubble> bubbleMap;

  public int moveInterval;

  public enum BubbleStates {
    IN_GRID, FIRED, CHECK, MATCHED
  }
  public BubbleStates state;
  public boolean matched = false;

  public BubbleShooterManager bubbleShooterManager;

  public Bubble(MainEngine engine, PVector enemyInitPosition, int bubbleID, PVector initVelocity, PVector color, Constants.BUBBLE_TYPES type, Map<Integer, Bubble> bubbleMap, BubbleShooterManager bubbleShooterManager) {
    super(color, enemyInitPosition, Constants.BUBBLE_DIAMETER, initVelocity, Constants.BUBBLE_INIT_ACC, engine);
    setVisible(true);

    this.bubbleID = bubbleID;
    this.columnID = bubbleID / 100;
    this.rowID = bubbleID % 100;
    this.bubbleMap = bubbleMap;
    this.moveInterval = Constants.BUBBLE_MOVE_INTERVAL;
    this.bubbleShooterManager = bubbleShooterManager;

    switch (type) {
      case INIT:
        state = BubbleStates.IN_GRID;
        break;
      case FIRE:
        state = BubbleStates.FIRED;
        break;
    }
  }

  public void update(float frameTicSize) {
    switch (state) {
      case IN_GRID:
        if (engine.gameTimelineInFrames.getTime() % moveInterval == 0) {
          position.y += velocity.y;
          checkWin();
        }
        break;
      case FIRED:
        position.add(velocity);
        if (!checkCollision())
          checkBounds();
        break;
      case CHECK:
        checkNeighbors();
        break;
      case MATCHED:
        setVisible(false);
        bubbleMap.remove(bubbleID);
        break;
    }
  }

  public void checkNeighbors() {
    int neighbor1 = (this.columnID + 1) * 100 + this.rowID;
    int neighbor2 = (this.columnID - 1) * 100 + this.rowID;
    int neighbor3 = (this.columnID) * 100 + this.rowID - 1;
    int neighbor4;
    if (this.rowID % 2 == 0) {
      neighbor4 = (this.columnID + 1) * 100 + this.rowID - 1;
    }
    else {
      neighbor4 = (this.columnID - 1) * 100 + this.rowID - 1;
    }
    if (bubbleMap.get(neighbor1) != null && bubbleMap.get(neighbor1).color.equals(color)) {
      matched = true;
      bubbleMap.get(neighbor1).state = BubbleStates.CHECK;
      bubbleMap.get(neighbor1).matched = true;
      bubbleShooterManager.player.score++;
    }
    if (bubbleMap.get(neighbor2) != null && bubbleMap.get(neighbor2).color.equals(color)) {
      matched = true;
      bubbleMap.get(neighbor2).state = BubbleStates.CHECK;
      bubbleMap.get(neighbor2).matched = true;
      bubbleShooterManager.player.score++;
    }
    if (bubbleMap.get(neighbor3) != null && bubbleMap.get(neighbor3).color.equals(color)) {
      matched = true;
      bubbleMap.get(neighbor3).state = BubbleStates.CHECK;
      bubbleMap.get(neighbor3).matched = true;
      bubbleShooterManager.player.score++;
    }
    if (bubbleMap.get(neighbor4) != null && bubbleMap.get(neighbor4).color.equals(color)) {
      matched = true;
      bubbleMap.get(neighbor4).state = BubbleStates.CHECK;
      bubbleMap.get(neighbor4).matched = true;
      bubbleShooterManager.player.score++;
    }
    if (matched) {
      state = BubbleStates.MATCHED;
      color.set(Constants.BUBBLE_MATCHED_COLOR);
    }
    else {
      state = BubbleStates.IN_GRID;
    }
  }

  public boolean checkCollision() {
    boolean collided = false;

    Iterator it = bubbleMap.entrySet().iterator();
    while(it.hasNext()) {
      Map.Entry<Integer, Bubble> pair = (Map.Entry) it.next();
      int otherBubbleID = pair.getKey();
      Bubble otherBubble = pair.getValue();

      if (Collision.hasCollidedCircles(this, otherBubble)) {
        int otherColumnID = otherBubbleID / 100;
        int otherRowID = otherBubbleID % 100;
        if ((otherRowID + 1) % 2 == 0) {
          position.x = Constants.CLIENT_RESOLUTION.x - Constants.BUBBLE_PADDING - otherColumnID * (Constants.BUBBLE_DIAMETER.x);
        }
        else {
          position.x = Constants.CLIENT_RESOLUTION.x - Constants.BUBBLE_PADDING - otherColumnID * (Constants.BUBBLE_DIAMETER.x) + Constants.BUBBLE_DIAMETER.x / 2;
        }
        position.y = otherBubble.getPosition().y + Constants.BUBBLE_DIAMETER.y;
        this.rowID = otherRowID + 1;
        this.columnID = otherColumnID;
        this.bubbleID = columnID * 100 + rowID;
        this.velocity.set(Constants.BUBBLE_MAX_VEL_INIT);
        bubbleMap.put(bubbleID, this);
        state = BubbleStates.CHECK;
        collided = true;
        break;
      }
    }

    return collided;
  }

  public void checkBounds() {
    if (position.x > Constants.CLIENT_RESOLUTION.x - Constants.BUBBLE_PADDING) {
      setVisible(false);
    }
    else if (position.x < Constants.BUBBLE_PADDING) {
      setVisible(false);
    }
  }

  public void checkWin() {
    if (position.y + Constants.BUBBLE_DIAMETER.y / 2 > Constants.BOUNDARY_POSITION.y) {
      bubbleShooterManager.player.deaths++;
      bubbleShooterManager.reset();
      engine.delay(3000);
      setVisible(false);
    }
  }

  @Override
  public void draw() {
    engine.pushMatrix();

    engine.noStroke();
    engine.fill(color.x, color.y, color.z);
    engine.ellipse(position.x, position.y, size.x, size.y);

    engine.popMatrix();
  }
}
