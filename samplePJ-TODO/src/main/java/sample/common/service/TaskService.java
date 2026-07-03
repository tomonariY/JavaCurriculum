package sample.common.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sample.common.dao.entity.Task;
import sample.common.dao.mapper.TaskMapper;

@Service
public class TaskService {
	@Autowired
	private	TaskMapper taskMapper;
	
	// 新規追加
	public Long nextTaskId() {
		return taskMapper.selectNextId();
	}
	
	public void insertTask(Task task) {	
		taskMapper.insertTask(task);
	}
	
	// 編集＆更新
	public Task getTaskById(Long id, String username) {
		Task task = taskMapper.findByIdAndUser(id, username);
		if (task == null) {
			throw new IllegalStateException("対象のタスクが見つかりません。");
		}
		return task;
	}
	
	public void updateTask(Task task, String username) {
		task.setUsername(username);
		int updated = taskMapper.updateTaskByUser(task);
		if (updated == 0) {
			throw new IllegalStateException("更新対象のタスクが見つかりません。");
		}
	}
	
	// 削除
	public void deleteTask(Long id, String username) {
		int deleted = taskMapper.deleteTaskByUser(id, username);
		if (deleted == 0) {
			throw new IllegalStateException("削除対象のタスクが見つかりません。");
		}
	}
	
	// ページネーション
	public List<Task> getTaskByPage(int page, String username) {
		int limit = 10;
		int offset = (page - 1) * limit;
		return taskMapper.selectPageByUser(username, limit, offset);
	}
	
	public int getTotalPages(String username) {
		long totalTasks = taskMapper.countTotalByUser(username); // データベースの総件数
		int limit = 10;
		
		int totalPages = (int) ((totalTasks + limit - 1) / limit);
		
		if (totalPages == 0) {
			totalPages = 1;
		}
		
		return totalPages;
	}

}
