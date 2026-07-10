package sample.common.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sample.common.dao.entity.Task;

@Mapper
public interface TaskMapper {
	
		
	// 新規作成のため
	void insertTask(Task task);
		
	// ページネーションのため
	List<Task> selectPageByUser(@Param("username") String username,
								@Param("limit") int limit,
								@Param("offset") int offset);
	long countTotalByUser(@Param("username") String username);
	// id取得のため
	Task findByIdAndUser(@Param("id") Long id, @Param("username") String username);
	// 更新のため
	int updateTaskByUser(Task task);
	// 削除のため
	int deleteTaskByUser(@Param("id") Long id, @Param("username") String username);

}
