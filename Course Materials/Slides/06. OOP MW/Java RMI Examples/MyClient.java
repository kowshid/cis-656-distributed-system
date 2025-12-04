import java.rmi.*;
import java.io.*;

public class MyClient{

	public static void main(String args[]){
		try{
			Method stub=(Method)Naming.lookup("rmi://localhost:5000/lab6");  
			System.out.println(stub.action(3,4));

		}catch(Exception e){System.out.println(e);}
	}
}
