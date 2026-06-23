package sample.common.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sample.common.dao.entity.Task;

@Mapper
public interface TaskMapper {
	// リスト取得用(Task編集時)
	List<Task> findAllTasks();	
	
	// idを取得
	Task findById (@Param("id") Long id);
		
	// 新規作成
	Long selectNextId();
	void insertTask(Task task);
	
	// 更新
	void updateTask(Task task);
	// 削除
	void deleteTask(Long id);
	
	// ページネーション
	List<Task> selectPage(
				@Param("limit") int limit,
				@Param("offset") int offset
			);
	long countTotal();
}
