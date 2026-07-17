package sample.thymeleaf.web;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import sample.common.dao.entity.Login;
import sample.common.logic.BusinessException;
import sample.common.service.TaskService;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TaskService taskService;

	private MockHttpSession session;

	@BeforeEach
	void セットアップ() {
		Login loginUser = new Login();
		loginUser.setUsername("alice");

		session = new MockHttpSession();
		session.setAttribute("loginUser", loginUser);
	}

	@Test // 正常系
	void tasks一覧_ログイン済みなら一覧が表示される() throws Exception {
		// TaskServiceは偽物なので、何を返すか仕込んでおく
		when(taskService.getTaskByPage(1, "alice")).thenReturn(List.of());
		when(taskService.getTotalPages("alice")).thenReturn(1);

		// when / then
		mockMvc.perform(get("/tasks").session(session))
				.andExpect(status().isOk());
	}

	@Test // 異常系
	void tasks一覧_ログインしていなければ何らかの応答が返る() throws Exception {
		// when / then
		mockMvc.perform(get("/tasks"))
				.andExpect(status().is3xxRedirection());
	}

	@Test // 異常系
	void タスク編集_存在しないタスクなら例外が処理される() throws Exception {
		// taskServiceが呼ばれたら、BusinessExceptionを投げるように仕込む
		when(taskService.getTaskById(99L, "alice"))
				.thenThrow(new BusinessException("対象のタスクが見つかりません。"));

		// when / then
		mockMvc.perform(get("/tasks/edit/99").session(session))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/tasks"))
				.andExpect(flash().attribute("error", "対象のタスクが見つかりません。"));
	}

}