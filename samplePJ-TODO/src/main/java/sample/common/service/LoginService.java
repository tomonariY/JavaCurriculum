package sample.common.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import sample.common.dao.entity.Login;
import sample.common.dao.mapper.LoginMapper;
import sample.common.logic.BusinessException;

@Service
public class LoginService {

	private final LoginMapper loginMapper;
	private final PasswordEncoder passwordEncoder;
	
    public LoginService(LoginMapper loginMapper, PasswordEncoder passwordEncoder) {
        this.loginMapper = loginMapper;
        this.passwordEncoder = passwordEncoder;
    }
	
	public Login loginForm(String username, String password) {
		
		Login user = loginMapper.findByUsername(username);
		if (user == null || !passwordEncoder.matches(password, user.getPass())) {
			return null;
		}
		
		return user;
	}
	
	
	public void registarNewUser(String username, String password) {
        if (loginMapper.findByUsername(username) != null) {
            throw new BusinessException("このユーザーは既に登録されています。");
        }
		
		Login user = new Login();		
		user.setUsername(username);
		user.setPass(passwordEncoder.encode(password));
		loginMapper.insertUser(user);		
	}
}
