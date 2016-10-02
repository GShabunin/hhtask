package hhtsks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainTsk2 {

    	//последовательности
		public byte[] SEQ,//исходная 
						NSEQ, //рабочая
						NSEQ0, //текущий кандидат для представления в качестве решения 
						RESSEQ; //текущее решение		
		//длины последовательностей. 
		//у последовательностей фиксированная физическая длина для простоты и скорости работы
		//нижеприведенные переменные дают отсечку по длине.
		public int N, NSEQLEN,  NSEQLEN0,  RESSEQLEN;

		//сравнить новый вариант с уже существующим
		//если новый вариант меньше, тогда TRUE
		public boolean compareSeq() {			
			if (RESSEQLEN == 0) return true;
			if (RESSEQLEN >= NSEQLEN0) {
			    if (RESSEQLEN > NSEQLEN0) return true;
			    for (int i = NSEQLEN0-1; i >=0; i--) {
			      if (RESSEQ[i] > NSEQ0[i])
			         return true; else
			      if (RESSEQ[i] < NSEQ0[i])
				     return false;
			    }
			}		
			return false;
		}			
		
		//записать в рабочую последовательность ряд цифр из исходной последовательности
		//по двум смещениям o1, o2.
		//SEQ
		//---o1--|----o2----|		
		//1234567|8910111213|.....
		//NSEQ
		//7654321|3121110198|	
		public void seqToNum(int o1, int o2) {
			byte v;
			NSEQLEN = (o1 + o2 + 1);
			NSEQLEN0 = NSEQLEN;
			for (int k = 1; k <= NSEQLEN; k++) {			 
			    if (k <= o2) v = SEQ[o1 + k]; else v = SEQ[k - o2 - 1];
			    NSEQ[NSEQLEN - k] = v;
			    NSEQ0[NSEQLEN - k] = v;
			}
		}

		//увеличит рабочую последовательность ++
		public void incSeq() {
			int k = 0;
			while (true) {
			  int v = NSEQ[k];
			  if (v == 9) {
			      NSEQ[k] = 0;
			      if (k == (NSEQLEN - 1)) {
			        NSEQ[NSEQLEN] = 1;
			        NSEQLEN++;
			        return;
			      }
			  } else {
			      NSEQ[k]++;
			      return;
			  }
			  k++;
			}
		}

		//проверить cnt цифр рабочей последовательности на совпадение
		//с цифрами в исходной, начиная с позиции p1.
		public boolean chkSeq(int p1, int cnt) {
			int C = p1 + cnt;
			if (N < C) C = N;			
			for (int i = p1; i < C; i++) {
				if (SEQ[i] != NSEQ[NSEQLEN - 1 - i + p1]) {			  
				  return false;
				}
			}
			return true;
		}
		
		public P256 Pos;

		//генерация результатов
		public void generateResult(int offs) {
			//запись результирующей последовательности			
			RESSEQLEN = NSEQLEN0;
			for (int i = 0; i < RESSEQLEN; i++) RESSEQ[i] = NSEQ0[i];

			//вычисление позиции в "бесконечном" множестве
			//используем для этого тип данных с макс длиной в 10^256
			P256 s;
			Pos = P256.SetNullP256();
			for (int i = 0; i < NSEQLEN0-1; i++) {			
			    s = P256.GenP256(9 * (i + 1));
			    Pos = P256.ADDP256(Pos, s, (byte)i);
			}
			s = P256.GenP256((NSEQ0[NSEQLEN0-1]-1) * (NSEQLEN0));
			Pos = P256.ADDP256(Pos, s, (byte) (NSEQLEN0-1));

			s = P256.SetNullP256();
			s.len = (byte) (NSEQLEN0-1);
			for (int i = 0; i < NSEQLEN0-1; i++) {			  
			    s.raw[i] = NSEQ0[i];
			}
			s = P256.MULP256(s, NSEQLEN0);
			Pos = P256.ADDP256(Pos, s, (byte)0);

			s = P256.GenP256(offs + 1);
			Pos = P256.ADDP256(Pos, s, (byte)0);
		}

		//пишем результаты на экран
		public void dropResult() {						
			String s = "";
			/*for (int i = RESSEQLEN-1; i >= 0; i--) {			  
			    s += Integer.toString(RESSEQ[i]);
			}

			System.out.println(s);

			s = "";*/
			for (int i = Pos.len-1; i >= 0; i--) {			  
			    s += Integer.toString(Pos.raw[i]);
			}
			System.out.println(s);
		}
		
	// получить набор цифр из строки
	public static byte[] bytesFromString(String s) {
		if ((s.length() > 50) || (s.length() == 0)) {
			return null;
		}
		byte[] v = new byte[s.length()];
		for (int i = 0; i < s.length(); i++) {
			if (Character.isDigit(s.charAt(i))) {
				v[i] = Byte.parseByte(s.substring(i, i+1));
			} else return null;
		}
		return v;
	}

	public static void main(String[] args) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	
		while (true) { //пока не введут пустое или неправильное множество
			String array = "";
			
			try {
				array = reader.readLine();	
			} catch (IOException e) { 
				e.printStackTrace();
				return;
			}
			
			int v1, snum;
		    int minval;
		    boolean ok, ok2;
		    	    
		    MainTsk2 Task2 = new MainTsk2();
	
		    Task2.SEQ = bytesFromString(array);
		    if (Task2.SEQ == null) {
		    	System.err.println("Invalid Array of Digits. Halt");
		    	return;
		    }
	
		    Task2.N = Task2.SEQ.length;
		    Task2.NSEQ = new byte[Task2.N + 1];
		    Task2.NSEQ0 = new byte[Task2.N + 1];
		    Task2.RESSEQ = new byte[Task2.N + 1];
		    Task2.RESSEQLEN = 0;
			v1 = 0;
			for (int c = 0; c < Task2.N; c++) v1 = v1 + Task2.SEQ[c];
			if (v1 == 0) {		  
				Task2.NSEQLEN = Task2.N + 1;
				Task2.NSEQLEN0 = Task2.NSEQLEN;
			    //заданы нули. результат = [1][множество].
			    for (int c = 0; c < Task2.N; c++) {		    
			    	Task2.NSEQ[c] = 0;
			    	Task2.NSEQ0[c] = 0;
			    }
			    Task2.NSEQ[Task2.NSEQLEN-1] = 1;
			    Task2.NSEQ0[Task2.NSEQLEN-1] = 1;
			    Task2.generateResult(1);
			    Task2.dropResult();
			    //return;
			} else {
				minval = 9;
				//перебор вариантов путем перестановок цифр во множестве.
			    for (int c = 1; c <= Task2.N; c++) {		    
			      ok2 = false;
			      for (int off1 = c - 1; off1 >= 0; off1--) {		      
			        int off2 = c - 1 - off1;
			        Task2.seqToNum(off1, off2);
			        v1 = Task2.NSEQ[Task2.NSEQLEN-1];
			        if (v1 == 0) {//выбран неверный вариант (с нулем в начале). игнорируем и идем дальше.		        
			          ok = false;
			      	} else {		        
			          snum = (Task2.N - off1 - 1) / c + 1;
			          ok = true;
			          for (int sn = 1; sn <= snum; sn++) {
			        	Task2.incSeq();
			            if (!Task2.chkSeq(off1 + (sn-1) * c + 1, c)) {
			              ok = false;
			              break;
			            }
			          }
			      	}
			        if (ok) {		        
			          if (minval >= v1) {
				          if (Task2.compareSeq()) {		          
				            minval = v1;
				            Task2.generateResult(off2);
				          }
			          }
			          ok2 = true;
			        }
			      }
			      if (ok2) {
			    	  Task2.dropResult();
			    	  break;
			      }
			    }
			}
		}
	}

}
