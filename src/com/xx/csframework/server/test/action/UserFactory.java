package com.xx.csframework.server.test.action;

import java.util.Map;

import com.xx.csframework.server.test.model.UserModel;
import com.xx.users.Dao.UserCfgDao;

public class UserFactory {
	
	private static Map<String, UserModel> UserMap;
	
	public UserFactory() {
		 UserMap = UserCfgDao.getUsersMap();
	}
	
	
	boolean isUserCorrect(UserModel stu) {
		if(UserMap.containsKey(stu.getId())) {
			return true;
		}
		return false;
	}
	boolean isUserPasswordCorrect(UserModel stu) {
		UserModel tmp = UserMap.get(stu.getId());
		if(tmp.getpassword().equals(stu.getpassword())) {
			return true;
		}
		return false;
	}
	boolean isUserImageCorrect(UserModel stu) {
		UserModel tmp = UserMap.get(stu.getId());
		if(tmp.getImagecode().equals(stu.getImagecode())) {
			return true;
		}
		return false;
	}


	public String getNike(String id) {
		return UserMap.get(id).getNick();
	}
}
