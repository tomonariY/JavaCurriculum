package sample.common.logic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
// Mainクラスからの移動 ※理由:Mainクラスに記載するとテスト時に不具合が発生するため
@MapperScan("sample.common.dao.mapper")
public class MyBatisConfig {

}
