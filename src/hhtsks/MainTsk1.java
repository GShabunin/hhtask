package hhtsks;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainTsk1 {
	
	public class Cell {
		//класс-ячейка с высотой
		
		public Cell[] nb; 
		public char hght, hght0, maxhght;
		public boolean chked;
		
		public Cell(char ahght) {
			nb = new Cell[4];
			sethght(ahght);
			reset();
			maxhght = 1001;			
		}		
		
		public void sethght(char ahght) {
			hght = ahght;
			hght0 = ahght;			
		}
		
		public void setAsSea() {
			nb = null;
		}
		
		public int calcVolume() {
			return hght - hght0;
		}		
		
		public void reset() {
			chked = false;
		}
		
		//определит может ли ячейка задержать воду на высоте h
		public boolean drop(char h) {			
			if (nb == null) return false; //ячейка-море. поток воды ушел в него.	
			chked = true;	//для предотвращения повторной проверки и бесконечных рекурсий		
			if (h <= hght) return true;		
			if (h > maxhght) return false;
			for (byte i = 0; i < nb.length; i++) {//прокладываем путь воде
				if (!nb[i].chked) {
					if (!nb[i].drop(h)) {
						//вода ушла в море
						if (maxhght > h) maxhght = h;//запишем в ячейку максимальную высоту воды, 
													//чтобы больше ее не проверять в циклах
						return false; 
					}
				}
			}
			return true;
		}
	}	
	
	public class Island {
		//класс-остров
		public Cell sea;
		public Cell[] cells;		
		public byte M, N;
		public char max;
		
		public Island(Cell asea, byte aM, byte aN) {
			M = aM;
			N = aN;			
			sea = asea;
			generateCells((char)1);
		}		
		
		public void generateCells(char low) {
			max = low;
			cells = new Cell[M * N];
			for (char i = 0; i < cells.length; i++) cells[i] = new Cell(low);				
			int c = 0;
			for (byte i = 0; i < N; i++) {
				for (byte j = 0; j < M; j++ ) {
					//east
					if (i == 0)     cells[c].nb[0] = sea; else cells[c].nb[0] = cells[(i - 1) * M + j]; 
					//west
					if (i == N - 1) cells[c].nb[1] = sea; else cells[c].nb[1] = cells[(i + 1) * M + j]; 
					//north
					if (j == 0)     cells[c].nb[2] = sea; else cells[c].nb[2] = cells[i * M + j - 1];
					//south
					if (j == M - 1) cells[c].nb[3] = sea; else cells[c].nb[3] = cells[i * M + j + 1];
					c++;
				}
			}			
		}
		
		public void reset() {
			for (char j = 0; j < cells.length; j++) {
				cells[j].reset();				
			}			
		}
		
		//капнуть во все ячейки h воды
		public void drop(char h) {
			if (h > max) return;						
			for (char i = 0; i < cells.length; i++) {
				if ((cells[i].hght < h) && (cells[i].maxhght > h)) {				
					reset();		
					if (cells[i].drop(h)) {					
						for (char j = 0; j < (char)cells.length; j++) {
							if (cells[j].chked) {
								if (cells[j].hght < h) {
									if (cells[j].maxhght > h) {
										cells[j].hght = h;	
									}
								}
							}
						}					
					}
				}
			}			
		}
		
		//определить объем накопленной воды на острове
		public int calcVolume() {
			int res = 0;
			for (char i = 0; i < cells.length; i++) {
				res += cells[i].calcVolume();				
			}
			return res;
		}

		//определим макс высоту острова
		public void prepare() {
			for (char i = 0; i < cells.length; i++) {
				if (max < cells[i].hght) max = cells[i].hght;				
			}			
		}		
		
	}
	
	public class CalcThread extends Thread {
		//рабочий поток для подсчета острова
		
		private Island subj;
		
		public CalcThread(Island asubj) {
			subj = asubj;
		}
		
	    public void run() {
			for (char h = 2; h <= subj.max; h++) {
				subj.drop(h);			
			}
	    }
	    
	}
	
	public static void main(String[] args) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		MainTsk1 Task1 = new MainTsk1();
		Cell sea = Task1.new Cell((char)1);
		sea.setAsSea();
				
		byte IN = 1;
		
		IN = IOHelper.readByte(reader);
		if (IN == 0) {
			return;
		}
				
		Island[] M = new Island[IN];
		
		for (int inum = 0; inum < IN; inum++) {			
			int[] v = IOHelper.readInt(reader, 2, 1, 50);
			if (v == null) return;
			
			M[inum] = Task1.new Island(sea, (byte)v[0], (byte)v[1]);
			
			for (int r = 0; r < v[0]; r++) {
				int[] row = IOHelper.readInt(reader, v[1], 1, 1000);
				if (row == null)
					return;
				else {
					for (int c = 0; c < v[1]; c++) {
						M[inum].cells[c * v[0] + r].sethght((char)row[c]);							
					}
				}
			}
			
			M[inum].prepare();
		}
		
		final byte MAX_NUM_OF_THREADS = 4; //макс одновременно работающих потоков 
		
		int tOffset = 0;
		while (tOffset < IN) {
			int tCnt = IN - tOffset;
			if (tCnt > MAX_NUM_OF_THREADS) tCnt = MAX_NUM_OF_THREADS;
			CalcThread threads[] = new CalcThread[tCnt];
			for (byte i = 0; i < tCnt; i++) {
				threads[i] = Task1.new CalcThread(M[tOffset + i]);
				threads[i].start();
			}
			
			//ждем потоки
			while (true) {
				boolean b = true;
				for (byte i = 0; i < tCnt; i++) {
					if (threads[i].isAlive()) b = false;
				}		
				if (b) break;
			}
			
			tOffset += MAX_NUM_OF_THREADS;
		}
		
		//вывод результатов
		System.out.println();
		
		for (byte i = 0; i < IN; i++) {
			System.out.println(M[i].calcVolume());		
		}			
	}

}
