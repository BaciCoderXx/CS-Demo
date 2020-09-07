package com.xx.csframework.server.test.action;

import com.xx.csframework.annotation.Actioner;
import com.xx.csframework.annotation.MecAction;
import com.xx.csframework.annotation.Para;
import com.xx.csframework.server.test.model.UserModel;

@MecAction
public class UserAction {
	
	public UserAction() {
	}
	
	@Actioner(action="userLogin")
	public UserModel userLogin(
			@Para(name="id") String id, 
			@Para(name="password") String password,
			@Para(name="imagecode") String imagecode) {
		UserModel stu = new UserModel();
		stu.setId(id);
		stu.setpassword(password);
		stu.setImagecode(imagecode);
		UserFactory users = new UserFactory();
		if(!users.isUserCorrect(stu) && !users.isUserImageCorrect(stu) && !users.isUserPasswordCorrect(stu)) {
			return new UserModel().setId("ERROR");
		}
//		Thread.sleep(5000);
		stu.setNick(users.getNike(id));
		return new UserModel().setId(id).setNick(stu.getNick());
	}
}
