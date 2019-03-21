//called on the very start of the program
void setup(){
  size(500, 500);
  frameRate(60);
  
  noStroke();
  
  createMap();
}

int nTileSizeX = 1;
int nTileSizeY = 1;

int nTileWidth = 500;
int nTileHeight = 500;

float fNoiseScale = 0.038;
int nOctives = 5;
float fPersistance = 5;
float fLacunarity = 0.163;

ArrayList<ArrayList<tile>> tiles = new ArrayList<ArrayList<tile>>();

void createMap(){
  float fHighestVal = 1;
  
  noiseSeed(9542352);
  
  for(int y = 0; y < nTileHeight; y++){
    ArrayList<tile> tempx = new ArrayList<tile>();
    
    for(int x = 0; x < nTileWidth; x++){
      float fNoise = getNoise(x, y, fNoiseScale, nOctives, fPersistance, fLacunarity);
      
      if(fNoise > fHighestVal){
        fHighestVal = fNoise;
      }
      
      float c = fNoise;
      tempx.add(new tile(x * nTileSizeX, y * nTileSizeY, nTileSizeX, nTileSizeY, c));
    }
    
    tiles.add(tempx);
  }
  
  print(fHighestVal);
  
  for(ArrayList<tile> y : tiles){
   for(tile x : y){
     //println(red(x.c));
     x.bc = int(map(x.bc, 0, fHighestVal, 0, 255));
   }
  }
}

//called every frame of the program
void draw(){
  background(200);
  
  for(ArrayList<tile> y : tiles){
   for(tile x : y){
     x.drawSelf();
   }
  }
}
