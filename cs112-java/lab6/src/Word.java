
public class Word implements Comparable {
	
	private int numOfWords;
	private String word;
	
	public Word(String word, int numOfWord){
		this.numOfWords=numOfWord;
		this.word=word;
	}


	public int compareTo(Object other) {
	      int othernum = ((Word)other).getNum();
	      String otherword = ((Word)other).getWord();
	      if (numOfWords==othernum){
	    	  //if the numbers are the same, compare the words
	         return word.compareTo(otherword);
	      }
	      else{
		      //to compare the numbers of words if the numbers are different
	         return othernum-numOfWords;
	      }
		
	}
	
	public String toString(){
		return numOfWords+" "+word;
	}
	
	public String getWord(){
		return word;
	}
	
	public int getNum(){
		return numOfWords;
	}


}
