package sample.common.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

	// ===== getTaskByPage =====
	@Test // 正常系
	void getTaskByPage_2ページ目はoffset10でMapperを呼ぶ() {
		// given: 25件 → 3ページ
		when(taskMapper.countTotalByUser("alice")).thenReturn(25L);

		// when
		taskService.getTaskByPage(2, "alice");

		// then: 2ページ目 → offset = (2-1) * 10 = 10
		verify(taskMapper).selectPageByUser("alice", 10, 10);
	}

	@Test // 境界値
	void getTaskByPage_1ページ目はoffset0でMapperを呼ぶ() {
		// given: 25件 → 3ページ
		when(taskMapper.countTotalByUser("alice")).thenReturn(25L);

		// when
		taskService.getTaskByPage(1, "alice");

		// then: 2ページ目 → offset = (1-1) * 10 = 0
		verify(taskMapper).selectPageByUser("alice", 10, 0);
	}

	@Test // 境界値
	void getTaskByPage_範囲外ページは最終ページに丸めてMapperを呼ぶ() {
		// given: 25件 → 3ページ。それを超える 99 ページを要求する
		when(taskMapper.countTotalByUser("alice")).thenReturn(25L);
		when(taskMapper.selectPageByUser("alice", 10, 20)).thenReturn(List.of());

		// when
		List<Task> actual = taskService.getTaskByPage(99, "alice");

		// then: 3ページ目に丸められ、offset = (3-1) * 10 = 20
		verify(taskMapper).selectPageByUser("alice", 10, 20);
		assertThat(actual).isEmpty();
	}

	// ===== getTotalPages =====
	@ParameterizedTest(name = "総件数{0}件 → {1}ページ")
	@CsvSource({
			"0,  1", // 0件でも最低1ページ(Math.max のガード)
			"1,  1",
			"10, 1", // ちょうど1ページ分
			"11, 2", // 1件あふれたら2ページ
			"20, 2",
			"21, 3"
	})
	void getTotalPages_件数からページ数を計算する(long total, int expected) {
		// given
		when(taskMapper.countTotalByUser("alice")).thenReturn(total);

		// when / then
		assertThat(taskService.getTotalPages("alice")).isEqualTo(expected);
	}

	// ===== clampPage =====
	@ParameterizedTest(name = "page={0} → {1}")
	@CsvSource({
			"-5, 1",
			"0,  1",
			"1,  1",
			"3,  3",
			"4,  3",
			"999, 3"
	})
	void clampPage_1からtotalPagesの範囲に丸められる(int page, int expected) {
		// given
		when(taskMapper.countTotalByUser("alice")).thenReturn(25L);

		// when / then
		assertThat(taskService.clampPage(page, "alice")).isEqualTo(expected);
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