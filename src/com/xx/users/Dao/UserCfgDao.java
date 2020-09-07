package com.xx.users.Dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.xx.csframework.server.test.database.UserDatabase;
import com.xx.csframework.server.test.model.UserModel;

public class UserCfgDao {
	
	public UserCfgDao() {
	}
	
	public static Map<String, UserModel> getUsersMap(){
		Map<String, UserModel> UserMap = new HashMap<>();
		String SQLstring = "SELECT id,nike,password,imagecode FROM user_info";
		try {
			ResultSet rs = UserDatabase.newInstance().executeQuery(SQLstring);
			while(rs.next()) {
				String id = rs.getString("id");
				String nike = rs.getString("nike");
				String password = rs.getString("password");
				String imagecode = rs.getString("imagecode");
				
				UserMap.put(id,new UserModel(id, password, nike, imagecode));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return UserMap;
	}
}
