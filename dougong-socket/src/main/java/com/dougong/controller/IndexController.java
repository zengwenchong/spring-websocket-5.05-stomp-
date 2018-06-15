package com.dougong.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 
 * @author zengwc
 * @email 
 * Company:杭州枓栱科技信息技术有限公司
 * CreateDate 2017年11月6日 下午6:00:44 
 * Description:
 * @since JDK 1.8
 * @version 
 * @see
 */
@Controller
@RequestMapping(value = "/index")
public class IndexController {

	@RequestMapping
	public String index() {
		return "index";
	}
	
	@RequestMapping("/socket")
	public String socket() {
		return "socket";
	}
}
