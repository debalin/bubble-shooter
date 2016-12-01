package com.debalin;

import com.debalin.characters.Bubble;
import com.debalin.characters.Player;
import com.debalin.characters.SpawnPoint;
import com.debalin.engine.MainEngine;
import com.debalin.engine.events.Event;
import com.debalin.engine.events.EventHandler;
import com.debalin.engine.scripting.ScriptManager;
import com.debalin.util.Constants;
import processing.core.PVector;

import java.util.List;

public class GameEventHandler implements EventHandler {

  BubbleShooterManager bubbleShooterManager;

  public GameEventHandler(BubbleShooterManager bubbleShooterManager) {
    this.bubbleShooterManager = bubbleShooterManager;
  }

  public void onEvent(Event event) {
    switch (event.getEventType()) {
      case "USER_INPUT":
        handleUserInput(event);
        break;
      case "PLAYER_DEATH":
        handlePlayerDeath();
        break;
      case "BUBBLE_SPAWN":
        handleBubbleSpawn(event);
        break;
      case "PLAYER_SPAWN":
        handlePlayerSpawn(event);
        break;
      case "NULL":
        break;
      case "RECORD_START":
        startRecording();
        break;
      case "RECORD_STOP":
        stopRecording();
        break;
      case "RECORD_PLAY":
        playRecording(event);
        break;
      case "PLAYER_FIRE":
        handlePlayerFire(event);
        break;
      case "BUBBLE_HIT":
        handleEnemyHit(event);
        break;
      case "SCRIPT":
        handleScripts(event);
        break;
    }
  }

  private void handleScripts(Event event) {
    List<Object> eventParameters = event.getEventParameters();
    String scriptFunctionName = (String) eventParameters.get(0);

    if (scriptFunctionName.equals(bubbleShooterManager.engine.scriptFunctionName)) {
      bubbleShooterManager.engine.bindScriptObjects();
      ScriptManager.loadScript(bubbleShooterManager.engine.scriptPath);
      ScriptManager.executeScript(bubbleShooterManager.engine.scriptFunctionName);
    }
  }

  private void handleEnemyHit(Event event) {
    List<Object> eventParameters = event.getEventParameters();
    int enemyID = (Integer) eventParameters.get(0);


  }

  private void handlePlayerFire(Event event) {
    List<Object> eventParameters = event.getEventParameters();
    PVector bubbleInitPosition = (PVector) eventParameters.get(0);


  }

  private void startRecording() {
    bubbleShooterManager.engine.takeSnapshot();
    bubbleShooterManager.engine.getEventManager().setRecording(true);
  }

  private void stopRecording() {
    bubbleShooterManager.engine.getEventManager().setRecording(false);
  }

  private void playRecording(Event event) {
    List<Object> eventParameters = event.getEventParameters();
    String replaySpeed = (String) eventParameters.get(0);

    switch (replaySpeed) {
      case "NORMAL":
        bubbleShooterManager.engine.replaySpeed = MainEngine.ReplaySpeed.NORMAL;
        bubbleShooterManager.engine.playRecordedGameObjects(1f);
        break;
      case "SLOW":
        bubbleShooterManager.engine.replaySpeed = MainEngine.ReplaySpeed.SLOW;
        bubbleShooterManager.engine.playRecordedGameObjects(2f);
        break;
      case "FAST":
        bubbleShooterManager.engine.replaySpeed = MainEngine.ReplaySpeed.FAST;
        bubbleShooterManager.engine.playRecordedGameObjects(0.5f);
        break;
    }
  }

  private void handlePlayerSpawn(Event event) {
    List<Object> eventParameters = event.getEventParameters();

    bubbleShooterManager.player = new Player(bubbleShooterManager.engine, (SpawnPoint) eventParameters.get(0));
    bubbleShooterManager.playerObjectID = bubbleShooterManager.engine.registerGameObject(bubbleShooterManager.player, bubbleShooterManager.playerObjectID, true);
    bubbleShooterManager.player.setConnectionID(bubbleShooterManager.getClientConnectionID().intValue());
  }

  private void handleBubbleSpawn(Event event) {
    List<Object> eventParameters = event.getEventParameters();
    Constants.BUBBLE_TYPES bubbleType = (Constants.BUBBLE_TYPES) eventParameters.get(0);
    int bubbleID = (Integer) eventParameters.get(1);
    PVector position = (PVector) eventParameters.get(2);
    PVector initVelocity = (PVector) eventParameters.get(3);
    PVector color = (PVector) eventParameters.get(4);

    Bubble bubble;
    switch (bubbleType) {
      case INIT:
        bubble = new Bubble(bubbleShooterManager.engine, position, bubbleID, initVelocity, color, Constants.BUBBLE_TYPES.INIT, bubbleShooterManager.bubbleMap, bubbleShooterManager);
        bubbleShooterManager.bubbles.add(bubble);
        bubbleShooterManager.bubbleMap.put(bubbleID, bubble);
        bubbleShooterManager.bubblesObjectID = bubbleShooterManager.engine.registerGameObject(bubble, bubbleShooterManager.bubblesObjectID, true);
        break;
      case FIRE:
        bubble = new Bubble(bubbleShooterManager.engine, position, bubbleID, initVelocity, color, Constants.BUBBLE_TYPES.FIRE, bubbleShooterManager.bubbleMap, bubbleShooterManager);
        bubbleShooterManager.bubbles.add(bubble);
        bubbleShooterManager.bubblesObjectID = bubbleShooterManager.engine.registerGameObject(bubble, bubbleShooterManager.bubblesObjectID, true);
        break;
    }
  }

  private void handlePlayerDeath() {
    bubbleShooterManager.engine.delay(3000);
    bubbleShooterManager.reset();
  }

  private void handleUserInput(Event event) {
    List<Object> eventParameters = event.getEventParameters();
    int key = (Integer) eventParameters.get(0);
    boolean set = (Boolean) eventParameters.get(1);
    Player player = bubbleShooterManager.player;

    player.handleKeypress(key, set);

  }

}
