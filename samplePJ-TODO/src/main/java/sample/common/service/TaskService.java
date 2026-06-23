package sample.common.service;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sample.common.dao.entity.Login;
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
	
	public void insertTask(HttpSession session, Task task) {
		java.util.Enumeration<String> attributeNames = session.getAttributeNames();
		
		while (attributeNames.hasMoreElements()) {
			String key = attributeNames.nextElement();
			Object value = session.getAttribute(key);
			
			if (value instanceof Login) {
				Login loginUser = (Login) value;
				task.setUsername(loginUser.getUsername());
				
				break;
			}
			
		}
		
		if (task.getUsername() == null) {
			task.setUsername("unknown_user");
		}
		
		taskMapper.insertTask(task);
	}
	
	// 編集＆更新
	public Task getTaskById(Long id) {
		return taskMapper.findById(id);
	}
	
	public void updateTask(Task task) {
		taskMapper.updateTask(task);
	}
	
	// 削除
	public void deleteTask(Long id) {
		taskMapper.deleteTask(id);
	}
	
	// ページネーション
	public List<Task> getTaskByPage(int page) {
		int limit = 10;
		int offset = (page - 1) * limit;
		return taskMapper.selectPage(limit, offset);
	}
	
	public int getTotalPages() {
		long totalTasks = taskMapper.countTotal(); // データベースの総件数
		int limit = 10;
		
		int totalPages = (int) ((totalTasks + limit - 1) / limit);
		
		if (totalPages == 0) {
			totalPages = 1;
		}
		
		return totalPages;
	}

}
