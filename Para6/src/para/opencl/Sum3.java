package para.opencl;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLProgram;

import easycl.*;
import java.nio.*;
import java.io.IOException;
import java.io.*;
import java.util.*;

import static java.lang.System.*;
import static com.jogamp.opencl.CLMemory.Mem.*;

/** OpenCLを使用例を知るための簡単なデモ。
 * BufferAとBufferBとBufferCのそれぞれのi番目の要素を足してBufferDのi番目に格納する
 */
public class Sum3{
  /** OpenCLの初期化、データ初期化、演算、結果出力、OpenCL資源の開放をする
   */
  public Sum3(){
    //OpenCL演算環境の準備
    CLSetup cl = CLSetupCreator.createCLSetup();
    cl.initContext();





    cl.release();
  }

  public static void main(String[] argv){
    new Sum3();
  }
}
