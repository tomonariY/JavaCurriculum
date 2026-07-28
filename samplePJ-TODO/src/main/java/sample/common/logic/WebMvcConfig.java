package sample.common.logic;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	private final LoginInterceptor loginInterceptor;
	
    public WebMvcConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		 // /tasks 配下、/api/tasks 配下はログイン必須のため認証チェックを挟む
		registry.addInterceptor(loginInterceptor)		
				.addPathPatterns("/tasks/**", "/api/tasks/**");
	}
}
