package sample.common.logic;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {
	
	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse respone,
			Object handler
			) throws Exception {
		
		HttpSession session = request.getSession();
		
		Object loginUser = session.getAttribute("loginUser");
		
		if (loginUser == null) {
			respone.sendRedirect(request.getContextPath() + "/login");
			return false;
		}
		
		return true;
	}
}
