package sample.common.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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

	
    // ===== updateTask =====	
	@Test // 正常系
	void updateTask_更新できれば例外を投げない() {
		// given
		Task task = new Task();
		task.setId(1L);
		when(taskMapper.updateTaskByUser(task)).thenReturn(1);
		
		// when / then
		assertThatCode(() -> taskService.updateTask(task, "alice"))
				.doesNotThrowAnyException();
	}
	
	@Test // 異常系
	void updateTask_更新件数が0ならBusinessExceptionを投げる() {
		// given
		Task task = new Task();
		task.setId(99L);
		when(taskMapper.updateTaskByUser(task)).thenReturn(0);
		
		// when / then
		assertThatThrownBy(() -> taskService.updateTask(task, "alice"))
				.isInstanceOf(BusinessException.class)
				.hasMessage("更新対象のタスクが見つかりません。");
	}
	
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
	

}
