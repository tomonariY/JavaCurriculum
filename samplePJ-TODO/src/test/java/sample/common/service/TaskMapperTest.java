package sample.common.service;

import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import sample.common.dao.mapper.TaskMapper;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/sql/TaskMapperTest.sql")
public class TaskMapperTest {
	
	private TaskMapper taskMapper;
	
}
