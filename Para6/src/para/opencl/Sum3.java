// 学籍番号：20B30100
// 氏名：伊藤悠馬
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


    FloatBuffer tmpfb;
    tmpfb = loadData("data/dataa.txt");
    CLBuffer<FloatBuffer> BufferA = cl.createBuffer(tmpfb,READ_ONLY);
    tmpfb.rewind();
    tmpfb = loadData("data/datab.txt");
    CLBuffer<FloatBuffer> BufferB = cl.createBuffer(tmpfb,READ_ONLY);
    tmpfb.rewind();
    tmpfb = loadData("data/datac.txt");
    CLBuffer<FloatBuffer> BufferC = cl.createBuffer(tmpfb,READ_ONLY);
    tmpfb.rewind();
    CLBuffer<FloatBuffer> BufferD = cl.createFloatBuffer(tmpfb.limit(),
							 WRITE_ONLY); 
    int datasize = tmpfb.limit();
    CLCommandQueue queue = cl.createQueue();

    CLProgram program = cl.createProgramFromResource(this,"sum3.cl");
    CLKernel kernel = program.createCLKernel("Sum");
    kernel.putArgs(BufferA, BufferB, BufferC, BufferD);
    kernel.setArg(4,datasize);

    BufferD.getBuffer().rewind();
    //デバイスへ転送、並列演算、演算結果の取得
    queue.putWriteBuffer(BufferA, false)//BufferAのデータをカーネル側へ転送指令
      .putWriteBuffer(BufferB, false)//BufferBのデータをカーネル側へ転送指令
      .putWriteBuffer(BufferC, false)//BufferBのデータをカーネル側へ転送指令
      .putBarrier() //今までの指令がすべて完了するまで待つ
      .put1DRangeKernel(kernel, 0, datasize, 1)//演算指令
      .putBarrier() //今までの指令がすべて完了するまで待つ
      .putReadBuffer(BufferD, true);//BufferDのデータをホスト側へ転送指令
                                    //転送完了まで待つ

    //演算結果の出力
    FloatBuffer fb = BufferD.getBuffer();
    fb.rewind();
    for(int i=0;i<fb.limit();i++){
      System.out.print(fb.get()+" ");
    }
    System.out.println();

    cl.release();
  }

  /** 引数で指定されたファイルから浮動小数の数値列を読み込み、その値を格納したバッファを返す。
   * {@link CLBuffer}に組み込むBufferは
   * @param fname 読み込み対象となるファイルの名
   * @return ダイレクトバッファとして領域確保した後、読み込んだ値を書き込んだ{@link FloatBuffer}
   */
  static public FloatBuffer loadData(String fname){
    ArrayList<Float> flt = new ArrayList<Float>();
    Scanner sc=null;
    try{
      sc= new Scanner(new File(fname));
    }catch(FileNotFoundException e){
      System.err.println(e);
      System.exit(1);
    }
    while(sc.hasNextFloat()){flt.add(sc.nextFloat());}
    FloatBuffer fb = 
      ByteBuffer.allocateDirect(flt.size()*java.lang.Float.SIZE/8) 
                 // java.lang.Float.SIZE はfloat型のbit数を意味する定数 
      .order(ByteOrder.nativeOrder())//float型を表すバイト順序にnativeを指定
      .asFloatBuffer();
    int i=0;
    while(fb.hasRemaining()){
      fb.put(flt.get(i));
      i++;
    }
    fb.rewind();
    return fb;
  } 

  public static void main(String[] argv){
    new Sum3();
  }
}
