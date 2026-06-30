package sample.common.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sample.common.dao.entity.Task;

@Mapper
public interface TaskMapper {
	// リスト取得用(Task編集時)
	List<Task> findAllTasks();	
	
		
	// 新規作成
	Long selectNextId();
	void insertTask(Task task);
		
	// ページネーション
	List<Task> selectPageByUser(@Param("username") String username,
								@Param("limit") int limit,
								@Param("offset") int offset);
	long countTotalByUser(@Param("username") String username);
	// idを取得
	Task findByIdAndUser (@Param("id") Long id, @Param("username") String username);
	// 更新
	int updateTaskByUser(Task task);
	// 削除
	int deleteTaskByUser(@Param("id") Long id, @Param("username") String username);

}
