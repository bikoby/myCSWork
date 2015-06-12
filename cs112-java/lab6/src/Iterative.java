import java.util.ArrayList;
import java.util.HashMap;


public class Iterative {
	
	private static HashMap<String, Integer> list;
	
	public Iterative(){
		list=new HashMap<String,Integer>();
	}

	public Word[] iterativeCounter(ArrayList<String> words){
		String word;
		//to go through all the words
		for (int i=0;i<words.size();i++){
			word=words.get(i);
			//to check whether the word is existing in the HashMap already
			if(list.containsKey(word)){
				//if yes, add the number with 1 and put it into HashMap again 
				list.put(word,list.get(word)+1);
			}else{
				//if not, put it in the HashMap with 1
				list.put(word,1);
			}
		}
		//to change the HaspMap into a Word list
		Word[] wordlist=new Word[list.size()];
		int j=0;
		for (String keys:list.keySet()){
			wordlist[j]=new Word(keys,list.get(keys));
			j++;
		}
		return wordlist;
	}

}
