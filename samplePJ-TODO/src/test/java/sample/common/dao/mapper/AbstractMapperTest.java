package sample.common.dao.mapper;

import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.testcontainers.junit.jupiter.Testcontainers;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public abstract class AbstractMapperTest {
    // 案B: src/test/resources/application.properties が自動的に使われるので、
    // このクラスでは接続先の設定は不要
}