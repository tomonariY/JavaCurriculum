package sample.common.logic;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public String handleBusiness(BusinessException e, RedirectAttributes ra) {
		ra.addFlashAttribute("error", e.getMessage());
		return "redirect:/tasks";
	}
}