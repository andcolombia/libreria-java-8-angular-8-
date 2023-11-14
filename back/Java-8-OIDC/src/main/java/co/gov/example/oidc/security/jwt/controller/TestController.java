package co.gov.example.oidc.security.jwt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import co.gov.example.oidc.security.jwt.annotations.Authorize;

@RestController
public class TestController {
	@GetMapping("/")
	public String index() {
		return "Sin JWT";
	}

	@Authorize
	@GetMapping("/WeatherForecast/Get")
	public String api() {
		return "[\"Item 1\", \"Item 2\", \"Item 3\", \"Item 4\", \"Item 5\"]";
	}
}
