package sample.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sample.common.dao.entity.Login;
import sample.common.dao.mapper.LoginMapper;
import sample.common.logic.BusinessException;

@Service
public class LoginService {

	@Autowired
	private LoginMapper loginMapper;
	
	@Transactional(readOnly = true)
	public Login loginForm(String username, String password) throws Exception {
		
		Login loginUser = loginMapper.findUser(username, password);
		
		if (loginUser == null) {
			throw new Exception("このユーザーは登録されていません。");
		}
		
		return loginUser;
	}
	
	
	@Transactional
	public void registarNewUser(String username, String password) {	
		if (loginMapper.findByUsername(username) != null) {
			throw new BusinessException("このユーザーは既に登録されています。");
		}
		
		Login registar = new Login();	
		registar.setUsername(username);
		registar.setPass(password);
		
		loginMapper.insertUser(registar);		
	}
}
