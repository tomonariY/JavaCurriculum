package sample.common.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sample.common.dao.entity.Login;

@Mapper
public interface LoginMapper{
	
	// リスト取得用(ログイン時)
	List<Login> findAllLogins();
	
	
	 // ログイン用(ユーザー、パスワード)
	Login findUser(
			@Param("username") String username,
			@Param("password") String password
			);
	
	Login findByUsername (
			@Param("username") String username
			);
	
	// ユーザー登録用
	void insertUser(Login login);
	
}