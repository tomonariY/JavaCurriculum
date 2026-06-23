package sample; // パッケージ名が sample になっていることを確認

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// ★追加：MyBatisのMapperインターフェースが置いてあるパッケージを正確に指定します
@MapperScan("sample.common.dao.mapper") 
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}