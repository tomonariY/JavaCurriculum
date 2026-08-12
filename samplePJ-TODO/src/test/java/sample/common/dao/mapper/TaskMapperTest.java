package sample.common.dao.mapper;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import sample.common.dao.entity.Login;
import sample.common.dao.entity.Task;
import sample.common.dto.TaskRequest;


class TaskMapperTest extends AbstractMapperTest {

	@Autowired private TaskMapper taskMapper;
	@Autowired private LoginMapper loginMapper;

	@BeforeEach
	void セットアップ() {
		// 全テストの前に、共通で使うログインユーザーを用意しておく
		Login alice = new Login();
		alice.setUsername("alice");
		alice.setPassword("dummyHashedPassword");
		loginMapper.insertUser(alice);

		Login bob = new Login();
		bob.setUsername("bob");
		bob.setPassword("dummyHashedPassword");
		loginMapper.insertUser(bob);
	}

	/**
	 * 任意のユーザー・任意の日付でタスクを1件登録する（最も汎用的な版）。
	 * 他ユーザー（bob等）のタスクを用意したいときや、日付を指定したいときに使う。
	 */
	private Task タスクを1件登録する(String username, String title, LocalDate startDate, LocalDate endDate) {
		Task task = new Task();
		task.setUsername(username);
		task.setTitle(title);
		task.setStartDate(startDate);
		task.setEndDate(endDate);
		taskMapper.insertTask(task);
		return task;
	}

	/**
	 * aliceのタスクを1件登録する（日付不要なテスト向けの簡易版）。
	 * 日付が結果に影響しないテスト（IDOR確認や更新・削除など）ではこちらを使う。
	 */
	private Task aliceのタスクを1件登録する(String title) {
		return aliceのタスクを1件登録する(title, null, null);
	}

	/**
	 * aliceのタスクを1件登録する（期間指定が必要なテスト向け）。
	 * CSVエクスポート（期間指定）のような、startDate/endDateが結果に影響するテストで使う。
	 */
	private Task aliceのタスクを1件登録する(String title, LocalDate startDate, LocalDate enddate) {
		Task task = new Task();
		task.setUsername("alice");
		task.setTitle(title);
		task.setStartDate(startDate);
		task.setEndDate(enddate);
		taskMapper.insertTask(task);
		return task;
	}

	// ===== findByIdAndUse =====
	@Test // 正常系
	void findByIdAndUser_本人なら取得できる() {
		// given
		Task aliceTask = aliceのタスクを1件登録する("aliceのタスク");

		// when: alice自身として取得する
		Task result = taskMapper.findByIdAndUser(aliceTask.getId(), "alice");

		// then: 正しく取得できる
		assertThat(result).isNotNull();
		assertThat(result.getUsername()).isEqualTo("alice");
	}

	@Test // 異常系
	void findByIdAndUser_他人から取得できない() {
		// given
		Task aliceTask = aliceのタスクを1件登録する("aliceのタスク");

		// when: bobとして取得する
		Task result = taskMapper.findByIdAndUser(aliceTask.getId(), "bob");

		// then: 正しく取得できる
		assertThat(result).isNull();
	}

	// ===== updateTaskByUser =====
	@Test // 正常系
	void updateTaskByUser_本人なら更新できる() {
		// given
		Task aliceTask = aliceのタスクを1件登録する("更新前タイトル");

		TaskRequest request = new TaskRequest();
		request.setTitle("更新後タイトル");
		request.setContent(aliceTask.getContent());
		request.setStartDate(aliceTask.getStartDate());
		request.setEndDate(aliceTask.getEndDate());

		// when: alice自身として取得する
		int updated = taskMapper.updateTaskByUser(aliceTask.getId(), request, "alice");

		// then: 正しく取得できる
		assertThat(updated).isEqualTo(1);
	}

	@Test // 異常系
	void updateTaskByUser_他人からは更新できない() {
		// given
		Task aliceTask = aliceのタスクを1件登録する("更新前タイトル");

		TaskRequest request = new TaskRequest();
		request.setTitle("bobが勝手に書き換えようとしたタイトル");

		// when: bobとして更新しようとする
		int updated = taskMapper.updateTaskByUser(aliceTask.getId(), request, "bob");

		// then: 他人のタスクなので更新されない
		assertThat(updated).isEqualTo(0);
	}

	// ===== deleteTaskByUser =====
	@Test // 正常系
	void deleteTaskByUser_本人なら削除できる() {
		// given
		Task aliceTask = aliceのタスクを1件登録する("削除前タイトル");

		// when: alice自身として削除する
		int deleted = taskMapper.deleteTaskByUser(aliceTask.getId(), "alice");

		// then: 正しく削除できる
		assertThat(deleted).isEqualTo(1);
	}

	@Test // 異常系
	void deleteTaskByUser_他人からは削除できない() {
		// given
		Task aliceTask = aliceのタスクを1件登録する("削除前タイトル");

		// when: bobとして削除しようとする
		int deleted = taskMapper.deleteTaskByUser(aliceTask.getId(), "bob");

		// then: 他人のタスクなので削除されない
		assertThat(deleted).isEqualTo(0);
	}

	// ===== selectPageByUser =====
	@Test // 正常系
	void selectPageByUser_他人のタスクは含まれない() {
		// given: aliceのタスクを2件、bobのタスクを1件登録する
		aliceのタスクを1件登録する("aliceのタスク1");
		aliceのタスクを1件登録する("aliceのタスク2");

		Task bobTask = new Task();
		bobTask.setUsername("bob");
		bobTask.setTitle("bobのタスク");
		taskMapper.insertTask(bobTask);

		// when: aliceとして一覧を取得する(1ページ目、10件まで)
		List<Task> result = taskMapper.selectPageByUser("alice", 10, 0);

		// then: aliceのタスクだけが2件含まれる(bobのタスクは含まれない)
		assertThat(result).hasSize(2);
	}

	@Test // 正常系
	void selectPageByUser_2ページ目には残りの件数が表示される() {
		// given: aliceのタスクを15件登録する
		for (int i = 1; i <= 15; i++) {
			aliceのタスクを1件登録する("タスク" + i);
		}

		// when: 2ページ目を取得する(1ページ10件なので、offsetは10)
		List<Task> result = taskMapper.selectPageByUser("alice", 10, 10);

		// then: aliceのタスクだけが2件含まれる(bobのタスクは含まれない)
		assertThat(result).hasSize(5);
	}

	@Test // 境界値
	void selectPageByUser_タスクが0件なら空のリストが返る() {
		// given: 何も登録しない(aliceはログインユーザーとして存在するが、タスクは0件)

		// when: 2ページ目を取得する(1ページ10件なので、offsetは10)
		List<Task> result = taskMapper.selectPageByUser("alice", 10, 0);

		// then: aliceのタスクだけが2件含まれる(bobのタスクは含まれない)
		assertThat(result).isEmpty();

	}

	@Test // 正常系
	void insertTask_登録した内容が正しく保存される() {
		// given / when
		Task aliceTask = aliceのタスクを1件登録する("新規タスク");

		// then: aliceのタスクだけが2件含まれる(bobのタスクは含まれない)
		Task result = taskMapper.findByIdAndUser(aliceTask.getId(), "alice");
		assertThat(result).isNotNull();
		assertThat(result.getTitle()).isEqualTo("新規タスク");

	}

	// ===== selectTasksForExportByPeriod =====
	@Test // 正常系
	void selectTasksForExportByPeriod_指定期間内のaliceのタスクだけが取得できる() {
		// given: 期間内のタスクを1件、期間外のタスクを1件、bobのタスクを1件登録する
		aliceのタスクを1件登録する("期間内タスク",
				LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 20));

		aliceのタスクを1件登録する("期間外タスク",
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 5));

		タスクを1件登録する("bob","bobのタスク",
				LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 20));

		// when: aliceとして 2026/01/01〜2026/01/31 の範囲で取得する
		List<Task> result = taskMapper.selectTasksForExportByPeriod(
				"alice", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));

		// then: 期間内・alice本人のタスクだけが1件返る
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getTitle()).isEqualTo("期間内タスク");

	}
	
	@Test // 境界値
	void selectTasksForExportByPeriod_期間の端の日付ちょうどのタスクが含まれるか() {
		// given: 期間の端の日付ちょうどのタスク
		aliceのタスクを1件登録する("期間内タスク",
				LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 10));
		
		// when: aliceとして 2026/01/10〜2026/01/10 の範囲で取得する
		List<Task> result = taskMapper.selectTasksForExportByPeriod(
				"alice", LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 10));
		
		// then: 期間内・alice本人のタスクだけが1件返る
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getTitle()).isEqualTo("期間内タスク");
		
	}
	
	@Test // 境界値
	void selectTasksForExportByPeriod_範囲から1日はみ出すタスクは含まれない() {
		// given: 終了日が指定範囲を1日超えているタスク
		aliceのタスクを1件登録する("期間外タスク",
				LocalDate.of(2026, 1, 10), LocalDate.of(2026, 2, 1));
		
		// when: aliceとして 2026/01/01〜2026/01/31 の範囲で取得する
		List<Task> result = taskMapper.selectTasksForExportByPeriod(
				"alice", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));
		
		// then: 期間からはみ出しているので対象外(0件)
		assertThat(result).isEmpty();
		
	}

	// ===== selectTaskForExportById =====
	@Test // 正常系
	void selectTaskForExportById_本人のタスクなら取得できる() {
		// given: aliceのタスクを1件登録する
		Task aliceTask = aliceのタスクを1件登録する("aliceのタスク");

		// when: alice本人としてそのIDを指定して取得する
		Task result = taskMapper.selectTaskForExportById("alice", aliceTask.getId());

		// then: 正しく取得できる
		assertThat(result).isNotNull();
		assertThat(result.getTitle()).isEqualTo("aliceのタスク");

	}

	@Test // 異常系
	void selectTaskForExportById_他人のタスクは取得できない() {
		// given: aliceのタスクを1件登録する
		Task aliceTask = aliceのタスクを1件登録する("aliceのタスク");

		// when: bobとしてaliceのタスクIDを指定して取得しようとする
		Task result = taskMapper.selectTaskForExportById("bob", aliceTask.getId());
		
		// then: 他人のタスクなので取得できない(nullが返る)
		assertThat(result).isNull();

	}

	@Test // 異常系
	void selectTaskForExportById_存在しないIDなら取得できない() {
		// when: 存在しないaliceのIDを指定して取得しようとする
		Task result = taskMapper.selectTaskForExportById("alice", 999L);
		
		// then: 期間内・alice本人のタスクだけが1件返る
		assertThat(result).isNull();

	}
}
