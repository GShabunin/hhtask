package hhtsks;

public class P256 {
	//класс для работы с "бесконечными" цифрами
	//кодирует до 10^256 значений
	public final static int MAX_P256_SIZE = 256;	
	
	public byte len;
	public byte[] raw;
	
	public P256() {
		raw = new byte[MAX_P256_SIZE];
	}
	
	//создать дубликат
	public static P256 dublicateP256(P256 a) {
		P256 res = new P256();
		res.len = a.len;
		for (int i = 0; i < MAX_P256_SIZE; i++) {
			res.raw[i] = a.raw[i];
		}
		return res;
	}
	
	//создать пустое множество
	public static P256 SetNullP256() {
		P256 result = new P256();
		for (int i = 0; i < MAX_P256_SIZE; i++) {
			result.raw[i] = 0;
		}
		result.len = 0;
		return result;
	}

	//генерация числа из целого int v
	public static P256 GenP256(int v) {	
		P256 result = SetNullP256();

	    while (v > 0) {
		   result.raw[result.len] = (byte)(v % 10);
		   v = v / 10;
		   result.len++;
	    }
	    return result;
	}

	//сумировать два "бесконечных" числа со смещением в off разрядов 
	public static P256 ADDP256(P256 a, P256 b, byte off) {	
		byte up, k, v;
		k = off;
		up = 0;
		P256 result = dublicateP256(a);
		while (true) {		  
		   v = (byte) (result.raw[k] + b.raw[k - off] + up);
		   if (v > 9) up = 1; else up = 0;
		   result.raw[k] = (byte) (v % 10);
		   k++;
		   if (((k - off) >= b.len) && (up == 0)) {		   
		       if (k > result.len) result.len = k;
		       return result;
		   }	
		}
	}
	
	//умножить на число
	public static P256 MULP256(P256 a, int m) {
		P256 result;
		if (m == 0) {
			result = SetNullP256();
			return result;
		}
		result = dublicateP256(a);
		for (int i = 1; i < m; i++) {	  
			result = ADDP256(result, a, (byte)0);
		}
		return result;
	}	
}
