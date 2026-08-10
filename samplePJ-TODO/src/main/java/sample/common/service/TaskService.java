package sample.common.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opencsv.CSVWriter;

import sample.common.dao.entity.Task;
import sample.common.dao.mapper.TaskMapper;
import sample.common.dto.TaskUpdateRequest;
import sample.common.logic.BusinessException;
import sample.common.logic.ValidationException;

@Service
public class TaskService {

	private final TaskMapper taskMapper;

	public TaskService(TaskMapper taskMapper) {
		this.taskMapper = taskMapper;
	}

	// 新規追加
	@Transactional
	public void insertApiTask(TaskUpdateRequest request, String username) {
		taskMapper.insertTaskByUser(request, username);
	}

	// 1件タスク情報を取得する
	@Transactional(readOnly = true)
	public Task getTaskById(Long id, String username) {
		Task task = taskMapper.findByIdAndUser(id, username);
		if (task == null) {
			throw new BusinessException("対象のタスクが見つかりません。");
		}
		return task;
	}

	// 更新
	@Transactional
	public void updateApiTask(Long id, TaskUpdateRequest request, String username) {
		int updated = taskMapper.updateTaskByUser(id, request, username);
		if (updated == 0) {
			throw new BusinessException("更新対象のタスクが見つかりません。");
		}
	}

	// 削除
	@Transactional
	public void deleteTask(Long id, String username) {
		int deleted = taskMapper.deleteTaskByUser(id, username);
		if (deleted == 0) {
			throw new BusinessException("削除対象のタスクが見つかりません。");
		}
	}

	// ページネーション
	private static final int PAGE_SIZE = 10;

	@Transactional(readOnly = true)
	public List<Task> getTaskByPage(int page, String username) {
		int totalPages = getTotalPages(username);
		int safePage = Math.max(1, Math.min(page, totalPages));
		int offset = (safePage - 1) * PAGE_SIZE;
		return taskMapper.selectPageByUser(username, PAGE_SIZE, offset);
	}

	@Transactional(readOnly = true)
	public int getTotalPages(String username) {
		long total = taskMapper.countTotalByUser(username); // データベースの総件数
		int pages = (int) ((total + PAGE_SIZE - 1) / PAGE_SIZE);
		return Math.max(pages, 1);

	}

	@Transactional(readOnly = true)
	public int clampPage(int page, String username) {
		int totalPages = getTotalPages(username);
		return Math.max(1, Math.min(page, totalPages));
	}

	// CSV出力
	@Transactional(readOnly = true)
	public List<Task> exportTasksByPeriod(String username, LocalDate startDate, LocalDate endDate) {
		if (startDate.isAfter(endDate)) {
			throw new BusinessException("不正な日付範囲です。");
		}

		List<Task> tasks = taskMapper.selectTasksForExportByPeriod(username, startDate, endDate);
		if (tasks.isEmpty()) {
			throw new BusinessException("出力件数は0件です。");
		}
		return tasks;
	}

	@Transactional(readOnly = true)
	public List<Task> exportTasksByPeriodApi(String username, LocalDate startDate, LocalDate endDate) {
		// CSV出力(新規、0件チェックの部分がない)
		if (startDate.isAfter(endDate)) {
			throw new ValidationException("不正な日付範囲です。");
		}
		
		return taskMapper.selectTasksForExportByPeriod(username, startDate, endDate);
	}

	@Transactional(readOnly = true)
	public Task exportTaskById(Long id, String username) {
		Task task = taskMapper.selectTaskForExportById(username, id);
		if (task == null) {
			throw new BusinessException("対象のタスクが見つかりません。");
		}
		return task;
	}

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public byte[] convertTasksToCsv(List<Task> tasks) {
		String[] header = { "ID", "ユーザー名", "タイトル", "内容", "登録者", "開始日", "終了日", "作成日時", "更新日" };

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			// Excelで開いたときに文字化けしないよう、UTF-8のBOM(先頭マーカー)を付与する
			baos.write(0xEF);
			baos.write(0xBB);
			baos.write(0xBF);

			try (CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))) {
				csvWriter.writeNext(header);
				for (Task task : tasks) {
					String[] row = {
							String.valueOf(task.getId()),
							task.getUsername(),
							task.getTitle(),
							task.getContent(),
							task.getName(),
							String.valueOf(task.getStartDate()),
							String.valueOf(task.getEndDate()),
							formatDateTime(task.getCreatedAt()),
							formatDateTime(task.getUpdatedAt())
					};
					csvWriter.writeNext(row);
				}
			}
			return baos.toByteArray();

		} catch (IOException e) {
			throw new BusinessException("CSV出力処理でエラーが発生しました。");
		}

	}

	private String formatDateTime(LocalDateTime dateTime) {
		return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : "";
	}

	// == 旧 Service 使用しなくなる予定 == //
	@Transactional
	public void insertTask(Task task, String username) {
		task.setUsername(username);
		taskMapper.insertTask(task);
	}

	@Transactional
	public void updateTask(Task task, String username) {
		task.setUsername(username);
		int updated = taskMapper.updateTask(task);
		if (updated == 0) {
			throw new BusinessException("更新対象のタスクが見つかりません。");
		}
	}
}
