package sample.common.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sample.common.dao.entity.Task;
import sample.common.dao.mapper.TaskMapper;
import sample.common.logic.BusinessException;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

	@Mock
	private TaskMapper taskMapper;

	@InjectMocks
	private TaskService taskService;

	// ===== getTaskById =====	
	@Test // 正常系
	void getTaskById_対象が有れば本人のタスクを返す() {
		// given
		Task task = new Task();
		task.setUsername("alice");
		when(taskMapper.findByIdAndUser(1L, "alice")).thenReturn(task);

		// when
		Task actual = taskService.getTaskById(1L, "alice");

		// then
		assertThat(actual).isSameAs(task);
	}

	@Test // 異常系
	void getTaskById_対象が無ければBusinessExceptionを投げる() {
		// given: 該当ユーザーのタスクが見つからない状況
		when(taskMapper.findByIdAndUser(99L, "alice")).thenReturn(null);

		// when / then
		assertThatThrownBy(() -> taskService.getTaskById(99L, "alice"))
				.isInstanceOf(BusinessException.class)
				.hasMessage("対象のタスクが見つかりません。");
	}

	@Test // 境界値
	void getTotalPages_件数からページ数を切り上げ最低1を返す() {
		// given: 11 件 → 10 件/ページなので 2 ページ
		when(taskMapper.countTotalByUser("alice")).thenReturn(11L);

		// when / then
		assertThat(taskService.getTotalPages("alice")).isEqualTo(2);
	}

	// ===== updateApiTask =====	
	

	// ===== insertTask =====	
	@Test // 正常系
	void insertTask_引数のusernameがtaskにセットされて保存される() {
		// given
		Task task = new Task();
		task.setUsername("dummy");

		// when
		taskService.insertTask(task, "alice");

		// then
		assertThat(task.getUsername()).isEqualTo("alice");
		verify(taskMapper).insertTask(task);
	}

	// ===== deleteTask =====	
	@Test // 正常系
	void deleteTask_削除できれば例外を投げない() {
		// given
		when(taskMapper.deleteTaskByUser(1L, "alice")).thenReturn(1);

		// when / then
		assertThatCode(() -> taskService.deleteTask(1L, "alice"))
				.doesNotThrowAnyException();
	}

	@Test // 異常系
	void deleteTask_削除件数が0ならBusinessExceptionを投げる() {
		// given
		when(taskMapper.deleteTaskByUser(99L, "alice")).thenReturn(0);

		// when / then
		assertThatThrownBy(() -> taskService.deleteTask(99L, "alice"))
				.isInstanceOf(BusinessException.class)
				.hasMessage("削除対象のタスクが見つかりません。");
	}

	// ===== clampPage =====	
	@Test // 境界値
	void clampPage_0以下なら1に補正される() {
		// given: 総件数25件 → 3ページ想定(PAGE_SIZE=10の場合)
		when(taskMapper.countTotalByUser("alice")).thenReturn(25L);

		// when
		int actual = taskService.clampPage(0, "alice");

		// then
		assertThat(actual).isEqualTo(1);
	}

	@Test // 境界値
	void clampPage_範囲外を大きく下回るなら1に補正される() {
		// given: 総件数25件 → 3ページ想定
		when(taskMapper.countTotalByUser("alice")).thenReturn(25L);

		// when
		int actual = taskService.clampPage(-5, "alice");

		// then
		assertThat(actual).isEqualTo(1);
	}

	@Test // 境界値
	void clampPage_範囲内の最小値ならそのまま表示される() {
		// given: 総件数25件 → 3ページ想定
		when(taskMapper.countTotalByUser("alice")).thenReturn(25L);

		// when
		int actual = taskService.clampPage(1, "alice");

		// then
		assertThat(actual).isEqualTo(1);
	}

	@Test // 境界値
	void clampPage_範囲内の最大値ならそのまま表示される() {
		// given: 総件数25件 → 3ページ想定
		when(taskMapper.countTotalByUser("alice")).thenReturn(25L);

		// when
		int actual = taskService.clampPage(3, "alice");

		// then
		assertThat(actual).isEqualTo(3);
	}

	@Test // 境界値
	void clampPage_範囲外を超えたら最大ページに補正される() {
		// given: 総件数25件 → 3ページ想定
		when(taskMapper.countTotalByUser("alice")).thenReturn(25L);

		// when
		int actual = taskService.clampPage(4, "alice");

		// then
		assertThat(actual).isEqualTo(3);
	}

	// ===== exportTasksByPeriod =====
	@Test // 正常系
	void exportTasksByPeriod_正常な期間ならタスク一覧を返す() {
		// given
		Task task = new Task();
		task.setUsername("alice");
		List<Task> tasks = List.of(task);
		when(taskMapper.selectTasksForExportByPeriod(
				"alice", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31)))
						.thenReturn(tasks);

		// when
		List<Task> actual = taskService.exportTasksByPeriod("alice", LocalDate.of(2026, 1, 1),
				LocalDate.of(2026, 1, 31));

		// then
		assertThat(actual).isSameAs(tasks);

	}

	@Test // 異常系
	void exportTasksByPeriod_開始日が終了日より後ならBusinessExceptionを投げる() {
		// given: startDate > endDate

		// when / then
		assertThatThrownBy(() -> taskService.exportTasksByPeriod(
				"alice", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1)))
						.isInstanceOf(BusinessException.class)
						.hasMessage("不正な日付範囲です。");

	}

	@Test // 異常系
	void exportTasksByPeriod_0件ならBusinessExceptionを投げる() {
		// given:
		when(taskMapper.selectTasksForExportByPeriod(
				"alice", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31)))
						.thenReturn(List.of());

		// when / then
		assertThatThrownBy(() -> taskService.exportTasksByPeriod(
				"alice", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31)))
						.isInstanceOf(BusinessException.class)
						.hasMessage("出力件数は0件です。");

	}

	// ===== exportTaskById =====
	@Test // 正常系
	void exportTaskById_対象が有れば本人のタスクを返す() {
		// given
		Task task = new Task();
		task.setUsername("alice");
		when(taskMapper.selectTaskForExportById("alice", 1L)).thenReturn(task);

		// when
		Task actual = taskService.exportTaskById(1L, "alice");

		// then
		assertThat(actual).isSameAs(task);

	}

	@Test // 異常系
	void exportTaskById_対象が無ければBusinessExceptionを投げる() {
		// given:
		when(taskMapper.selectTaskForExportById("alice", 999L)).thenReturn(null);

		// when / then
		assertThatThrownBy(() -> taskService.exportTaskById(999L, "alice"))
				.isInstanceOf(BusinessException.class)
				.hasMessage("対象のタスクが見つかりません。");

	}

	// ===== convertTasksToCsv =====
	@Test // 正常系
	void convertTasksToCsv_ヘッダーとデータが正しくCSVに変換される() {
		// given
		Task task = new Task();
		task.setId(1L);
		task.setUsername("alice");
		task.setTitle("タスクA");
		task.setContent("内容A");
		task.setName("登録者A");
		task.setStartDate(LocalDate.of(2026, 1, 1));
		task.setEndDate(LocalDate.of(2026, 1, 31));
		List<Task> tasks = List.of(task);

		// when
		byte[] result = taskService.convertTasksToCsv(tasks);
		String csv = new String(result, StandardCharsets.UTF_8);

		// then: ヘッダー行と、登録いた内容がCSVにふくまれていること
		assertThat(csv).contains("\"ID\",\"ユーザー名\",\"タイトル\"");
		assertThat(csv).contains("\"1\",\"alice\",\"タスクA\",\"内容A\",\"登録者A\",\"2026-01-01\",\"2026-01-31\"");

	}
}