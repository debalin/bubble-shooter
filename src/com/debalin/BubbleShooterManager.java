package com.debalin;

import com.debalin.characters.Bubble;
import com.debalin.characters.Player;
import com.debalin.characters.SpawnPoint;
import com.debalin.engine.*;
import com.debalin.engine.events.Event;
import com.debalin.engine.events.EventHandler;
import com.debalin.engine.events.EventManager;
import com.debalin.engine.game_objects.GameObject;
import com.debalin.engine.network.GameClient;
import com.debalin.engine.network.GameServer;
import com.debalin.engine.util.EngineConstants;
import com.debalin.engine.util.TextRenderer;
import com.debalin.util.Constants;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import processing.core.PVector;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class BubbleShooterManager extends Controller implements TextRenderer {

  public Player player;
  public SpawnPoint playerSpawnPoint;
  public Queue<GameObject> bubbles;
  public Map<Integer, Bubble> bubbleMap;
  public boolean serverMode;
  public GameServer gameServer;
  public GameClient gameClient;

  public int bubblesObjectID;
  public int playerObjectID;

  Map<Integer, Queue<Event>> fromServerWriteQueues;

  DecimalFormat decimalFormatter;

  private EventHandler eventHandler;

  public BubbleShooterManager(boolean serverMode) {
    this.serverMode = serverMode;
    bubbles = new ConcurrentLinkedQueue<>();
    bubbleMap = new HashMap<>();
    bubblesObjectID = playerObjectID = -1;
    fromServerWriteQueues = new HashMap<>();

    decimalFormatter = new DecimalFormat();
    decimalFormatter.setMaximumFractionDigits(2);

    eventHandler = new GameEventHandler(this);
  }

  public static void main(String args[]) {
    BubbleShooterManager bubbleShooterManager;

    if (args[0].toLowerCase().equals("s")) {
      System.out.println("Starting as server.");
      bubbleShooterManager = new BubbleShooterManager(true);
    } else {
      System.out.println("Starting as client.");
      bubbleShooterManager = new BubbleShooterManager(false);
    }

    bubbleShooterManager.startEngine();
  }

  @Override
  public Map<String, GameObject> bindObjects() {
    Map<String, GameObject> gameObjects = new HashMap<>();

    gameObjects.put("player", player);

    return gameObjects;
  }

  @Override
  public String getScriptPath() {
    String scriptPath = System.getProperty("user.dir") + "/scripts/script.js";
    return scriptPath;
  }

  @Override
  public String getScriptFunctionName() {
    return "playerColorChanger";
  }

  @Override
  public void mirrorGameObjects(List<Queue<GameObject>> gameObjectsCluster) {

  }

  private void startEngine() {
    registerConstants();

    System.out.println("Starting engine.");
    MainEngine.startEngine(this);
  }

  public String getTextContent() {
    if (player == null)
      return "";

    String content = "Score: " + decimalFormatter.format(player.score);
    content += "\nDeaths: " + player.deaths;
    return content;
  }

  public PVector getTextPosition() {
    return Constants.SCORE_POSITION;
  }

  private void registerConstants() {
    System.out.println("Registering constants.");
    MainEngine.registerConstants(Constants.CLIENT_RESOLUTION, Constants.SERVER_RESOLUTION, Constants.SMOOTH_FACTOR, Constants.BACKGROUND_RGB, serverMode);
  }

  @Override
  public void setup() {
    registerEventTypes();

    if (!serverMode) {
      AtomicInteger clientConnectionID = getClientConnectionID();
      synchronized (clientConnectionID) {
        try {
          while (clientConnectionID.intValue() == -1)
            clientConnectionID.wait();
        } catch (InterruptedException ex) {
        }
      }
      System.out.println("Connection ID is " + getClientConnectionID() + ".");
      initializePlayer();
      registerTextRenderers();
    } else {
      spawnBubbles();
    }
  }

  public void registerServerOrClient() {
    if (serverMode) {
      System.out.println("Registering Server.");
      gameServer = engine.registerServer(Constants.SERVER_PORT, this);
    } else {
      System.out.println("Registering Client.");
      gameClient = engine.registerClient(Constants.SERVER_ADDRESS, Constants.SERVER_PORT, this);
    }
  }

  private void registerEventTypes() {
    engine.getEventManager().registerEventType(Constants.EVENT_TYPES.PLAYER_DEATH.toString(), EventManager.EventPriorities.HIGH);
    engine.getEventManager().registerEventType(Constants.EVENT_TYPES.BUBBLE_SPAWN.toString(), EventManager.EventPriorities.MED);
    engine.getEventManager().registerEventType(Constants.EVENT_TYPES.PLAYER_FIRE.toString(), EventManager.EventPriorities.HIGH);
    engine.getEventManager().registerEventType(Constants.EVENT_TYPES.PLAYER_SPAWN.toString(), EventManager.EventPriorities.HIGH);
  }

  private void registerTextRenderers() {
    System.out.println("Registering text renderers.");
    engine.registerTextRenderer(this);
  }

  private void spawnBubbles() {
    int bubbleColumns = (int) ((Constants.CLIENT_RESOLUTION.x - 2 * Constants.BUBBLE_PADDING) / Constants.BUBBLE_DIAMETER.x);
    for (int i = 1; i <= Constants.BUBBLE_ROWS; i++) {
      for (int j = 1; j <= bubbleColumns; j++) {
        float x;
        if (i % 2 == 0)
          x = Constants.CLIENT_RESOLUTION.x - Constants.BUBBLE_PADDING - j * (Constants.BUBBLE_DIAMETER.x);
        else
          x = Constants.CLIENT_RESOLUTION.x - Constants.BUBBLE_PADDING - j * (Constants.BUBBLE_DIAMETER.x) + Constants.BUBBLE_DIAMETER.x / 2;
        float y = i * (Constants.BUBBLE_DIAMETER.y) + Constants.BUBBLE_DIAMETER.y / 2;
        PVector enemyInitPosition = new PVector(x, y);
        int enemyID = j * 100 + i;
        String eventType = Constants.EVENT_TYPES.BUBBLE_SPAWN.toString();
        List<Object> eventParameters = new ArrayList<>();
        eventParameters.add(Constants.BUBBLE_TYPES.INIT);
        eventParameters.add(enemyID);
        eventParameters.add(enemyInitPosition);
        eventParameters.add(Constants.BUBBLE_MAX_VEL_INIT.copy());
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
        eventParameters.add(color);
        Event event = new Event(eventType, eventParameters, EngineConstants.DEFAULT_TIMELINES.GAME_MILLIS.toString(), getClientConnectionID().intValue(), engine.gameTimelineInMillis.getTime(), true);
        engine.getEventManager().raiseEvent(event, true);
      }
    }
  }

  public void manage() {
    removeGameObjects();
    if (checkAllEnemiesDeath() && bubblesObjectID != -1) {
      reset();
    }
  }

  public boolean checkAllEnemiesDeath() {
    if (bubbles.size() == 0)
      return true;
    else
      return false;
  }

  private void removeGameObjects() {
    synchronized (bubbles) {
      Iterator<GameObject> i = bubbles.iterator();
      while (i.hasNext()) {
        Bubble bubble = (Bubble) i.next();
        if (!bubble.isVisible())
          i.remove();
      }
    }
  }

  private void initializePlayer() {
    System.out.println("Initializing player.");
    playerSpawnPoint = new SpawnPoint(Constants.PLAYER_SPAWN.copy());

    String eventType = Constants.EVENT_TYPES.PLAYER_SPAWN.toString();
    List<Object> eventParameters = new ArrayList<>();

    eventParameters.add(playerSpawnPoint);
    Event event = new Event(eventType, eventParameters, EngineConstants.DEFAULT_TIMELINES.GAME_MILLIS.toString(), getClientConnectionID().intValue(), engine.gameTimelineInMillis.getTime(), true);

    engine.getEventManager().raiseEvent(event, true);
  }

  @Override
  public EventHandler getEventHandler() {
    return eventHandler;
  }

  public void reset() {
    engine.removeGameObjects(bubblesObjectID);
    bubbleMap.clear();
    bubbles.clear();
    spawnBubbles();
  }

}