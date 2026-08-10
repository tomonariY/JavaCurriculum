package sample.common.dao.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sample.common.dao.entity.Task;
import sample.common.dto.TaskRequest;

@Mapper
public interface TaskMapper {

	// id取得のため
	Task findByIdAndUser(
			@Param("id") Long id,
			@Param("username") String username);

	// 新規作成のため
	int insertTaskByUser(
			@Param("request") TaskRequest request,
			@Param("username") String username);

	// 更新のため
	int updateTaskByUser(
			@Param("id") Long id,
			@Param("request") TaskRequest request,
			@Param("username") String username);

	// 削除のため
	int deleteTaskByUser(
			@Param("id") Long id,
			@Param("username") String username);

	// CSV出力のため
	List<Task> selectTasksForExportByPeriod(
			@Param("username") String username,
			@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	Task selectTaskForExportById(
			@Param("username") String username,
			@Param("id") Long id);

	// ページネーションのため
	List<Task> selectPageByUser(
			@Param("username") String username,
			@Param("limit") int limit,
			@Param("offset") int offset);

	long countTotalByUser(@Param("username") String username);

	// == 旧 Mapper 使用しなくなる予定 == //
	int updateTask(Task task);

	void insertTask(Task task);
}
