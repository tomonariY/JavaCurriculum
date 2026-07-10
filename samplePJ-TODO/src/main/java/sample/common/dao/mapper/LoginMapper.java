package sample.common.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sample.common.dao.entity.Login;

@Mapper
public interface LoginMapper{
	
	 // ログインのため username を取得する
	Login findByUsername (
			@Param("username") String username
			);
	
	// ユーザー登録のため
	void insertUser(Login login);
	
}