package com.xx.csframework.server.test.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDatabase {
	private static Connection connection;
	private static UserDatabase me;
	
	private UserDatabase() {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/users_info?serverTimezone=GMT-8",
					"root", "xyj990802");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static UserDatabase newInstance() {
		if(me == null) {
			return me = new UserDatabase();
		}else return me;
	}
	
	public ResultSet executeQuery(String sqlString) {//实现对数据库内容的查找
		try {
			PreparedStatement state = connection.prepareStatement(sqlString);
			return state.executeQuery(sqlString);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int executeUpdate(String sqlString) {//实现对数据库中内容的添加
		try {
			PreparedStatement state = connection.prepareStatement(sqlString);
			return state.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
}
