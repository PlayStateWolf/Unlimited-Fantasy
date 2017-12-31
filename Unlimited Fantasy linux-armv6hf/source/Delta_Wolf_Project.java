import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Delta_Wolf_Project extends PApplet {

/*
 Delta Wolf Project - Made with Processing 3.1 (https://processing.org/)
 ---------------------------------------------------
 This is a full open source game project heavily inspired by
 Zarkonnen's Soapquest: https://github.com/Zarkonnen/SoapQuest
 
 */

//Map Variables - Used to generate and display maps

Tile[][] map; //Creates a 2D array of Tile objects(The Map)
int cols; //Width(in Tile objects) of a map
int rows; //Height(in Tile objects) of a map
int tileWidth = 20; //Width(in pixels) of a Tile object
int tileHeight = 20; //Height(in pixels) of a Tile object
int randomX; //Used for picking a random X location
int randomY; //Used for picking a random Y location
boolean found = false; //Has a leader been found
int searchIndex = 1;   //Size of search area(in tiles)
String hemisphere; //Decides whether the Snow and Ice appear north or south in map generation

//Player Variables - Player location, inventory and info

int playerX; //The X location of the Tile object the player is on
int playerY; //The Y location of the Tile object the player is on
String playerInventory = "None"; //The player's inventory
Boolean dropping = false; //Is the player dropping an object
int fontSize = 18; //Size of the font
String playerLog; //The In-Game Console's Current Message
boolean mapOpened = false; //When true, displays the world map
float inputDelay = 0.25f; //Slows down the speed of input to match human speed(not machine speed)

//Save variables

String[] data; //The String to load the save data into
String[] saveFile = new String[(cols*rows)*2+1]; //The String to write to the save file with
int count = 0; //Helper int to keep control of saving and loading the save data

//Startup
boolean isRunning = false; //Is set to true when the game has finished starting up
boolean createdMap = false; //Set to true when the core map data(Array) is made
boolean isLoaded = false; //Set to true when the save data is loaded or map generation finished
float step = 0; //Used to aid the start up process

//This is used for applying various settings such as fullScreen() before setup()
//A neat trick could be loading in a config file which decides if it is full screen or not
public void settings() {
  //Remove the '//' below to make the game fullscreen, but just remember to add '//' just before size()
  //fullScreen();
  size(displayWidth/2+displayWidth/4,displayHeight/2+displayHeight/3);
}

//Runs once and at the very beginning of a program
public void setup() {
  
  //Sets the background color using the RGB format
  background(65, 60, 65);

  //Sets how many frames per second
  frameRate(30);

  //Sets up the in-game console
  textFont(createFont("SourceCodePro-Regular.ttf", 14));
  
  //cols and rows get assigned a value here because the width and height aren't set before size() or fullScreen()
  cols = width/4; //The width of a map is directly related to the width of the window(You can change this to any positive int)
  rows = height/4; //The height of a map is directly related to the height of the window(You can change this to any positive int)
  saveFile = new String[(cols*rows)*2+1]; //The String to write to the save file with
  
}

/*

 The draw() function continually runs over and over again in a loop.
 It's mostly used for drawing/displaying objects onto the screen
 
 */
public void draw() {
  
  //If the game hasn't loaded
  if(!isRunning) {
    
    /*
      
      If we were to load and generate everything in setup(), everyone will be looking
      at a blank screen with nothing(visually) happening until the game is finally loaded.
      
      To help this, I made a loading screen which gives live updates of the game being loaded.
      (Please note: This loading screen does take up a little more processing power than standard loading)
      
    */
    
    //Everytime step is increased by 1, exactly one run of the draw() loop is run.
    
    if(step == 0) {
    
      //Send a message to the log that we have started loading the game
      if(frameCount<=1) {
        playerLog = "Loading";
        System.out.println("Loading");
        
      }
      
      step++;
      
    }
    else if(step == 1) {
    
      /*
  
      Creates a map and fills it with empty Tile objects
      For more info on Functions, vist: http://www.learnjavaonline.org/en/Functions
   
      */
   
      createMap();
      
      step++;
      
    }
    else if(step >= 2 && step <= 7) {

      //Loads save file if it exists, if it doesn't, it generates a new map
      loadSave();
      
    }
    
  }
  
  //If the game is running(this is the main game's loop)
  else {
  
    //If the player doesn't have the world map open
    if(!mapOpened) {
      
      //This just shifts the camera/screen to focus on the player
      pushMatrix();
      translate((0-playerX)*tileWidth+width/2-tileWidth,(0-playerY)*tileHeight+height/2-tileHeight);
      
    }

    //Sets the background color
    background(65, 60, 65);

    //Display each Tile object
    for (int x = 0; x < cols; x++) {
      for (int y = 0; y < rows; y++) {
        
        //To save a lot of memory/keep the game running fast, we should only draw the tile objects within the screen(with the exception that the world map is opened)
        //If you want to see something really cool, delete the '||mapOpened' at the end of the if statements. You can use this to add a Fog of War effect
        if((x*tileWidth-tileWidth>(playerX*tileWidth-tileWidth*2)-tileWidth*35)&&(x*tileWidth-tileWidth<(playerX*tileWidth-tileWidth*2)+tileWidth*38)||mapOpened) {
          if((y*tileHeight-tileHeight>(playerY*tileHeight-tileHeight*2)-tileHeight*22)&&(y*tileHeight-tileHeight<(playerY*tileHeight-tileHeight*2)+tileHeight*25)||mapOpened) {
            map[x][y].display();
          }
        }
        
      }
    }
  
    if(!mapOpened) {
      
      //This shifts the camera/screen back to it's original location
      popMatrix();
      
    }
    
    //If the world map is opened
    else {
      
      //Draw the red box that represents the main screen where the player is
      stroke(255,0,0);
      strokeWeight(3);
      noFill();
      rect(playerX*tileWidth-(tileHeight*cols/20)*2+tileWidth,playerY*tileHeight-(tileHeight*rows/10)+tileHeight,tileWidth*cols/5,tileHeight*rows/5);
      
    }

    //Display the In-Game Console
    displayConsole();
    
  }
  
  //Update inputDelay(Check keyPressed() for it's usage)
  if(inputDelay>0)
    inputDelay--;
  
  //If inputDelay is less than 0, set to 0(In the case that inputDelay ever goes below 0)
  else if(inputDelay<0)
    inputDelay=0;
  
}

//Every time a button on the keyboard is pressed, this runs(loops at incredible speed if held down)
public void keyPressed() {

  //To counter the super machine speed of keyPressed(), we need to delay the input speed
  
  //This checks to see if we are ready for input
  if(inputDelay <= 0) {
  
    //Press the 'W','A','S','D' keys for movement and interacting with the environment

    if ( key == 'w' || key == 'W') {  //Notice the capitals? The machine treats them as different entities(ASCI characters)

      //So that the player can't leave the map
      if ( playerY >= 1) {

        //Interact with the tile adjacent to the player
        playerInteract(playerX, playerY-1);
        inputDelay = 0.5f;
      }
    }

    if ( key == 's' || key == 'S') {

      if (playerY <= rows-2) {

        playerInteract(playerX, playerY+1);
        inputDelay = 0.5f;
      }
    }

    if ( key == 'a' || key == 'A') {

      if ( playerX >= 1) {

        playerInteract(playerX-1, playerY);
        inputDelay = 0.5f;
      }
    }

    if ( key == 'd' || key == 'D') {

      if ( playerX <= cols-2) {

        playerInteract(playerX+1, playerY);inputDelay = 1;
        inputDelay = 0.5f;
      }
    }

    //Press the 'ENTER' button to save the game

    if ( key == ENTER) {

      //Log before saving the game
      playerLog = "Saving Data...";
      System.out.println("Saving Data...");

      //The save format goes like so:

      //Record all the tile biomes
      int count = 0;
      for (int x=0; x<cols; x++) {
        for (int y=0; y<rows; y++) {
          saveFile[count] = map[x][y].getBiome();
          count++;
        }
      }

      //Then record all the tile objects
      for (int x=0; x<cols; x++) {
        for (int y=0; y<rows; y++) {
          saveFile[count] = map[x][y].getObject();
          count++;
        }
      }

      //And lastly, record the player's inventory
      saveFile[count] = playerInventory;

      //Save the recorded data into a save file
      saveStrings("/data/save.txt", saveFile);

      //Then log that we have finished saving
      playerLog = "Data Saved!";
      System.out.println("Data Saved!");
    }

    //Press the 'SPACE' button to drop objects
    if ( key == ' ') {
      //If we are already dropping, cancle the drop
      if (dropping) {
        playerLog = "Dropping canceled";
        dropping = false;
      //If the player has no object to drop
      } else if (playerInventory.equals("None")) {
        playerLog = "You have no object to drop";
        dropping = false;
      //Start to drop an object
      } else {
        playerLog = "Choose a direction to drop the "+playerInventory+" using the 'W','A','S','D' keys. To cancel, press 'Space' again";
        dropping = true;
      }
    }
  
    //Press the 'M' key to open/close the world map
    if ( key == 'm' || key == 'M') {
      
      //If the world map isn't open, open it
      if(mapOpened == false) {
        
        //For the world map, we just need to rescale the Tile objects to be smaller
        tileWidth = width/cols;
        tileHeight = height/rows;
        
        for(int x=0; x<cols; x++) {
          for(int y=0; y<rows; y++) {
            map[x][y].setX(x*tileWidth);
            map[x][y].setY(y*tileHeight);
            map[x][y].setWidth(tileWidth);
            map[x][y].setHeight(tileHeight);
          }
        }
        mapOpened = true;
      
      }
      
      //If the world map is opened, close it
      else if(mapOpened == true) {
        
        //We just rescale Tile objects to their original size
        tileWidth = 20;
        tileHeight = 20;
        for(int x=0; x<cols; x++) {
          for(int y=0; y<rows; y++) {
            map[x][y].setX(x*tileWidth);
            map[x][y].setY(y*tileHeight);
            map[x][y].setWidth(tileWidth);
            map[x][y].setHeight(tileHeight);
          }
        }
        mapOpened = false;
      
      }
    
    }
    
  }
  
}

//Handles all the player movement/object interaction
public void playerInteract(int tileX, int tileY) {

  //If we are dropping an object
  if (dropping) {
    
    //And the space is clear of objects
    if (map[tileX][tileY].getObject().equals("None")) {
      
      //Drop the object
      map[tileX][tileY].setObject(playerInventory);
      playerInventory = "None";
      dropping = false;
      
      //Update the log
      playerLog = "You dropped the "+map[tileX][tileY].getObject();
      
    }
    
    //If the space isn't clear
    else {
      
      //Don't drop the object
      dropping = false;
      
      //Update the log
      playerLog = "There's something blocking the way";
      
    }
  }
  //If the path is clear of objects
  else if (map[tileX][tileY].getObject().equals("None")) {
    
    //And the biome is walkable
    if (map[tileX][tileY].getWalkable()) {
      
      //Move the player
      map[playerX][playerY].setObject("None");
      map[tileX][tileY].setObject("Player");
      
      //Update player location
      playerX = tileX;
      playerY = tileY;
      
      //Update the log
      playerLog = "To move: W,A,S,D keys. To pick up objects: Walk into them. To drop objects: 'SPACE'. To view map: 'M'. To save: 'ENTER'";
      
    }
    
    //If the biome isn't walkable
    else {
      
      //Update the log
      playerLog = "You can't walk in "+map[tileX][tileY].getBiome();
      
    }
  }
  
  //All the code past this point can only be run when there's an object in the path
  
  //If player's inventory is empty
  else if (playerInventory.equals("None")) {
    
    //Check to see if the object has a pickupable(an object that can be held)
    if(map[tileX][tileY].getPickupable()) {
      
      //Checks to see if the object itself is being picked up or it's resource(Ex. Trees and Tree Branches)
      if(map[tileX][tileY].getIsResource()) {
        
        //Pick up the object
        playerInventory = map[tileX][tileY].getObject();
        map[tileX][tileY].setObject("None");
        playerLog = "You picked up the "+playerInventory;
        
      }
      else {
        
        //Pick up the object's resource
        playerInventory = map[tileX][tileY].getResource();
        playerLog = "You grabbed the "+playerInventory+" from the "+map[tileX][tileY].getObject();
        
      }
    }
    else {
      
      //You can't pick the object up
      playerLog = "You can't pick up the "+map[tileX][tileY].getObject();
    }
  }
  
  //If the player's inventory isn't empty
  else {
    
    //Run the Tile's interactDictionary() (Used for all Object on Object interactions)
    map[tileX][tileY].interactDictionary();
    
  }
}

//Creates a map of empty tiles
public void createMap() {
  
  //Send a message to the log
  System.out.println(playerLog = "Creating The Map");
  displayConsole();

  //Initialize all Tile objects that make up the map
  map = new Tile[cols][rows];
  for (int x = 0; x < cols; x++) {
    for (int y = 0; y < rows; y++) {
      
      //initialize that specific Tile object as either a "Shallow Water" tile or as a "Grassland" tile
      int random = round(random(1));
      
      if(random == 0) {
      map[x][y] = new Tile(x*tileWidth, y*tileHeight, tileWidth, tileHeight, "Deep Water", "None");
      }
      else {
      map[x][y] = new Tile(x*tileWidth, y*tileHeight, tileWidth, tileHeight, "Grassland", "None");
      }
      
    }
  }
}

//Handles loading the save data/generating a new random map if there is no save
public void loadSave() {
  
  //Set data(String) to contain all the data in "/data/save.txt" if it exists
  data = loadStrings("data/save.txt");

  //If there is save data
  if (data!=null) {
    
    if(step == 2) {
      
      //Start loading the save
      System.out.println(playerLog = "Loading Save Data");
      displayConsole();

      //Send a message to the In-Game Console
      playerLog = "Loading Biomes";
      displayConsole();
      System.out.println("Loading Biomes");
    
      //Load all the tile biomes
      for (int x=0; x<cols; x++) {
        for (int y=0; y<rows; y++) {
          map[x][y].setBiome(data[count]);
          count++;
        }
      }
        
      step++;
        
    }
    
    else if(step == 3) {
      
      playerLog = "Loading Objects";
      displayConsole();
      System.out.println("Loading Objects");
      
      //Then load all the tile objects
      for (int x=0; x<cols; x++) {
        for (int y=0; y<rows; y++) {
          map[x][y].setObject(data[count]);
          if (map[x][y].getObject().equals("Player")) {
            playerX = x;
            playerY = y;
          }
          count++;
        }
      }
    
      step++;
    
    }
    
    else if(step == 4) {
    
      playerLog = "Loading Inventory";
      displayConsole();
      System.out.println("Loading Inventory");
      
      //And lastly, load the player's inventory
      playerInventory = data[count];
    
      //Loading is finished, so update the game
      playerLog = "Save loaded!";
      displayConsole();
      System.out.println("Save Loaded!");
      
      isRunning = true;
      
    }
  
  }

  //If no save data
  if (data==null) {

    //Create a randomly generated map
    randomMap();
    
  }
  
}

//Creates a randomly generated map
public void randomMap() {
  
  if(step == 2) {
  
    //Send a message to the In-Game Console
    playerLog = "Generating Land Formations";
    displayConsole();
    System.out.println("Generating Land Formations");
    
    int r = round(random(0,1));
    if(r == 0) hemisphere = "Northern";
    else hemisphere = "Southern";
    System.out.println(hemisphere);
    
    //And now run the simulation for a set number of steps
    for(int i=0; i<25; i++){
      map = processMap(map,"Grassland","Deep Water",4,40, true);
    }
  
  step++;
  
  }
  
  else if(step == 3) {
  
    //Send a message to the In-Game Console
    playerLog = "Adding Water";
    displayConsole();
    System.out.println("Adding Water");
    
    map = addWater(map);
    
    //And now run the simulation for a set number of steps
    /*
    for(int i=0; i<25; i++){
      randomX = round(random(cols-1));
      randomY = round(random(rows-1));
      while(!map[randomX][randomY].getBiome().equals("Grassland")) {
        randomX = round(random(cols-1));
        randomY = round(random(rows-1));
      }
      map[randomX][randomY].setBiome("Forest");
      map[randomX][randomY].setLeader(true);
    }
    */
  
  step++;
  
  }
  
    else if(step == 4) {
  
    //Send a message to the In-Game Console
    playerLog = "Adding Forests";
    displayConsole();
    System.out.println("Adding Biomes");
    
    map = addForests(map);
    for(int i=0; i<2; i++){
      map = processMap(map,"Forest","Grassland",5,50, false);
    }
    map = addSavannahs(map);
    for(int i=0; i<2; i++){
      map = processMap(map,"Savannah","Grassland",5,50, false);
    }
    //addRockySoil(map,10);
    /*map = addRockySoil(map);
    for(int i=0; i<2; i++){
      map = processMap(map,"Rocky Soil","Forest",4,22, false);
    }*/
    //addSnow(map,10);
    //map = addTaiga(map);
    map = addTaiga(map);
    for(int i=0; i<10; i++){
      map = processMap(map,"Taiga","Forest",8,110, false);
    }
    
    map = addSnow(map);
    for(int i=0; i<10; i++){
      map = processMap(map,"Snow","Taiga",8,110, false);
    }
    //addSleet(map,10);
    
    //addTemperature(map);
    
    //And now run the simulation for a set number of steps
    /*
    for(int i=0; i<25; i++){
      randomX = round(random(cols-1));
      randomY = round(random(rows-1));
      while(!map[randomX][randomY].getBiome().equals("Grassland")) {
        randomX = round(random(cols-1));
        randomY = round(random(rows-1));
      }
      map[randomX][randomY].setBiome("Forest");
      map[randomX][randomY].setLeader(true);
    }
    */
  
  step++;
  
  }
  
  else if(step == 5) {

    /*
    //Once the biomes are set, we create objects that spawn in them
    //For this, we use chance percentage out of the specified number(Mostly out of 200)
  
    //Log
    playerLog = "Adding Sugar, Spice and Everything Nice";
    displayConsole();
    
    //This is pretty straightforward. Calculate the chances for an object to spawn
    
    //For all tiles
    for (int x=0; x<cols; x++) {
      for (int y=0; y<rows; y++) {
        
        //The chances are dependant on the biome(You don't want "Long Grass" to spawn in "Deep Water"
        
        //Grassland
        if (map[x][y].getBiome().equals("Grassland")) {
          int chance = round(random(300));
          if (chance <= 1) {
            map[x][y].setObject("Tree");
          }
          if (chance >=2 && chance <= 3) {
            map[x][y].setObject("Small Stone");
          }
        }
        
        //Forest
        if (map[x][y].getBiome().equals("Forest")) {
          int chance = round(random(300));
          if (chance <= 20) {
            map[x][y].setObject("Tree");
          }
          if (chance >= 21&&chance <= 21) {
            map[x][y].setObject("Small Stone");
          }
          if (chance >= 25&&chance <= 25) {
            map[x][y].setObject("Big Stone");
          }
          if (chance >= 27&&chance <= 30) {
            map[x][y].setObject("Berry Bush");
          }
        }
        
        //Desert
        if (map[x][y].getBiome().equals("Desert")) {
          int chance = round(random(200));
          if (chance <=2) {
            map[x][y].setObject("Small Stone");
          }
          if (chance >=3&&chance<=4) {
            map[x][y].setObject("Starfish");
          }
          if (chance == 4) {
            map[x][y].setObject("Starfish");
          }
          if (chance == 5) {
            map[x][y].setObject("Shell");
          }
        }
        
        //Savanah
        if (map[x][y].getBiome().equals("Savanah")) {
          int chance = round(random(200));
          if (chance <=10) {
            map[x][y].setObject("Long Grass");
          }
          if (chance >=11&&chance<=12) {
            map[x][y].setObject("Small Stone");
          }
          if (chance ==13) {
            map[x][y].setObject("Big Stone");
          }
        }
        
        //Snow
        if (map[x][y].getBiome().equals("Snow")) {
          int chance = round(random(200));
          if (chance <=6) {
            map[x][y].setObject("Tree");
          }
          if (chance ==7) {
            map[x][y].setObject("Small Stone");
          }
        }
        
        //Taiga
        if (map[x][y].getBiome().equals("Taiga")) {
          int chance = round(random(200));
          if (chance <=15) {
            map[x][y].setObject("Tree");
          }
        }
        
      }
    }
    */
    step++;
    
  }
  
  else if(step == 6) {
    
    //Log
    playerLog = "Finding a home";
    displayConsole();

    //Find a neat place to put the player on the map
    found = false;
    while (!found) {
      randomX = round(random(cols-1));
      randomY = round(random(rows-1));
      
      //Has to be walkable and free of objects
      if (map[randomX][randomY].getWalkable()==true&&
        map[randomX][randomY].getObject().equals("None")) {
          
        //We found a home!
        playerX = randomX;
        playerY = randomY;
        found = true;
        
      }
    }
    
    //The map is fully generated, so lets update the game
    
    //Log
    map[playerX][playerY].setObject("Player");
    playerLog = "Use the 'W','A','S','D' keys to move";
    
    //The game is now running
    isRunning = true;
    
  }
}

//Display/update the In-Game Console(Dark Bar on the bottom of the screen)
public void displayConsole() {
  
  //Draw the background of the In-Game Console
  stroke(0);
  strokeWeight(1);
  fill(95,90,95,200);
  rect(0,height-80,width,80);

  //Picks the color White
  fill(255);

  //Draws White text
  text(playerLog, 0, height-77, 1280, 40);
  
  //Only draw the following text if the game is running
  if(isRunning) {
    text("Player Inventory: "+playerInventory, 0, height-77+fontSize, 1280, 40);
    text("Player X: "+playerX+", Player Y: "+playerY, 0, height-77+fontSize*2, 1280, 40);
    text("Biome: "+map[playerX][playerY].getBiome(), 0, height-77+fontSize*3, 1280, 40);
  }
  
}

public Tile[][] processMap(Tile[][] oldMap, String landType1, String landType2,int countNeighbourSize, int nbsLimit, boolean allTiles){
  Tile[][] newMap = new Tile[cols][rows];
  
  for (int x = 0; x < cols; x++) {
    for (int y = 0; y < rows; y++) {
      if(allTiles) newMap[x][y] = new Tile(x*tileWidth, y*tileHeight, tileWidth, tileHeight, "Shallow Water", "None");
      else newMap[x][y] = oldMap[x][y];
    }
  }
  
  //Loop over each row and column of the map
  for(int x=0; x<cols; x++){
    for(int y=0; y<rows; y++){
      int nbs = countNeighbours(oldMap, landType1, x, y, countNeighbourSize);
      //The new value is based on our simulation rules
      //First, if a cell is alive but has too few neighbours, kill it.
        if(oldMap[x][y].getBiome().equals(landType1)){
          if(nbs < nbsLimit){ //Death limit
            newMap[x][y].setBiome(landType2);
          }
          else{
            newMap[x][y].setBiome(landType1);
          }
        } //Otherwise, if the cell is dead now, check if it has the right number of neighbours to be 'born'
        else if(oldMap[x][y].getBiome().equals(landType2)){
          if(nbs > nbsLimit){ //Birth Limit
            newMap[x][y].setBiome(landType1);
          }
          else{
            newMap[x][y].setBiome(landType2);
          }
        }
    }
  }
  return newMap;
}

//Returns the number of tiles in a ring around (x,y) that are of the passed biome.
public int countNeighbours(Tile[][] map, String biome, int x, int y, int countBy){
  int count = 0;
  for(int i=-countBy; i<=countBy; i++){
    for(int j=-countBy; j<=countBy; j++){
      int neighbour_x = x+i;
      int neighbour_y = y+j;
      //If we're looking at the middle point
      if(i == 0 && j == 0){
        //Do nothing, we don't want to add ourselves in!
      }
      //Otherwise, a normal check of the neighbour
      if(neighbour_x>=0&&neighbour_x<cols) {
        if(neighbour_y>=0&&neighbour_y<rows) {
          if(map[neighbour_x][neighbour_y].getBiome().equals(biome)){
            count = count + 1;
          }
        }
      }
    }
  }
  return count;
}

public Tile[][] addWater(Tile[][] oldMap) {
  
  Tile[][] newMap = oldMap;
  
  for(int x=0; x<cols; x++){
    for(int y=0; y<rows; y++){
      if(oldMap[x][y].getBiome().equals("Deep Water")) {
        int nbs = countNeighbours(oldMap, "Grassland", x, y, PApplet.parseInt(random(2,10)));
        if(nbs>1)
          newMap[x][y].setBiome("Shallow Water");
      }
    }
  }
  
  for(int i=0; i<15; i++){
    map = processMap(map,"Shallow Water","Deep Water",1,5, false);
  }
  
  for(int x=0; x<cols; x++){
    for(int y=0; y<rows; y++){
      if(oldMap[x][y].getBiome().equals("Grassland")) {
        int nbs = countNeighbours(oldMap, "Grassland", x, y, 13);
        if(nbs<365)
          newMap[x][y].setBiome("Beach");
      }
    }
  }
  
  for(int i=0; i<1; i++){
    map = processMap(map,"Beach","Grassland",1,4, false);
  }
  
  return newMap;
}

public Tile[][] addForests(Tile[][] oldMap){
  Tile[][] newMap = new Tile[cols][rows];
  
  for (int x = 0; x < cols; x++) {
    for (int y = 0; y < rows; y++) {
      newMap[x][y] = oldMap[x][y];
    }
  }
  
  //Loop over each row and column of the map
  for(int x=0; x<cols; x++){
    for(int y=0; y<rows; y++){
      int nbs = countNeighbours(oldMap, "Grassland", x, y, 10);
      //The new value is based on our simulation rules
      //First, if a cell is alive but has too few neighbours, kill it.
        if(oldMap[x][y].getBiome().equals("Grassland")){
          if(nbs >50){
            newMap[x][y].setBiome("Forest");
          }
        }
    }
  }
  //growBiome(newMap,10,"Grassland");
  
  for (int x = 0; x < cols; x++) {
    for (int y = 0; y < rows; y++) {
      if(newMap[x][y].getBiome().equals("Forest")) {
      
        //initialize that specific Tile object as either a "Shallow Water" tile or as a "Grassland" tile
        int random = round(random(100));
      
        if(random < 37) {
          newMap[x][y].setBiome("Grassland");
        }
      
      }
    }
  }
  return newMap;
}

public Tile[][] addSavannahs(Tile[][] oldMap){
  Tile[][] newMap = new Tile[cols][rows];
  
  for (int x = 0; x < cols; x++) {
    for (int y = 0; y < rows; y++) {
      newMap[x][y] = oldMap[x][y];
    }
  }
  
  //Loop over each row and column of the map
  for(int x=0; x<cols; x++){
    for(int y=0; y<rows; y++){
      int nbs = countNeighbours(oldMap, "Grassland", x, y, 10);
      //The new value is based on our simulation rules
      //First, if a cell is alive but has too few neighbours, kill it.
        if(oldMap[x][y].getBiome().equals("Grassland")){
          if(nbs >50){
            newMap[x][y].setBiome("Savannah");
          }
        }
    }
  }
  //growBiome(newMap,10,"Grassland");
  
  for (int x = 0; x < cols; x++) {
    for (int y = 0; y < rows; y++) {
      if(newMap[x][y].getBiome().equals("Savannah")) {
      
        //initialize that specific Tile object as either a "Shallow Water" tile or as a "Grassland" tile
        int random = round(random(100));
      
        if(random < 37) {
          newMap[x][y].setBiome("Grassland");
        }
      
      }
    }
  }
  return newMap;
}

/*Tile[][] addRockySoil(Tile[][] oldMap){
  Tile[][] newMap = new Tile[cols][rows];
  
  for (int x = 0; x < cols; x++) {
    for (int y = 0; y < rows; y++) {
      newMap[x][y] = oldMap[x][y];
    }
  }
  
  //Loop over each row and column of the map
  for(int x=0; x<cols; x++){
    for(int y=0; y<rows; y++){
      int nbs = countNeighbours(oldMap, "Forest", x, y, 10);
      //The new value is based on our simulation rules
      //First, if a cell is alive but has too few neighbours, kill it.
        if(oldMap[x][y].getBiome().equals("Forest")){
          if(nbs >50){
            newMap[x][y].setBiome("Rocky Soil");
          }
        }
    }
  }
  //growBiome(newMap,10,"Grassland");
  
  for (int x = 0; x < cols; x++) {
    for (int y = 0; y < rows; y++) {
      if(newMap[x][y].getBiome().equals("Rocky Soil")) {
      
        //initialize that specific Tile object as either a "Shallow Water" tile or as a "Grassland" tile
        int random = round(random(100));
      
        if(random < 76) {
          newMap[x][y].setBiome("Forest");
        }
      
      }
    }
  }
  return newMap;
}*/

public Tile[][] addTaiga(Tile[][] oldMap){
  Tile[][] newMap = new Tile[cols][rows];
  
  for (int x = 0; x < cols; x++) {
    for (int y = 0; y < rows; y++) {
      newMap[x][y] = oldMap[x][y];
    }
  }
  
  //Loop over each row and column of the map
  for(int x=0; x<cols; x++){
    for(int y=0; y<rows; y++){
      int nbs = countNeighbours(oldMap, "Forest", x, y, 10);
      //The new value is based on our simulation rules
      //First, if a cell is alive but has too few neighbours, kill it.
        if(oldMap[x][y].getBiome().equals("Forest")){
          if(nbs >50){
            newMap[x][y].setBiome("Taiga");
          }
        }
    }
  }
  //growBiome(newMap,10,"Grassland");
  
  for (int x = 0; x < cols; x++) {
    for (int y = 0; y < rows; y++) {
      if(newMap[x][y].getBiome().equals("Taiga")) {
      
        //initialize that specific Tile object as either a "Shallow Water" tile or as a "Grassland" tile
        int random = round(random(100));
      
        if(random < 35) {
          newMap[x][y].setBiome("Forest");
        }
      
      }
    }
  }
  return newMap;
}

public Tile[][] addSnow(Tile[][] oldMap){
  Tile[][] newMap = new Tile[cols][rows];
  
  for (int x = 0; x < cols; x++) {
    for (int y = 0; y < rows; y++) {
      newMap[x][y] = oldMap[x][y];
    }
  }
  
  //Loop over each row and column of the map
  for(int x=0; x<cols; x++){
    for(int y=0; y<rows; y++){
      int nbs = countNeighbours(oldMap, "Taiga", x, y, 10);
      //The new value is based on our simulation rules
      //First, if a cell is alive but has too few neighbours, kill it.
        if(oldMap[x][y].getBiome().equals("Taiga")){
          if(nbs >50){
            newMap[x][y].setBiome("Snow");
          }
        }
    }
  }
  //growBiome(newMap,10,"Grassland");
  
  for (int x = 0; x < cols; x++) {
    for (int y = 0; y < rows; y++) {
      if(newMap[x][y].getBiome().equals("Snow")) {
      
        //initialize that specific Tile object as either a "Shallow Water" tile or as a "Grassland" tile
        int random = round(random(100));
      
        if(random < 38) {
          newMap[x][y].setBiome("Taiga");
        }
      
      }
    }
  }
  return newMap;
}

public int growBiome(Tile[][] oldMap, int numberOfRuns, String biome) {
  
  Tile[][] newMap = new Tile[cols][rows];
  
  for (int x = 0; x < cols; x++) {
    for (int y = 0; y < rows; y++) {
      newMap[x][y] = oldMap[x][y];
    }
  }
  
  
  if(numberOfRuns > 0) {
    for (int x = 0; x < cols; x++) {
      for (int y = 0; y < rows; y++) {
        if(oldMap[x][y].getLeader() == true) {
          for(int i=-numberOfRuns; i<=numberOfRuns; i++){
            for(int j=-numberOfRuns; j<=numberOfRuns; j++){
              int neighbour_x = x+i;
              int neighbour_y = y+j;
              //If we're looking at the middle point
              if(i == 0 && j == 0){
                //Do nothing, we don't want to add ourselves in!
              }
              //Otherwise, a normal check of the neighbour
              if(neighbour_x>=0&&neighbour_x<cols) {
                if(neighbour_y>=0&&neighbour_y<rows) {
                  if(!oldMap[neighbour_x][neighbour_y].getBiome().equals(biome)&&
                     !oldMap[neighbour_x][neighbour_y].getBiome().equals("Shallow Water")&&
                     !oldMap[neighbour_x][neighbour_y].getBiome().equals("Deep Water")&&
                     !oldMap[neighbour_x][neighbour_y].getBiome().equals("Beach")){
                        
                       int random = round(random(1));
                       /*
                       if(biome.equals("Snow")) {
                         if(random==1&&newMap[neighbour_x][neighbour_y].getBiome().equals("Forest"))
                           newMap[neighbour_x][neighbour_y].setBiome(biome);
                       }
                       else if(random==1&&newMap[neighbour_x][neighbour_y].getBiome().equals("Grassland"))
                         newMap[neighbour_x][neighbour_y].setBiome(biome);
                       //newMap[neighbour_x][neighbour_y].setLeader(true);
                       */
                  }
                }
              }
            }
          }
        }
      }
    }
    
    //Loop over each row and column of the map
    for(int x=0; x<cols; x++){
      for(int y=0; y<rows; y++){
        int nbs = countNeighbours(oldMap, biome, x, y, 2);
        //The new value is based on our simulation rules
        //First, if a cell is alive but has too few neighbours, kill it.
        if(newMap[x][y].getBiome().equals("Grassland")){
          if(nbs < 10){ //Death limit
            newMap[x][y].setBiome(oldMap[x][y].getBiome());
          }
          else{
            newMap[x][y].setBiome(biome);
          }
        } //Otherwise, if the cell is dead now, check if it has the right number of neighbours to be 'born'
      }
    }
    
    growBiome(newMap,numberOfRuns-1, biome);
    
  }
  
  return 0;
  
}

public Tile[][] addTemperature(Tile[][] oldMap){
  Tile[][] newMap = new Tile[cols][rows];
  
  for (int x = 0; x < cols; x++) {
    for (int y = 0; y < rows; y++) {
      newMap[x][y] = oldMap[x][y];
    }
  }
  
  //Loop over each row and column of the map
  for(int y=0; y>=0 && y<rows/3; y++){
    if(hemisphere.equals("Southern")) {
      
    }
  }
  
  int random = round(random(10,20));
  growBiome(map,random,"Savannah");
  
  return newMap;
}
class Tile {
  
  //For information on Classes, please visit: http://www.wideskills.com/java-tutorial/java-classes-and-objects
  
  //Define the class variables
  float x,y,w,h;
  String biome, object, resource;
  boolean walkable, leader, searched; 
  boolean pickupable, isResource;
  
  //We then define the constructer
  Tile(float tempX, float tempY, float tempW, float tempH, String tempBiome, String tempObject) {
    x = tempX; //Tile x Location
    y = tempY; //Tile y Location
    w = tempW; //Tile width
    h = tempH; //Tile height
    biome = tempBiome; //Tile Biome
    object = tempObject; //Tile Object
    leader = false; //Whether it's a Biome Leader
    setWalkable(); //Whether the Tile's Biome is walkable
    setPickupable(); //Whether the Tile's object is pickupable
    setIsResource(); //Whether the Tile's object is an item that can be picked up by itself(self contained object)
    setResource(); //The object that is given when an object with isResource is false
  }
  
  //Draw the tile
  public void display() {
    
    //Set the drawing settings
    if(mapOpened)
      noStroke();
    else {
      strokeWeight(1);
      stroke(0,0,0,64);
    }
    
    //Determine what biome to draw
    
    if( biome.equals("None")) {
      fill(75,45,60);
    }
    if( biome.equals("Grassland")) {
      fill(180,240,180);
    }
    if( biome.equals("Shallow Water")) {
      fill(125,135,255);
    }
    if( biome.equals("Deep Water")) {
      fill(105,115,235);
    }
    if( biome.equals("Forest")) {
      fill(110,200,130);
    }
    if( biome.equals("Mangrove")) {
      fill(125,175,230);
    }
    if( biome.equals("Beach")) {
      fill(255,255,220);
    }
    if( biome.equals("Rocky Soil")) {
      fill(225,200,185);
    }
    if( biome.equals("Savannah")) {
      fill(255,240,200);
    }
    if( biome.equals("Snow")) {
      fill(245,250,255);
    }
    if( biome.equals("Taiga")) {
      fill(215,255,215);
    }
    if( biome.equals("Sleet")) {
      fill(195,235,255);
    }
    
    //Draws a square of the biome's color
    rect(x,y,w,h);
    
    //Then we draw the object on the tile
    
    //The player is an object itself
    if(object.equals("Player")) {
      fill(20,20,20);
      ellipse(x+w/2,y+h/2,w/4*3,h/4*3);
    }
    
    //Trees change color depending on their biome
    if(object.equals("Tree")) {
      if(biome.equals("Snow")) {
        fill(195,255,210);
        ellipse(x+w/2,y+w/2,w/2,h/2);
        noStroke();
        fill(195,130,185);
        ellipse(x+w/2,y+w/2,w/4,h/4);
      }
      else if(biome.equals("Mangrove")) {
        fill(130,255,165);
        ellipse(x+w/2,y+w/2,w/2,h/2);
        noStroke();
        fill(195,130,185);
        ellipse(x+w/2,y+w/2,w/4,h/4);
      }
      else {
        fill(125,200,110);
        ellipse(x+w/2,y+w/2,w/2,h/2);
        noStroke();
        fill(160,85,110);
        ellipse(x+w/2,y+w/2,w/4,h/4);
      }
    }
    if(object.equals("Small Stone")) {
      fill(230,230,255);
      ellipse(x+w/2,y+w/2,w/2,h/2);
    }
    if(object.equals("Big Stone")) {
      fill(230,230,255);
      ellipse(x+w/2,y+w/2,w,h);
    }
    if(object.equals("Tree Branch")) {
      fill(175,150,75);
      rect(x+w/4,y+h/4+h/8,w-w/2,h-h/2-h/8*2);
    }
    if(object.equals("Berry Bush")) {
      fill(20,150,100);
      ellipse(x+w/2,y+h/2,w/4*3,h/4*3);
    }
    if(object.equals("Berry")) {
      fill(255,20,255);
      ellipse(x+w/2,y+w/2,w/2,h/2);
    }
    if(object.equals("Starfish")) {
      fill(255,10,35);
      ellipse(x+w/2,y+w/2,w/2,h/2);
    }
    if(object.equals("Shell")) {
      fill(185,150,255);
      ellipse(x+w/2,y+w/2,w/2,h/2);
    }
    if(object.equals("Long Grass")) {
      fill(255,200,180);
      ellipse(x+w/2,y+w/2,w/2,h/2);
    }
    if(object.equals("Ice Shard")) {
      fill(150,185,255);
      ellipse(x+w/2,y+w/2,w/2,h/2);
    }
    if(object.equals("Crystal")) {
      fill(45,75,255);
      ellipse(x+w/2,y+w/2,w/2,h/2);
    }
    
    //interactDictionary() objects
    
    if(object.equals("Unfinished Stone Axe")) {
      fill(100,100,135);
      ellipse(x+w/2,y+w/2,w/2,h/2);
    }
    if(object.equals("Stone Axe")) {
      fill(255,185,160);
      ellipse(x+w/2,y+w/2,w/2,h/2);
    }
    if(object.equals("Wooden Log")) {
      fill(255,185,160);
      rect(x,y+h/4,w,h/2);
    }
    if(object.equals("Wooden Wall")) {
      fill(220,175,145);
      rect(x,y,w,h);
    }
    if(object.equals("Pile of Wooden Logs")) {
      fill(200,155,125);
      rect(x,y+h/4,w,h/2);
      fill(255,185,160);
      rect(x+w/4,y,w/2,h);
    }
    if(object.equals("Pile of Stone")) {
      fill(125,125,165);
      ellipse(x+w/2,y+w/2,w/2,h/2);
    }
    if(object.equals("Camp Fire")) {
      fill(255,80,20);
      ellipse(x+w/2,y+w/2,w/2,h/2);
    }
  }
  
  //Define the setters and getters
  public void setX(float tempX) {
    x = tempX;
  }
  
  public void setY(float tempY) {
    y = tempY;
  }
  
  public void setWidth(float tempW) {
    w = tempW;
  }
  
  public void setHeight(float tempH) {
    h = tempH;
  }
  
  public void setBiome(String tempBiome) {
    biome = tempBiome;
    setWalkable();
  }
  
  public void setObject(String tempObject) {
    object = tempObject;
    setPickupable();
    setIsResource();
    setResource();
  }
  
  public void setLeader(boolean tempLeader) {
    leader = tempLeader;
  }
  
  public void setSearched(boolean tempSearched) {
    searched = tempSearched;
  }
  
  public void setWalkable() {
    if(biome.equals("Grassland"))
      walkable = true;
    if(biome.equals("Shallow Water"))
      walkable = true;
    if(biome.equals("Deep Water"))
      walkable = false;
    if(biome.equals("Forest"))
      walkable = true;
    if(biome.equals("Mangrove"))
      walkable = true;
    if(biome.equals("Beach"))
      walkable = true;
    if(biome.equals("Rocky Soil"))
      walkable = true;
    if(biome.equals("Savannah"))
      walkable = true;
    if(biome.equals("Snow"))
      walkable = true;
    if(biome.equals("Taiga"))
      walkable = true;
    if(biome.equals("Iceland"))
      walkable = true;
  }
  
  public void setPickupable() {
    if(object.equals("Small Stone")) {
      pickupable = true;
    }
    if(object.equals("Big Stone")) {
      pickupable = false;
    }
    if(object.equals("Tree")) {
      pickupable = true;
    }
    if(object.equals("Tree Branch")) {
      pickupable = true;
    }
    if(object.equals("Berry Bush")) {
      pickupable = true;
    }
    if(object.equals("Berry")) {
      pickupable = true;
    }
    if(object.equals("Pile of Tree Branches")) {
      pickupable = true;
    }
    if(object.equals("Starfish")) {
      pickupable = true;
    }
    if(object.equals("Shell")) {
      pickupable = true;
    }
    if(object.equals("Long Grass")) {
      pickupable = true;
    }
    if(object.equals("Ice Shard")) {
      pickupable = true;
    }
    if(object.equals("Crystal")) {
      pickupable = true;
    }
    
    //interactDictionary() objects
    
    if(object.equals("Unfinished Stone Axe")) {
      pickupable = true;
    }
    if(object.equals("Stone Axe")) {
      pickupable = true;
    }
    if(object.equals("Wooden Log")) {
      pickupable = true;
    }
    if(object.equals("Pile of Wooden Logs")) {
      pickupable = true;
    }
    if(object.equals("Wooden Wall")) {
      pickupable = false;
    }
    if(object.equals("Pile of Stone")) {
      pickupable = true;
    }
    if(object.equals("Camp Fire")) {
      pickupable = false;
    }
    
    if(object.equals("None")) {
      pickupable = false;
    }
  }
  
  public void setIsResource() {
    if(object.equals("Small Stone")) {
      isResource = true;
    }
    if(object.equals("Big Stone")) {
      isResource = true;
    }
    if(object.equals("Tree")) {
      isResource = false;
    }
    if(object.equals("Tree Branch")) {
      isResource = true;
    }
    if(object.equals("Berry Bush")) {
      isResource = false;
    }
    if(object.equals("Berry")) {
      isResource = true;
    }
    if(object.equals("Starfish")) {
      isResource = true;
    }
    if(object.equals("Shell")) {
      isResource = true;
    }
    if(object.equals("Long Grass")) {
      isResource = true;
    }
    if(object.equals("Ice Shard")) {
      isResource = true;
    }
    if(object.equals("Crystal")) {
      isResource = true;
    }
    
    //interactionDictionary() objects
    
    if(object.equals("Unfinished Stone Axe")) {
      isResource = true;
    }
    if(object.equals("Stone Axe")) {
      isResource = true;
    }
    if(object.equals("Wooden Log")) {
      isResource = true;
    }
    if(object.equals("Pile of Wooden Logs")) {
      isResource = true;
    }
    if(object.equals("Pile of Stone")) {
      isResource = true;
    }
    if(object.equals("Camp Fire")) {
      isResource = true;
    }
    
    if(object.equals("None")) {
      isResource = false;
    }
  }
  
  public void setResource() {
    if(object.equals("Tree")) {
      resource = "Tree Branch";
    }
    if(object.equals("Berry Bush")) {
      resource = "Berry";
    }
    if(object.equals("None")) {
      resource = "None";
    }
  }
  
  public float getX() {
    return x;
  }
  
  public float getY() {
    return y;
  }
  
  public float getWidth() {
    return w;
  }
  
  public float getHeight() {
    return h;
  }
  
  public String getBiome() {
    return biome;
  }
  
  public String getObject() {
    return object;
  }
  
  public boolean getLeader() {
    return leader;
  }
  
  public boolean getSearched() {
    return searched;
  }
  
  public boolean getWalkable() {
    return walkable;
  }
  
  public boolean getPickupable() {
      return pickupable;
  }
  
  public boolean getIsResource() {
      return isResource;
  }
  
  public String getResource() {
      return resource;
  }
  
  //Used to handle all object on object interactions
  public void interactDictionary() {
    
    //Unfinished Stone Axe
    if(playerInventory.equals("Small Stone")&&object.equals("Tree Branch")||
       playerInventory.equals("Tree Branch")&&object.equals("Small Stone")) {
         playerInventory = "None";
         object = "Unfinished Stone Axe";
         setPickupable();
         setIsResource();
         setResource();
         playerLog = "You started making a Stone Axe. You need something to tie the pieces together";
       }
       
    //Stone Axe
    else if(playerInventory.equals("Unfinished Stone Axe")&&object.equals("Long Grass")||
       playerInventory.equals("Long Grass")&&object.equals("Unfinished Stone Axe")) {
         playerInventory = "None";
         object = "Stone Axe";
         setPickupable();
         setIsResource();
         setResource();
         playerLog = "You made a Stone Axe";
       }
       
    //Wooden Log
    else if(playerInventory.equals("Stone Axe")&&object.equals("Tree")){
         playerInventory = "Stone Axe";
         object = "Wooden Log";
         setPickupable();
         setIsResource();
         setResource();
         playerLog = "You chopped the tree into a Wooden Log";
       }
       
    //Pile of Wooden Logs
    else if(playerInventory.equals("Wooden Log")&&object.equals("Wooden Log")||
            playerInventory.equals("Stone Axe")&&object.equals("Wooden Wall")){
         playerInventory = "None";
         object = "Pile of Wooden Logs";
         setPickupable();
         setIsResource();
         setResource();
         playerLog = "You made a Pile of Wooden Logs";
       }
       
    //Wooden Wall
    else if(playerInventory.equals("Stone Axe")&&object.equals("Pile of Wooden Logs")){
         playerInventory = "Stone Axe";
         object = "Wooden Wall";
         setPickupable();
         setIsResource();
         setResource();
         playerLog = "You made a Wooden Wall. Use the Stone Axe again to bring it down";
       }
       
    //Pile of Stone
    else if(playerInventory.equals("Small Stone")&&object.equals("Small Stone")) {
         playerInventory = "None";
         object = "Pile of Stone";
         setPickupable();
         setIsResource();
         setResource();
         playerLog = "You made a Pile of Stone. Add some wood to start a Camp Fire";
       }
       
    //Camp Fire
    else if(playerInventory.equals("Tree Branch")&&object.equals("Pile of Stone")||
            playerInventory.equals("Wooden Log")&&object.equals("Pile of Stone")) {
         playerInventory = "None";
         object = "Camp Fire";
         setPickupable();
         setIsResource();
         setResource();
         playerLog = "You made a Camp Fire";
       }
       
    else {
             playerLog = "You can't use a "+playerInventory+" on "+object;
       }
    
  }
  
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Delta_Wolf_Project" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
