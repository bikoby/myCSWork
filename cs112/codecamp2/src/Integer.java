
public class Integer implements Comparable{
	public int num;
	public Integer(int i){
		num=i;
	}


	public int compareTo(Object o) {
		int other=((Integer)o).num;
		return num-other;
	}

}
