package sample.thymeleaf.web;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sample.common.dao.entity.Login;
import sample.common.dao.entity.Task;
import sample.common.dto.TaskUpdateRequest;
import sample.common.logic.BusinessException;
import sample.common.logic.UnauthorizedException;
import sample.common.logic.ValidationException;
import sample.common.service.TaskService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/tasks")
public class TaskApiController {

	private final TaskService taskService;

	public TaskApiController(TaskService taskService) {
		this.taskService = taskService;
	}

	private Login currentUser(HttpSession session) {
		Login user = (Login) session.getAttribute("loginUser");
		if (user == null) {
			throw new UnauthorizedException("ログインが必要です。");
		}
		return user;
	}

	// Task一覧とページネーションの表示
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

	// Task CSV出力用メソッド
	@GetMapping("/export")
	public ResponseEntity<Map<String, Object>> getTaskByCsv(
			@RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
			HttpSession session) {
				
		try {
			Login user = currentUser(session);
			if (startDate == null || endDate == null) {
				throw new ValidationException("出力条件を指定してください。");
			}

			List<Task> tasks = taskService.exportTasksByPeriodApi(user.getUsername(), startDate, endDate);

			Map<String, Object> result = new LinkedHashMap<>();
			result.put("tasks", tasks);
			return ResponseEntity.ok(result);

		} catch (ValidationException e) {
			return ResponseEntity.status(400).build();
		} catch (UnauthorizedException e) {
			return ResponseEntity.status(401).build();
		} catch (BusinessException e) {
			return ResponseEntity.notFound().build();
		}
	}

	// Task一覧から指定のIDを1件取得し表示
	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getTaskById(
			@PathVariable("id") Long id, HttpSession session) {
		try {
			Login user = currentUser(session);
			Task targetTask = taskService.getTaskById(id, user.getUsername());

			Map<String, Object> result = new LinkedHashMap<>();
			result.put("task", targetTask);
			return ResponseEntity.ok(result);

		} catch (UnauthorizedException e) {
			return ResponseEntity.status(401).build();

		} catch (BusinessException e) {
			return ResponseEntity.notFound().build();

		}
	}

	// Taskの更新用メソッド
	@PutMapping("/{id}")
	public ResponseEntity<Void> updateTask(
			@PathVariable("id") Long id,
			@RequestBody TaskUpdateRequest request,
			HttpSession session) {

		try {
			Login user = currentUser(session);
			taskService.updateTask(id, request, user.getUsername());
			;
			return ResponseEntity.ok().build();

		} catch (UnauthorizedException e) {
			return ResponseEntity.status(401).build();

		} catch (BusinessException e) {
			return ResponseEntity.notFound().build();
		}
	}

	// Taskの削除用メソッド
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