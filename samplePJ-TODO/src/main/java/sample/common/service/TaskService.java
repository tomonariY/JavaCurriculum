package sample.common.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sample.common.dao.entity.Task;
import sample.common.dao.mapper.TaskMapper;

@Service
public class TaskService {
	
	private final TaskMapper taskMapper;
	
	public TaskService(TaskMapper taskMapper) {
		this.taskMapper = taskMapper;
	}
	
	// 新規追加
	@Transactional
	public void insertTask(Task task, String username) {
		task.setUsername(username);
		taskMapper.insertTask(task);
	}
	
	// 編集 ＆ 更新
	@Transactional
	public Task getTaskById(Long id,String username) {
		Task task = taskMapper.findByIdAndUser(id, username);
		if (task == null) {
			throw new IllegalStateException("対象のタスクが見つかりません。");
		}
		return task;
	}
	
	@Transactional
	public void updateTask(Task task, String username) {
		task.setUsername(username);
		int updated = taskMapper.updateTaskByUser(task);
		if (updated == 0) {
			throw new IllegalStateException("更新対象のタスクが見つかりません。");
		}
	}
	
	// 削除
	@Transactional
	public void deleteTask(Long id, String username) {
		taskMapper.deleteTaskByUser(id, username);
	}
	
	// ページネーション
	private static final int PAGE_SIZE = 10;
	
	@Transactional(readOnly = true)
	public List<Task> getTaskByPage(int page, String username) {
		int totalPages = getTotalPages(username);
		int safePage = Math.max(1, Math.min(page, totalPages));
		int offset = (safePage - 1) * PAGE_SIZE;
		return taskMapper.selectPageByUser(username, PAGE_SIZE, offset);
	}
	
	public int getTotalPages(String username) {
		long total = taskMapper.countTotalByUser(username); // データベースの総件数
		int pages = (int) ((total + PAGE_SIZE -1) / PAGE_SIZE);		
		return Math.max(pages, 1);
	}

}
