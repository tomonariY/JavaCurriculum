package sample.common.dao.mapper;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;

import sample.Main;
import sample.common.dao.entity.Login;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = Main.class)
public class LoginMapperTest {

	@Autowired
	private LoginMapper loginMapper;

	private Login aliceを登録する() {
		// 全テストの前に、共通で使うログインユーザーを用意しておく
		Login alice = new Login();
		alice.setUsername("alice");
		alice.setPassword("dummyHashedPassword");
		loginMapper.insertUser(alice);
		return alice;
	}

	// ===== findByUsername =====
	@Test // 正常系
	void findByUsername_存在するユーザー名なら取得できる() {
		// given
		aliceを登録する();

		// when
		Login result = loginMapper.findByUsername("alice");

		// then
		assertThat(result).isNotNull();
		assertThat(result.getUsername()).isEqualTo("alice");
	}

	@Test // 異常系
	void findByUsername_存在しないユーザー名ならnullが返る() {
		// given 何も登録しない

		// when
		Login result = loginMapper.findByUsername("alice");

		// then
		assertThat(result).isNull();
	}

	// ===== insertUser =====
	@Test // 正常系
	void insertUser_登録した内容が正しく保存される() {
		// given / when
		aliceを登録する();

		// then
		Login result = loginMapper.findByUsername("alice");
		assertThat(result).isNotNull();
		assertThat(result.getUsername()).isEqualTo("alice");

	}

	@Test // 境界値
	void insertUser_同一usernameを重複登録しようとするとDB制約違反になる() {
		// given
		aliceを登録する();

		// when
		Login duplicateAlice = new Login();
		duplicateAlice.setUsername("alice");
		duplicateAlice.setPassword("別パスワード");

		// then
		assertThatThrownBy(() -> loginMapper.insertUser(duplicateAlice))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

}
