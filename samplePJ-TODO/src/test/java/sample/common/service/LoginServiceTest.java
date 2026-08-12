package sample.common.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import sample.common.dao.entity.Login;
import sample.common.dao.mapper.LoginMapper;
import sample.common.logic.BusinessException;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
	
	@Mock
	private LoginMapper loginMapper;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@InjectMocks
	private LoginService loginService;
	
	private Login aliceを用意する(String hashPassword) {
		Login alice = new Login();
		alice.setUsername("alice");
		alice.setPassword(hashPassword);
		when(loginMapper.findByUsername("alice")).thenReturn(alice);
		return alice;
	}
	

	// ===== loginForm =====	
	@Test // 正常系
	void loginForm_ユーザー名とパスワードが正しければLoginを返す() {
		// given
		Login alice = aliceを用意する("ハッシュ化されたパスワード");
		when(passwordEncoder.matches("入力パスワード", "ハッシュ化されたパスワード")).thenReturn(true);
		
		// when
		Login result = loginService.loginForm("alice", "入力パスワード");
		
		// then
		assertThat(result).isSameAs(alice);
	}

	@Test // 異常系
	void loginForm_ユーザーが存在しなければnullを返す() {
		// given
		when(loginMapper.findByUsername("unknown")).thenReturn(null);
		
		// when
		Login result = loginService.loginForm("unknown", "任意のパスワード");
		
		// then
		assertThat(result).isNull();
	}
	
	@Test // 異常系
	void loginForm_パスワードが誤っていればnullを返す() {
		// given
		aliceを用意する("ハッシュ化されたパスワード");
		when(passwordEncoder.matches("間違ったパスワード", "ハッシュ化されたパスワード")).thenReturn(false);
		
		// when
		Login result = loginService.loginForm("alice", "間違ったパスワード");
		
		// then
		assertThat(result).isNull();
	}
	
	// ===== registarNewUser =====	
	@Test // 正常系
	void registarNewUser_ユーザー名とパスワードが正しければLoginを返す() {
		// given
		when(loginMapper.findByUsername("newuser")).thenReturn(null);
		when(passwordEncoder.encode("平文")).thenReturn("ハッシュ値");
		
		// when
		assertThatCode(() -> loginService.registerNewUser("newuser", "平文"))
				.doesNotThrowAnyException();
		
		// then
		verify(loginMapper).insertUser(any(Login.class));
	}

	@Test // 正常系
	void registerNewUser_パスワードはハッシュ化されてから保存される() {
		// given
		when(loginMapper.findByUsername("newuser")).thenReturn(null);
		when(passwordEncoder.encode("平文")).thenReturn("ハッシュ値");

		// when
		loginService.registerNewUser("newuser", "平文");

		// then: insertUser に「何が」渡ったのかを捕まえて中身を確認する
		ArgumentCaptor<Login> captor = ArgumentCaptor.forClass(Login.class);
		verify(loginMapper).insertUser(captor.capture());

		Login saved = captor.getValue();
		assertThat(saved.getUsername()).isEqualTo("newuser");
		assertThat(saved.getPassword())
				.as("平文パスワードがそのまま保存されてはいけない")
				.isEqualTo("ハッシュ値")
				.isNotEqualTo("平文");

		// encode が実際に呼ばれたことも押さえる
		verify(passwordEncoder).encode("平文");
	}

	@Test // 異常系
	void registarNewUser_登録済みのユーザー名ならBusinessExceptionを投げる() {
		// given
		aliceを用意する("任意のパスワード");
		
		// when / then
		assertThatThrownBy(() -> loginService.registerNewUser("alice", "任意の新しいパスワード"))
				.isInstanceOfAny(BusinessException.class)
				.hasMessage("このユーザーは既に登録されています。");
				
	}
	
}
