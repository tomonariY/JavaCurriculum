package sample.thymeleaf.web;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import sample.common.dao.entity.Login;
import sample.common.service.LoginService;

@Controller
public class LoginController {
	@Autowired	
	private LoginService loginService;
	
	// ログイン
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView loginShoe(ModelAndView mv) {		
		mv.setViewName("login");
		return mv;
	}
			
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView loginUser(
			@RequestParam("username") String username,
			@RequestParam("password") String password,
			HttpSession session,
			ModelAndView mv) {
						
		try {
			Login user = loginService.loginForm(username, password);
			
			if (user != null) {
				session.setAttribute("loginUser", user);
				mv.setViewName("redirect:/tasks");
				
			} else {
				mv.addObject("error", "ユーザー名またはパスワードが間違っています。");
				mv.setViewName("login");				
		
			}
			
		} catch (Exception e) {
			mv.addObject("error", e.getMessage());
			mv.setViewName("login");
		
		}
		
		return mv;
	}
	
	// ログアウト
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpSession session) {
	    
	    session.invalidate();
	    
	    return "redirect:/login";
	}
	
	// ユーザー登録
	@RequestMapping(value = "/registar", method = RequestMethod.GET)
	public ModelAndView registarShow(ModelAndView mv) {
		
		mv.setViewName("registar");
		return mv;
	}
	
	@RequestMapping(value = "/registar", method = RequestMethod.POST)
	public ModelAndView registarUser(
			@RequestParam("username") String username,
			@RequestParam("password") String password,
			ModelAndView mv) {
			
			try {
			
				loginService.registarNewUser(username, password);
				mv.setViewName("redirect:/login");
				
			} catch (Exception e) {
				
				mv.addObject("error", e.getMessage());
				mv.setViewName("registar");
				
			}
			
			return mv;
			
		}
		
	}