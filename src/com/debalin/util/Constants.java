package com.debalin.util;

import processing.core.PVector;

public class Constants {

  public static final PVector CLIENT_RESOLUTION = new PVector(600, 700);
  public static final PVector SERVER_RESOLUTION = new PVector(1, 1);
  public static final int SMOOTH_FACTOR = 4;
  public static final PVector BACKGROUND_RGB = new PVector(20, 20, 20);

  public static final PVector PLAYER_COLOR = new PVector(240, 240, 240);
  public static final PVector PLAYER_TURRET_COLOR = new PVector(140, 140, 140);
  public static final float PLAYER_TURRET_PADDING = 20;
  public static final PVector PLAYER_SIZE = new PVector(40, 40);
  public static final PVector PLAYER_TURRET_SIZE = new PVector(5, 40);
  public static final float PLAYER_PADDING_Y = 80;
  public static final PVector PLAYER_SPAWN = new PVector(Constants.CLIENT_RESOLUTION.x / 2, Constants.CLIENT_RESOLUTION.y - Constants.PLAYER_PADDING_Y);
  public static final PVector PLAYER_INIT_VEL = new PVector(0, 0);
  
  public static final int BUBBLE_ROWS = 9;
  public static final PVector BUBBLE_MAX_VEL_INIT = new PVector(0, 24);
  public static final float BUBBLE_MAX_VEL_FIRE_MAG = 4;
  public static final PVector BUBBLE_INIT_ACC = new PVector(0, 0);
  public static final PVector BUBBLE_COLOR_1 = new PVector(66, 164, 244);
  public static final PVector BUBBLE_COLOR_2 = new PVector(244, 66, 80);
  public static final PVector BUBBLE_COLOR_3 = new PVector(66, 244, 122);
  public static final PVector BUBBLE_MATCHED_COLOR = new PVector(244, 232, 66);
  public static final PVector BUBBLE_DIAMETER = new PVector(25, 25);
  public static final float BUBBLE_PADDING = 25;
  public static int BUBBLE_MOVE_INTERVAL = 360;

  public enum BUBBLE_TYPES {
    INIT, FIRE
  }

  public static final PVector BOUNDARY_POSITION = new PVector(BUBBLE_PADDING, PLAYER_SPAWN.y - 100);
  public static final PVector BOUNDARY_SIZE = new PVector(CLIENT_RESOLUTION.x - 2 * BUBBLE_PADDING, 3);

  public static final int SERVER_PORT = 5678;
  public static final String SERVER_ADDRESS = "localhost";

  public static final PVector SCORE_POSITION = new PVector(40, Constants.CLIENT_RESOLUTION.y - 90);

  public enum EVENT_TYPES {
    PLAYER_DEATH, BUBBLE_SPAWN, PLAYER_SPAWN, PLAYER_FIRE, NULL, SCRIPT
  }

}
