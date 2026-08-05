package sample.common.dao.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sample.common.dao.entity.Task;
import sample.common.dto.TaskUpdateRequest;

@Mapper
public interface TaskMapper {

	// 新規作成のため
	void insertTask(Task task);

	// ページネーションのため
	List<Task> selectPageByUser(
			@Param("username") String username,
			@Param("limit") int limit,
			@Param("offset") int offset);

	long countTotalByUser(@Param("username") String username);

	// id取得のため
	Task findByIdAndUser(@Param("id") Long id, @Param("username") String username);

	// 更新のため
	int updateTaskByUser(
			@Param("id") Long id,
			@Param("request") TaskUpdateRequest request,
			@Param("username") String username);

	// == 旧 更新(Mapper) 使用しなくなる予定 == //
	int updateTaskOld(Task task);
	// ====================================== //

	// 削除のため
	int deleteTaskByUser(@Param("id") Long id, @Param("username") String username);

	// CSV出力のため
	List<Task> selectTasksForExportByPeriod(
			@Param("username") String username,
			@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	Task selectTaskForExportById(@Param("username") String username,
			@Param("id") Long id);
}
