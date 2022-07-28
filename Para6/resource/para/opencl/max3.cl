// student_num:20B30100
// name:Yuma Ito
#pragma OPENCL EXTENSION cl_khr_byte_addressable_store : enable

__kernel void Max(__constant float* a, __constant float* b, __constant float* c,
                  __global float* d, int numElements){
  // get index into global data array
  int i = get_global_id(0);
  
  if(numElements<i) return;
  
  d[i] = max(a[i], max(b[i], c[i]));
}

