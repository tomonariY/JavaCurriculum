package sample.thymeleaf.web;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import sample.common.dao.entity.Login;
import sample.common.logic.BusinessException;
import sample.common.service.LoginService;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private LoginService loginService;

	// ===== showLoginForm =====
	@Test // 正常系
	void showLoginForm_ログイン画面が表示される() throws Exception {
		// when / then
		mockMvc.perform(get("/login"))
				.andExpect(status().isOk());

	}

	// ===== loginForm =====
	@Test // 正常系
	void loginForm_認証成功でタスク一覧へリダイレクトされる() throws Exception {
		// given
		Login alice = new Login();
		alice.setUsername("alice");

		when(loginService.loginForm("alice", "正しいパスワード")).thenReturn(alice);

		// when / then
		mockMvc.perform(post("/login")
				.param("username", "alice")
				.param("password", "正しいパスワード"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/tasks"));

	}

	@Test // 異常系
	void loginForm_認証失敗でログイン画面にエラー表示される() throws Exception {
		// given
		when(loginService.loginForm("alice", "間違ったパスワード")).thenReturn(null);

		// when / then
		mockMvc.perform(post("/login")
				.param("username", "alice")
				.param("password", "間違ったパスワード"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("error", "ユーザー名またはパスワードが間違っています。"));
	}

	// ===== showRegisterForm =====	
	@Test // 正常系
	void showRegisterForm_新規登録画面が表示される() throws Exception {
		// when / then
		mockMvc.perform(get("/register"))
				.andExpect(status().isOk());
	}

	// ===== registarUser =====	
	@Test // 正常系
	void registerUser_新規登録が成功するとログイン画面へリダイレクトされる() throws Exception {
		// given: 何も設定しない(voidメソッドは、デフォルトで正常に終わる)

		// when / then
		mockMvc.perform(post("/register")
				.param("username", "newuser")
				.param("password", "任意のパスワード"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test // 異常系
	void registerUser_登録済みユーザー名だと登録画面にエラー表示される() throws Exception {
		// given
		doThrow(new BusinessException("このユーザーは既に登録されています。"))
				.when(loginService).registerNewUser("alice", "任意のパスワード");

		// when / then
		mockMvc.perform(post("/register")
				.param("username", "alice")
				.param("password", "任意のパスワード"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("error", "このユーザーは既に登録されています。"));

	}
}
