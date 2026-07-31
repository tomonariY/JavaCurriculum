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
import sample.common.dao.entity.Task;
import sample.common.logic.BusinessException;
import sample.common.service.TaskService;

@WebMvcTest(TaskApiController.class)
public class TaskApiControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private TaskService taskService;
	
	private MockHttpSession session;
	
	@BeforeEach
	void setUp() {
		Login loginUser = new Login();
		loginUser.setUsername("alice");
		
		session = new MockHttpSession();
		session.setAttribute("loginUser", loginUser);
	}
	
	@Test // 正常系
	void getTasks_returnsTaskListWithPageInfo() throws Exception {
		// given: taskServiceが1件のタスクとページ情報を返すよう設定
		Task task = new Task();
		task.setId(1L);
		task.setUsername("alice");
		task.setTitle("テストタスク");
		
		when(taskService.clampPage(1, "alice")).thenReturn(1);
		when(taskService.getTaskByPage(1, "alice")).thenReturn(List.of(task));
		when(taskService.getTotalPages("alice")).thenReturn(2);
		
		// when / then: JSONの中身(currentPage, totalPages, tasks)を検証する
		mockMvc.perform(get("/api/tasks")
						.session(session)
						.param("page", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.currentPage").value(1))
				.andExpect(jsonPath("$.totalPages").value(2))
				.andExpect(jsonPath("$.tasks[0].title").value("テストタスク"));
				
	}
	
	@Test
	void getTasks_redirectsToLoginWhenNotLoggedIn() throws Exception {
	    // given: セッションにloginUserを設定しない（未ログイン状態）
		MockHttpSession emptySession = new MockHttpSession();
		
	    // when / then: LoginInterceptorによって/loginへリダイレクトされる
		mockMvc.perform(get("/api/tasks")
						.session(emptySession))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));

	}

	@Test // 正常系
	void deleteTask_deletesOwnTaskSuccessfully() throws Exception {
		// given: taskService.deleteTaskが正常に動作するよう設定
		doNothing().when(taskService).deleteTask(1L, "alice");
		
		// when / then: DELETEリクエストを送信し、204 OKを期待する
		mockMvc.perform(delete("/api/tasks/1")
						.session(session))
				.andExpect(status().is2xxSuccessful());
		
		// verify: taskService.deleteTaskが正しい引数で呼ばれたことを検証
		verify(taskService).deleteTask(1L, "alice");
	}

	@Test // 異常系
	void deleteTask_failsWhenDeletingOthersTask() throws Exception {
		// given: taskService.deleteTaskがBusinessExceptionをスローするよう設定
		doThrow(new BusinessException("削除対象のタスクが見つかりません。"))
				.when(taskService).deleteTask(2L, "alice");
		
		// when / then: DELETEリクエストを送信し、500 Internal Server Errorを期待する
		mockMvc.perform(delete("/api/tasks/2")
						.session(session))
				.andExpect(status().isNotFound());
		
		// verify: taskService.deleteTaskが正しい引数で呼ばれたことを検証
		verify(taskService).deleteTask(2L, "alice");
	}
}
