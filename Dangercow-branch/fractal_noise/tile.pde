class tile {
  //position vector
  int x;
  int y;
  int w;
  int h;
  color c;
  float bc;
  
  tile(int x, int y, int w, int h, float bc){
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.bc = bc;
  }
  
  void drawSelf(){
    c = color(bc);
    if (bc <= 110){
      c = color(0, 0, 150); 
    }
    else if (bc <= 125){
     c = color(0, 0, 255); 
    }
    else if (bc <= 135){
     c = color(255, 255, 0); 
    }
    else if (bc <= 170){
     c = color(0, 255, 0); 
    }
    else if (bc <= 220){
      c = color(185);
    }
    else {
     c = color(255, 255, 255); 
    }
    
    fill(c);
    rect(x, y, w, h);
    fill(255, 255, 255);
  }
}
