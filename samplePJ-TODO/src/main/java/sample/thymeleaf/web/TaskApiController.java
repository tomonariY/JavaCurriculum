package sample.thymeleaf.web;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sample.common.dao.entity.Login;
import sample.common.dao.entity.Task;
import sample.common.logic.BusinessException;
import sample.common.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskApiController {
	
	private final TaskService taskService;
	
	public TaskApiController(TaskService taskService) {
		this.taskService = taskService;
	}
	
	@GetMapping
	public List<Task> getTasks(HttpSession session) {
		
		Login user = currentUser(session);
		return taskService.getTaskByPage(1, user.getUsername());
	}
	
	private Login currentUser(HttpSession session) {
		Login user = (Login) session.getAttribute("loginUser");
		if (user == null) {
			throw new BusinessException("ログインが必要です。");
		}
		return user;
	}
}
