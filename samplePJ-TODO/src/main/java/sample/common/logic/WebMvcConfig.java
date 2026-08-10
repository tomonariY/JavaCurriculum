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
		// /tasks 配下（画面系）はログイン必須のためリダイレクト用の認証チェックを挟む。
		// /api/tasks 配下（API系）は TaskApiController 側で 401 を返すため対象外。
		registry.addInterceptor(loginInterceptor)		
				.addPathPatterns("/tasks/**");
	}
}
