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
  void display() {
    
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
      if(biome.equals("Snow")||biome.equals("Taiga")) {
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
  void setX(float tempX) {
    x = tempX;
  }
  
  void setY(float tempY) {
    y = tempY;
  }
  
  void setWidth(float tempW) {
    w = tempW;
  }
  
  void setHeight(float tempH) {
    h = tempH;
  }
  
  void setBiome(String tempBiome) {
    biome = tempBiome;
    setWalkable();
  }
  
  void setObject(String tempObject) {
    object = tempObject;
    setPickupable();
    setIsResource();
    setResource();
  }
  
  void setLeader(boolean tempLeader) {
    leader = tempLeader;
  }
  
  void setSearched(boolean tempSearched) {
    searched = tempSearched;
  }
  
  void setWalkable() {
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
  
  void setPickupable() {
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
  
  void setIsResource() {
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
  
  void setResource() {
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
  
  float getX() {
    return x;
  }
  
  float getY() {
    return y;
  }
  
  float getWidth() {
    return w;
  }
  
  float getHeight() {
    return h;
  }
  
  String getBiome() {
    return biome;
  }
  
  String getObject() {
    return object;
  }
  
  boolean getLeader() {
    return leader;
  }
  
  boolean getSearched() {
    return searched;
  }
  
  boolean getWalkable() {
    return walkable;
  }
  
  boolean getPickupable() {
      return pickupable;
  }
  
  boolean getIsResource() {
      return isResource;
  }
  
  String getResource() {
      return resource;
  }
  
  //Used to handle all object on object interactions
  void interactDictionary() {
    
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