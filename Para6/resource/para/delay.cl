// student_num:20B30100
// name:Yuma Ito
#pragma OPENCL EXTENSION cl_khr_byte_addressable_store : enable

__kernel void Delay(const int width, const int height,
         __global uchar* latest, __global uchar* previous,
         __global uchar* output, const float p){

  int x = get_global_id(0);
  int y = get_global_id(1);

  int addr3 = (y*width+x)*3;
  int addr4 = (y*width+x)*4;
  output[addr4] = latest[addr3]*p+previous[addr3]*(1-p);
  output[addr4+1] = latest[addr3+1]*p+previous[addr3+1]*(1-p);
  output[addr4+2] = latest[addr3+2]*p+previous[addr3+2]*(1-p);
  output[addr4+3] = 0xff;
  
  previous[addr3] = output[addr4];
  previous[addr3+1] = output[addr4+1];
  previous[addr3+2] = output[addr4+2];
  








}

