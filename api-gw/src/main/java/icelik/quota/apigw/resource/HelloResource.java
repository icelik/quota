package icelik.quota.apigw.resource;

import icelik.quota.apigw.Limit;
import icelik.quota.apigw.LimitExceededException;
import icelik.quota.apigw.dto.Greeting;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloResource {

	@RequestMapping(value = "/hello")
	@GetMapping
	@Limit(key = "{#IP + 'sayHello'}",
			treshold = "2",
			blockDurationInMilis = "5000")
	public Greeting sayHello(@RequestParam String name) {
		return new Greeting("Hello " + name);
	}

	@ExceptionHandler(LimitExceededException.class)
	@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
	public void handleResourceNotFoundException() {
		//Do nothing
	}

}
