package br.com.casadocodigo.loja.controlers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
	@RequestMapping("/")
	public String index() {
		System.out.println("Entrando na home da CDC");
		return "home";
	}
}
