package usflix;

import java.util.HashMap;

public class UserDatabase {

	public static final int minPwdLength=6;
	private HashMap<String,User> userDB;
	
	public UserDatabase(){
		userDB= new HashMap<String, User>();
		
	}
	
	public User createAccount(String f,String l,String u,String p){
		//if the password contains username or the length of the password less than minPwdLength or
		//the username is already exist
		if (p.contains(u)||p.length()<minPwdLength||userDB.get(u)!=null){
			return null;
		}else{
			//if not
			User user=new User(f,l,u,p);
			userDB.put(u,user);
			return user;
		}
	}
		
	public User login(String u,String p){
			//if the userDB contains the username and the password is the same
			if(userDB.containsKey(u)&&p.equals(userDB.get(u).getPassWord())){
				return userDB.get(u);
			}else{
				return null;
			}
		
	}
	
	public boolean isAvailable(String u){
		if (userDB.containsKey(u)){
			return false;
		}else{
			return true;
		}
	}
	
	public HashMap<String,User> getUserDB(){
		return userDB;
	}
	
	
}
