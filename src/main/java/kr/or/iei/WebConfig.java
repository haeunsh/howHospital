package kr.or.iei;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer{
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
			registry
				.addResourceHandler("/**")
				.addResourceLocations("classpath:/templates/","classpath:/static/");
			registry
				.addResourceHandler("/doctor/**")
				.addResourceLocations("file:///C:/Temp/hospital/doctor/");
			registry
				.addResourceHandler("/hospital/**")
				.addResourceLocations("file:///C:/Temp/hospital/hospital/");
			registry
				.addResourceHandler("/reservation/**")
				.addResourceLocations("file:///C:/Temp/hospital/reservation/");
			registry
				.addResourceHandler("/community/editor/**")
				.addResourceLocations("file:///C:/Temp/upload/community/editor/");
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		
		registry.addInterceptor(new BlockInterceptor())
		.addPathPatterns("/admin/**","/etc/**","/hospital/**")
		.excludePathPatterns("/admin/adminMsg","/admin/blockMsg","/admin/faqList","admin/noticeList?");
		
		registry.addInterceptor(new AdminInterceptor())
		.addPathPatterns("/admin/**")
		.excludePathPatterns("/admin/adminMsg","/admin/blockMsg","/admin/faqList","admin/noticeList");;
		
	}
}
