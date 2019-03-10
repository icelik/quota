package icelik.quota.apigw.resource;

import icelik.quota.apigw.RateLimitedException;
import icelik.quota.apigw.dto.Greeting;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloResource {

	@RequestMapping(value = "/hello")
	@GetMapping
	public Greeting sayHello(@RequestParam String name) {
		return new Greeting("Hello " + name);
	}

	@ExceptionHandler(RateLimitedException.class)
	@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
	public void handleResourceNotFoundException() {
	}
}
