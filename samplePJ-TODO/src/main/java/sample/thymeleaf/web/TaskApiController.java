package sample.thymeleaf.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sample.common.dao.entity.Login;
import sample.common.dao.entity.Task;
import sample.common.logic.BusinessException;
import sample.common.logic.UnauthorizedException;
import sample.common.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskApiController {

	private final TaskService taskService;

	public TaskApiController(TaskService taskService) {
		this.taskService = taskService;
	}

	@GetMapping
	public ResponseEntity<Map<String, Object>> getTasks(
			@RequestParam(value = "page", defaultValue = "1") int page,
			HttpSession session) {

		
		try {
			Login user = currentUser(session);
		
			int safePage = taskService.clampPage(page, user.getUsername());
			List<Task> tasks = taskService.getTaskByPage(safePage, user.getUsername());
			int totalPages = taskService.getTotalPages(user.getUsername());

			Map<String, Object> result = new LinkedHashMap<>();
			result.put("tasks", tasks);
			result.put("currentPage", safePage);
			result.put("totalPages", totalPages);
			return ResponseEntity.ok(result);
			
		} catch (UnauthorizedException e) {
			return ResponseEntity.status(401).build();

		} catch (BusinessException e) {
			return ResponseEntity.notFound().build();

		}
		

	}

	private Login currentUser(HttpSession session) {
		Login user = (Login) session.getAttribute("loginUser");
		if (user == null) {
			throw new UnauthorizedException("ログインが必要です。");
		}
		return user;
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id, HttpSession session) {
		try {
			Login user = currentUser(session);
			taskService.deleteTask(id, user.getUsername());
			return ResponseEntity.noContent().build();
			
		} catch (UnauthorizedException e) {
			return ResponseEntity.status(401).build();

		} catch (BusinessException e) {
			return ResponseEntity.notFound().build();
			
		}
	}
}