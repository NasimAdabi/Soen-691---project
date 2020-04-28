package tutorial.example;

import java.io.*;
class ThrowExample { 
  void myMethod(int num)throws IOException, ClassNotFoundException{ 
     if(num==1)
        throw new IOException("IOException Occurred");
     else
        throw new ClassNotFoundException("ClassNotFoundException");
  } 
} 

public class kitchen{ 
  public static void main(String args[]){ 
	  int i=0;
	  while(i<1) {
   try{ 
     ThrowExample obj=new ThrowExample(); 
     obj.myMethod(1); 
   }catch(Exception ex){
     System.out.println(ex);
    } 
  }
	  i++;
  }
}