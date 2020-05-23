package yateen.examples.web.client;

import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscriber;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yateen.examples.web.client.dto.User;

public class Application {

	public static void main(String[] args) throws InterruptedException {
		
		SpringApplication.run(Application.class, args);
		CountDownLatch latch =new CountDownLatch(1);
		
			
	//	post(latch);
	//	put(latch,"vaibhav");
		retrieveAndTail(latch);
		latch.await();
		System.out.println("await over");
		
	}
	public static void retrieve(CountDownLatch latch)
	{
		WebClient client = WebClient.create("http://localhost:8080");
		client.get()
			.uri("/users")
			.retrieve()
			.bodyToFlux(User.class)
			.doOnComplete(()->{System.out.println("get users complete"); 
				
			})
			.doOnTerminate(()->latch.countDown())
			.subscribe(user->{System.out.println("user received: "+user.toString());});
		
		System.out.println("get all users request sent");
	}
	
	public static void retrieveAndTail(CountDownLatch latch)
	{
		WebClient client = WebClient.create("http://localhost:8080");
		client.get()
			.uri("/functional/tail")
			.retrieve()
			.bodyToFlux(User.class)
			.doOnError(err->{System.out.println("error while retrieving user"+ err);})
			.doOnComplete(()->{System.out.println("get users complete"); 
				
			})
			.doOnTerminate(()->latch.countDown())
			.subscribe(user->{System.out.println("user received: "+user.toString());});
		
		System.out.println("get all users request sent");
	}
	
	public static void retrieve(CountDownLatch latch, String userName)
	{
		WebClient client = WebClient.create("http://localhost:8080");
		client.get()
			.uri("/users/{user_name}", userName)
			.retrieve()
			.bodyToFlux(User.class)
			.doOnError(err->{System.out.println("error while retrieving user"+ err);})
			.doOnComplete(()->{System.out.println("get users complete"); 
				
			})
			.doOnTerminate(()->latch.countDown())
			.subscribe(user->{System.out.println("user exchanged: "+user.toString());});
		
		System.out.println("get user request sent");
	}
	public static void exchange(CountDownLatch latch)
	{
		WebClient client = WebClient.create("http://localhost:8080");
		client.get()
			.uri("/users")
			.exchange()
			.flatMapMany(response->response.bodyToFlux(User.class))
			.doOnComplete(()->{System.out.println("get users exchange complete"); 
				latch.countDown();
			})
			.subscribe(user->{System.out.println("user received: "+user.toString());});
		
		System.out.println("get all users request sent");
	}
	
	
	
	public static void post(CountDownLatch latch)
	{
		Subscriber sub;
		User user = new User();
		user.setId("2");
		user.setAge(24);
		user.setUserName("vaibhav");
		WebClient client = WebClient.create("http://localhost:8080");
		client.post()
			.uri("/users")
			.body(Mono.just(user), User.class)
			.retrieve()
			.bodyToMono(User.class)
			.doOnError(error->{System.out.println("Error while posting user"+ error);})
			.doOnSuccess(x->{System.out.println("post complete"); x.toString(); 
			})
			.doOnTerminate(()->{latch.countDown();})
			.subscribe(x->{System.out.println("user posted: "+x);});
		
		System.out.println("post user request sent");
	}
	
	public static void put(CountDownLatch latch, String userName)
	{
		User user = new User();
		user.setAge(45);
		WebClient client = WebClient.create("http://localhost:8080");
		client.put()
			.uri("/users/{user_name}", userName)
			.body(Mono.just(user), User.class)
			.retrieve()
			.bodyToMono(User.class)
			.map(x->{System.out.println("printing deleted user"); throw new IllegalArgumentException("man made tragedy occured");})
			.doOnError(error->{System.out.println("Error while updating user"+ error);})
			.doOnSuccess(x->{System.out.println("update complete"); x.toString(); 
			})
			.doOnTerminate(()->{latch.countDown();})
			.subscribe(x->{System.out.println("user updated: "+x);});
		
		System.out.println("put user request sent");
	}
	
	public static void patch(CountDownLatch latch, String userName)
	{
		
		WebClient client = WebClient.create("http://localhost:8080");
		client.patch()
			.uri("/users/{user_name}", userName)
			.retrieve()
			.bodyToMono(Void.class)
			.map(x->{System.out.println("printing deleted user"); return x;})
			.doOnError(error->{System.out.println("Error while deleting user"+ error);})
			.doOnSuccess(x->{System.out.println("delete complete");  
			})
			.doOnTerminate(()->{latch.countDown();})
			.subscribe(x->{System.out.println("user deleted: ");});
		
		System.out.println("delete user request sent");
	}
	
	public static void delete(CountDownLatch latch, String userName)
	{
		
		WebClient client = WebClient.create("http://localhost:8080");
		client.delete()
			.uri("/users/{user_name}", userName)
			.retrieve()
			.bodyToMono(Void.class)
			.map(x->{System.out.println("printing deleted user"); return x;})
			.doOnError(error->{System.out.println("Error while deleting user"+ error);})
			.doOnSuccess(x->{System.out.println("delete complete");  
			})
			.doOnTerminate(()->{latch.countDown();})
			.subscribe(x->{System.out.println("user deleted: ");});
		
		System.out.println("delete user request sent");
	}


}
