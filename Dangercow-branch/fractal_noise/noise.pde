float getNoise(int x, int y, float scale, int octaves, float persistance, float lacunarity){
  float fHeight = 0;
  
  float fAmp = 1;
  float fFeq = 1;
  
  for(int i = 0 ; i < octaves; i++){
    float fSampleX = (x / scale) * fFeq;
    float fSampleY = (y / scale) * fFeq;
    
    float fPerlin = noise(fSampleX, fSampleY);
    fHeight += fPerlin * fAmp;
    
    fAmp *= persistance;
    fFeq *= lacunarity;
  }
  
  return fHeight;
}
