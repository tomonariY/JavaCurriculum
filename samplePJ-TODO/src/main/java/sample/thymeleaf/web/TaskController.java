package sample.thymeleaf.web;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
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

	private final TaskService taskService;

	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}

	// 現在のログインユーザー
	private Login currentUser(HttpSession session) {
		Login user = (Login) session.getAttribute("loginUser");
		if (user == null) {
			throw new BusinessException("ログインが必要です。");
		}
		return user;
	}

	// ホーム画面(Task) API実装
	@RequestMapping(value = "/tasks/live", method = RequestMethod.GET)
	public ModelAndView showApiTaskList(ModelAndView mv) {
		mv.setViewName("tasks/live");
		return mv;
	}

	// 新規追加
	@RequestMapping(value = "/tasks/new", method = RequestMethod.GET)
	public ModelAndView newApiTaskList(
			@RequestParam(value = "from", required = false, defaultValue = "tasks") String from,
			ModelAndView mv) {

		mv.addObject("from", from);
		mv.setViewName("tasks/live-new");
		return mv;
	}

	// 編集＆更新
	@RequestMapping(value = "/tasks/edit/{id}", method = RequestMethod.GET)
	public ModelAndView editApiTaskList(
			@PathVariable("id") Long id,
			@RequestParam(value = "from", required = false, defaultValue = "tasks") String from,
			HttpSession session, ModelAndView mv) {

		mv.addObject("id", id);
		mv.addObject("from", from);
		mv.setViewName("tasks/live-edit");
		return mv;
	}

	// 削除
	@RequestMapping(value = "/tasks/delete/{id}")
	public String deleteTaskList(
			@PathVariable("id") Long id,
			HttpSession session) {

		Login user = currentUser(session);
		taskService.deleteTask(id, user.getUsername());
		return "redirect:/tasks";
	}

	// CSVエクスポート - 入力画面表示
	@RequestMapping(value = "/tasks/export", method = RequestMethod.GET)
	public ModelAndView showExport(ModelAndView mv) {
		mv.setViewName("tasks/live-export");
		return mv;
	}

	@RequestMapping(value = "/tasks/export", method = RequestMethod.POST)
	public ModelAndView export(
			@RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
			@RequestParam(value = "from", required = false, defaultValue = "tasks") String from,
			HttpSession session,
			HttpServletResponse response,
			ModelAndView mv) throws IOException {

		Login user = currentUser(session);

		try {
			if (startDate == null || endDate == null) {
				throw new BusinessException("出力条件を指定してください。");
			}
			List<Task> tasks = taskService.exportTasksByPeriod(user.getUsername(), startDate, endDate);

			byte[] csv = taskService.convertTasksToCsv(tasks);
			response.setContentType("text/csv; charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=\"tasks_export.csv\"");
			response.getOutputStream().write(csv);
			response.getOutputStream().flush();
			return null;

		} catch (BusinessException e) {
			mv.addObject("error", e.getMessage());
			mv.addObject("startDate", startDate);
			mv.addObject("endDate", endDate);
			mv.addObject("from", from);
			mv.setViewName("tasks/export");
			return mv;
		}

	}

	// === 旧画面(Web API経由でないもの) == //
	@RequestMapping(value = "/tasks", method = RequestMethod.GET)
	public ModelAndView showTaskList(
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "from", required = false) String from,
			HttpSession session,
			ModelAndView mv) {

		if ("live".equals(from)) {
			mv.setViewName("redirect:/tasks/live");
			return mv;
		}

		Login user = currentUser(session);
		int safePage = taskService.clampPage(page, user.getUsername());
		List<Task> taskList = taskService.getTaskByPage(safePage, user.getUsername());
		int totalPages = taskService.getTotalPages(user.getUsername());

		mv.addObject("taskList", taskList);
		mv.addObject("currentPage", safePage);
		mv.addObject("totalPages", totalPages);
		mv.setViewName("tasks/tasks");

		return mv;
	}

		@RequestMapping(value = "/tasks/new", method = RequestMethod.POST)
	public String insertTaskList(
			@ModelAttribute("taskForm") Task task,
			@RequestParam(value = "from", required = false, defaultValue = "tasks") String from,
			HttpSession session) {

		Login user = currentUser(session);
		task.setUsername(user.getUsername());
		taskService.insertTask(task, user.getUsername());

		if ("live".equals(from)) {
			return "redirect:/tasks/live";
		}

		return "redirect:/tasks";
	}

	@RequestMapping(value = "/tasks/edit", method = RequestMethod.POST)
	public String updateTaskList(
			@ModelAttribute("taskForm") Task task,
			@RequestParam(value = "from", required = false, defaultValue = "tasks") String from,
			HttpSession session) {

		Login user = currentUser(session);
		taskService.updateTask(task, user.getUsername());

		if ("live".equals(from)) {
			return "redirect:/tasks/live";
		}

		return "redirect:/tasks";
	}
}
