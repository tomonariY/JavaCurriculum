package sample.thymeleaf.web;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import sample.common.dao.entity.Login;
import sample.common.dao.entity.Task;
import sample.common.logic.BusinessException;
import sample.common.service.TaskService;

@Controller
public class TaskController {
	@Autowired
	private TaskService taskService;
	
	// ホーム画面(Task)

	@RequestMapping(value = "/tasks", method = RequestMethod.GET)

	public ModelAndView showTaskList(
	@RequestParam(value = "page", defaultValue = "1") int page,
	HttpSession session,
	ModelAndView mv) {
		
	Login user = currentUser(session);
	List<Task> taskList = taskService.getTaskByPage(page, user.getUsername());

	int totalPages =taskService.getTotalPages(user.getUsername());

	mv.addObject("taskList",taskList);
	mv.addObject("currentPage", page);
	mv.addObject("totalPages", totalPages);
	mv.setViewName("tasks/tasks");

	return mv;

	} 


	
	// 新規追加
	@RequestMapping(value = "/tasks/new", method = RequestMethod.GET)
	public ModelAndView newTaskList(ModelAndView mv) {
		
		Task newTask = new Task();
		Long nextId = taskService.nextTaskId();
		
		newTask.setId(nextId);
		
		mv.addObject("taskForm", newTask);
		mv.setViewName("tasks/form-new");
		return mv;
	}
	
	@RequestMapping(value = "/tasks/new", method = RequestMethod.POST)
	public String insertTaskList(@ModelAttribute("taskForm") Task task, HttpSession session) {
		
		Login user = currentUser(session);
		task.setUsername(user.getUsername());
		taskService.insertTask(task);
		
		return "redirect:/tasks";
	}
	
	// 編集＆更新
	@RequestMapping(value = "/tasks/edit/{id}", method = RequestMethod.GET)
	public ModelAndView editTaskList(@PathVariable("id") Long id, HttpSession session, ModelAndView mv) {
		
		Login user = currentUser(session);
		Task targetTask = taskService.getTaskById(id, user.getUsername());
		
		mv.addObject("taskForm", targetTask);
		mv.setViewName("tasks/form-edit");
		return mv;
	}
	
	@RequestMapping(value = "/tasks/edit", method =  RequestMethod.POST)
	public String updataTaskList(@ModelAttribute("taskForm") Task task, HttpSession session) {
		
		Login user = currentUser(session);
		taskService.updateTask(task, user.getUsername());
		
		return "redirect:/tasks";
	}
	
	// 削除
	@RequestMapping(value = "/tasks/delete/{id}")
	public String deleteTaskList(@PathVariable("id") Long id, HttpSession session) {
		Login user = currentUser(session);
		taskService.deleteTask(id,user.getUsername());
		return "redirect:/tasks";
	}
	
	private Login currentUser(HttpSession session) {
		Login user = (Login) session.getAttribute("loginUser");
		if (user == null) {
			throw new BusinessException("ログインが必要です。");
		}
		return user;
	}
}
