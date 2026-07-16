package sample.common.dao.mapper;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;

import sample.Main;
import sample.common.dao.entity.Login;
import sample.common.dao.entity.Task;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = Main.class)
public class TaskMapperTest {
	
	@Autowired
	private TaskMapper taskMapper;
	
	@Autowired
	private LoginMapper loginMapper;
	
	
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
	
	private Task aliceのタスクを1件登録する(String title) {
        Task task = new Task();
        task.setUsername("alice");
        task.setTitle(title);
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
        aliceTask.setTitle("更新後タイトル");

        // when: alice自身として取得する
        int updated = taskMapper.updateTaskByUser(aliceTask);

        // then: 正しく取得できる
        assertThat(updated).isEqualTo(1);
	}

	@Test // 異常系
	void updateTaskByUser_他人からは更新できない() {
        // given
		Task aliceTask = aliceのタスクを1件登録する("更新前タイトル");
		aliceTask.setUsername("bob");
		aliceTask.setTitle("bobが勝手に書き換えようとしたタイトル");
		
		// when: bobとして更新しようとする
		int updated = taskMapper.updateTaskByUser(aliceTask);
		
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
		assertThat(result).isEmpty();;
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
}
