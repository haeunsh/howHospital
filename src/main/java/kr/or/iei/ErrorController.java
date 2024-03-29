package kr.or.iei;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/error")
public class ErrorController {
	
	@GetMapping(value="/notFound")
	public String notFound() {
		return "error/404error";
	}
	
	@GetMapping(value="/serverError")
	public String serverError() {
		return "error/500error";
	}
}
