package sample.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sample.common.dao.entity.Login;
import sample.common.dao.mapper.LoginMapper;

@Service
public class LoginService {

	@Autowired
	private LoginMapper loginMapper;
	
	public Login loginForm(String username, String password) throws Exception {
		
		Login loginUser = loginMapper.findUser(username, password);
		
		if (loginUser == null) {
			throw new Exception("このユーザーは登録されていません。");
		}
		
		return loginUser;
	}
	
	
	public void registarNewUser(String username, String password) throws Exception {
		
		Login registarUser = loginMapper.findByUsername(username);
		
		if (registarUser != null) {
			throw new Exception("このユーザーは既に登録されています。");
		}
		
		Login registar = new Login();	
		registar.setUsername(username);
		registar.setPass(password);
		
		loginMapper.insertUser(registar);		
	}
}
