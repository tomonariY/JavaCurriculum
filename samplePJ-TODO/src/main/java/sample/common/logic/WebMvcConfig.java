package sample.common.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	@Autowired
	private LoginInterceptor loginInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO 自動生成されたメソッド・スタブ
		registry.addInterceptor(loginInterceptor)
		
			.addPathPatterns("/tasks/**");
	}
}
