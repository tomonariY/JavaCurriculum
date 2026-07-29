package sample.thymeleaf.web;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
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
	
	// ===== redirectToRoot =====
	@Test // 正常系
	void redirectToRoot_redirectsToLiveTasksPage() throws Exception {
		// when / then: "/" へのGETリクエストで "/tasks/live" へリダイレクトされることを確認
		mockMvc.perform(get("/"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/tasks/live"));
	}

	// ===== export =====
	@Test // 正常系
	void csvエクスポート_期間指定で正しくCSVがダウンロードされる() throws Exception {
		// given
		when(taskService.exportTasksByPeriod(
				eq("alice"), any(LocalDate.class), any(LocalDate.class)))
						.thenReturn(List.of());
		when(taskService.convertTasksToCsv(anyList()))
				.thenReturn("dummy csv content".getBytes(StandardCharsets.UTF_8));

		// when / then
		mockMvc.perform(post("/tasks/export")
				.session(session)
				.param("startDate", "2026-01-01")
				.param("endDate", "2026-01-31"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Type", "text/csv; charset=UTF-8"))
				.andExpect(header().string("Content-Disposition", "attachment; filename=\"tasks_export.csv\""))
				.andExpect(content().bytes("dummy csv content".getBytes(StandardCharsets.UTF_8)));

	}

	@Test // 異常系
	void csvエクスポート_日付未入力なら同じ画面にエラーが表示される() throws Exception {
		// given

		// when / then
		mockMvc.perform(post("/tasks/export")
				.session(session))
				.andExpect(status().isOk())
				.andExpect(view().name("tasks/export"))
				.andExpect(model().attribute("error", "出力条件を指定してください。"));

	}

	@Test // 異常系
	void csvエクスポート_0件ならBusinessExceptionが表示される() throws Exception {
		// given
		when(taskService.exportTasksByPeriod(
				eq("alice"), any(LocalDate.class), any(LocalDate.class)))
						.thenThrow(new BusinessException("出力件数は0件です。"));

		// when / then
		mockMvc.perform(post("/tasks/export")
				.session(session)
				.param("startDate", "2026-01-01")
				.param("endDate", "2026-01-31"))
				.andExpect(status().isOk())
				.andExpect(view().name("tasks/export"))
				.andExpect(model().attribute("error", "出力件数は0件です。"));

	}

}