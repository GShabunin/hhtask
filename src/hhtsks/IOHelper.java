package hhtsks;

import java.io.BufferedReader;
import java.io.IOException;

public class IOHelper {

	public static byte readByte(BufferedReader reader) {
		try {
			String s = reader.readLine();
			try{
	            byte b = Byte.parseByte(s);
	            return b;
	        }catch(NumberFormatException nfe){
	            System.err.println("Invalid Byte");
	        }				
		} catch (IOException e) { 
			e.printStackTrace();
		}	        
		return 0;
	}

	public static int[] readInt(BufferedReader reader, int cnt, int amin, int amax) {
		int[] res = new int[cnt];
		String[] els;
		try {
			String s = reader.readLine();
			els = s.split(" ");
			if (els.length < cnt) {
				System.err.println("Invalid Num of parameters");
				return null;
			}
			try{
	            for (int i = 0; i < cnt; i++) {
	            	res[i] = Integer.parseInt(els[i]);
	            	if (res[i] < amin || res[i] > amax) {
	            		System.err.println("Wrong number");
	            		return null;
	            	}
	            }
	            return res;
	        }catch(NumberFormatException nfe){
	            System.err.println("Invalid Integer");
	        }				
		} catch (IOException e) { 
			e.printStackTrace();
		}	        
		return null;
	}

}
