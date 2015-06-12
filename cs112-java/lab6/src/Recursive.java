import java.util.ArrayList;
import java.util.HashMap;


public class Recursive {
	
	private static HashMap<String, Integer> list;
		
		public Recursive(){
			list=new HashMap<String,Integer>();
		}
		
	public Word[] recursiveCounter(ArrayList<String> words){
		//to add words from index 0
		HashMap<String, Integer> newlist=rCI(words,0);
		//to change the HaspMap into Word list
		Word[] wordlist=new Word[newlist.size()];
		int j=0;
		for (String keys:newlist.keySet()){
			wordlist[j]=new Word(keys,newlist.get(keys));
			j++;
		}
		
		return wordlist;
	}
	
	public HashMap<String,Integer> rCI(ArrayList<String> words,int Start){
		if(Start==words.size()){
			return list;
		}else{
			String word;
			word=words.get(Start);
			//to check whether the word is existing in the HashMap
			if(list.containsKey(word)){
				list.put(word,list.get(word)+1);
			}else{
				list.put(word,1);
			}
			return rCI(words,Start+1);
		}
	}
}
