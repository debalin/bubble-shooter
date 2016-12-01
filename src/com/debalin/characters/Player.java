package com.debalin.characters;

import com.debalin.engine.MainEngine;
import com.debalin.engine.events.Event;
import com.debalin.engine.util.EngineConstants;
import com.debalin.util.Constants;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

import java.util.*;

public class Player extends MovingRectangle {

  public float score = 0;
  public int deaths = 0;

  private SpawnPoint spawnPoint;

  private PShape group, base, turret;
  public float orientation;

  public LinkedList<PVector> firingColors;

  public Player(MainEngine engine, SpawnPoint spawnPoint) {
    super(Constants.PLAYER_COLOR, spawnPoint.getPosition(), Constants.PLAYER_SIZE, Constants.PLAYER_INIT_VEL, null, engine);
    setVisible(true);

    this.spawnPoint = spawnPoint;

    group = engine.createShape(PApplet.GROUP);
    base = engine.createShape(PApplet.ELLIPSE, 0, 0, Constants.PLAYER_SIZE.x, Constants.PLAYER_SIZE.y);
    base.setFill(engine.color(color.x, color.y, color.z, 255));
    base.setStroke(engine.color(255, 0));
    turret = engine.createShape(PApplet.RECT, - Constants.PLAYER_TURRET_SIZE.x / 2, -Constants.PLAYER_TURRET_SIZE.y, Constants.PLAYER_TURRET_SIZE.x, Constants.PLAYER_TURRET_SIZE.y);
    turret.setFill(engine.color(Constants.PLAYER_TURRET_COLOR.x, Constants.PLAYER_TURRET_COLOR.y, Constants.PLAYER_TURRET_COLOR.z, 255));
    turret.setStroke(engine.color(255, 0));
    group.addChild(base);
    group.addChild(turret);

    firingColors = new LinkedList<>();
    firingColors.addLast(generateRandomColor());
    firingColors.addLast(generateRandomColor());
    firingColors.addLast(generateRandomColor());
  }

  public synchronized void update(float frameTicSize) {
    moveTurret();
  }

  private void moveTurret() {
    orientation = (float) Math.atan2(engine.mouseY - position.y, engine.mouseX - position.x);
  }

  public void handleKeypress(int key, boolean set) {
    switch (key) {
      case 32:
        if (set)
          fireBubble();
        break;
    }
  }

  @Override
  public void draw() {
    group = engine.createShape(PApplet.GROUP);
    base = engine.createShape(PApplet.ELLIPSE, 0, 0, Constants.PLAYER_SIZE.x, Constants.PLAYER_SIZE.y);
    base.setFill(engine.color(color.x, color.y, color.z, 255));
    base.setStroke(engine.color(255, 0));
    turret = engine.createShape(PApplet.RECT, - Constants.PLAYER_TURRET_SIZE.x / 2, -Constants.PLAYER_TURRET_SIZE.y, Constants.PLAYER_TURRET_SIZE.x, Constants.PLAYER_TURRET_SIZE.y);
    turret.setFill(engine.color(Constants.PLAYER_TURRET_COLOR.x, Constants.PLAYER_TURRET_COLOR.y, Constants.PLAYER_TURRET_COLOR.z, 255));
    turret.setStroke(engine.color(255, 0));
    group.addChild(base);
    group.addChild(turret);

    engine.pushMatrix();

    engine.noStroke();
    group.rotate(orientation + (float)(Math.PI / 2));
    engine.shape(group, position.x, position.y);
    group.resetMatrix();
    engine.popMatrix();

    engine.pushMatrix();
    engine.fill(200, 0, 0);
    engine.rect(Constants.BOUNDARY_POSITION.x, Constants.BOUNDARY_POSITION.y, Constants.BOUNDARY_SIZE.x, Constants.BOUNDARY_SIZE.y);
    engine.popMatrix();

    for (int i = 0; i <= firingColors.size() - 1; i++) {
      engine.pushMatrix();
      engine.fill(firingColors.get(i).x, firingColors.get(i).y, firingColors.get(i).z);
      engine.ellipse(position.x - 20 - (Constants.BUBBLE_DIAMETER.x + 5) * (i + 1), position.y, Constants.BUBBLE_DIAMETER.x, Constants.BUBBLE_DIAMETER.y);
      engine.popMatrix();
    }
  }

  public void fireBubble() {
    String eventType = Constants.EVENT_TYPES.BUBBLE_SPAWN.toString();
    List<Object> eventParameters = new ArrayList<>();

    PVector bubbleInitPosition = new PVector();
    bubbleInitPosition.x = (float) (position.x + Math.cos(orientation) * (Constants.PLAYER_TURRET_SIZE.y + Constants.PLAYER_TURRET_PADDING));
    bubbleInitPosition.y = (float) (position.y + Math.sin(orientation) * (Constants.PLAYER_TURRET_SIZE.y + Constants.PLAYER_TURRET_PADDING));

    eventParameters.add(Constants.BUBBLE_TYPES.FIRE);
    eventParameters.add(-1);
    eventParameters.add(bubbleInitPosition);
    eventParameters.add(new PVector((float) Math.cos(orientation), (float) Math.sin(orientation)).setMag(Constants.BUBBLE_MAX_VEL_FIRE_MAG));
    eventParameters.add(firingColors.pollFirst());
    firingColors.addLast(generateRandomColor());
    Event event = new Event(eventType, eventParameters, EngineConstants.DEFAULT_TIMELINES.GAME_MILLIS.toString(), MainEngine.controller.getClientConnectionID().intValue(), engine.gameTimelineInMillis.getTime(), true);

    engine.getEventManager().raiseEvent(event, true);
  }

  public PVector generateRandomColor() {
    PVector color = null;
    int random = (int) Math.floor(engine.random(3));
    switch (random) {
      case 0:
        color = Constants.BUBBLE_COLOR_1.copy();
        break;
      case 1:
        color = Constants.BUBBLE_COLOR_2.copy();
        break;
      case 2:
        color = Constants.BUBBLE_COLOR_3.copy();
        break;
    }
    return color;
  }

  public void changeColor(float x, float y, float z) {
    this.color.set(x, y, z);
  }

}
