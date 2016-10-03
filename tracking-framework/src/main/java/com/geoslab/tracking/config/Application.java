package com.geoslab.tracking.config;

import static reactor.event.selector.Selectors.$;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.event.Event;
import reactor.function.Consumer;
import reactor.function.Function;
import reactor.net.NetChannel;
import reactor.net.config.ServerSocketOptions;
import reactor.net.netty.NettyServerSocketOptions;
import reactor.net.netty.tcp.NettyTcpServer;
import reactor.net.tcp.TcpServer;
import reactor.net.tcp.spec.TcpServerSpec;
import reactor.spring.context.config.EnableReactor;

import com.geoslab.tracking.tcp.ResponseCodec;
import com.geoslab.tracking.web.domain.Response;
import com.geoslab.tracking.web.domain.SemaphoreList;

@ComponentScan("com.geoslab.tracking")
@Configuration
@EnableAutoConfiguration
@EnableReactor

//@EnableJpaRepositories
//@EntityScan
//@EnableWebMvc
public class Application {
	
	public static final SemaphoreList semaphore = new SemaphoreList();
    
	@Autowired
	private Function<Event<Response>, String> nodeEventHandler;
	

	
    @Bean
    public Reactor reactor(Environment env) {
        Reactor reactor = Reactors.reactor(env, Environment.THREAD_POOL);

        // Register event processor on the Reactor
        reactor.receive($("event"), nodeEventHandler);

        return reactor;
    }

    
    @Bean
    public ServerSocketOptions serverSocketOptions() {
        NettyServerSocketOptions options = new NettyServerSocketOptions();
        options.backlog(1000);
        options.reuseAddr(true);
        options.tcpNoDelay(true);
        return options ;
    }
    
    
    @Bean
    public TcpServer<Response, String> tcpServer(Environment env,
                                                                final Reactor reactor,ServerSocketOptions opts,
                                                                final CountDownLatch closeLatch) throws InterruptedException {
        

     // create a spec using the Netty-based server
        TcpServer<Response, String> server = new TcpServerSpec<Response, String>(NettyTcpServer.class)
          .env(env).listen(3001)
          .codec(new ResponseCodec()).synchronousDispatcher().options(opts)
          .consume(new Consumer<NetChannel<Response, String>>() {
            public void accept(final NetChannel<Response, String> conn) {

              // for each connection, process incoming data
              conn.in().consume(new Consumer<Response>() {
                public void accept(Response line) {
                  // handle line feed data
                    String res = nodeEventHandler.apply(Event.wrap(line));
                    //conn.out().accept(res);
                    conn.send(res);
                    
                    //conn.close();
                    
                    // respond to client (newline will be added by codec)
                    /*
                    ObjectMapper mapper = new ObjectMapper();
                    
                        Response response = mapper.readValue(line, Response.class);
                        conn.send(response.getEVT());
                    }
                    catch (JsonParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        conn.send("error");
                    }
                    catch (JsonMappingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        conn.send("error");
                    }
                    catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        conn.send("error");
                    }
                    */

                    //closeLatch.countDown();
                }
              });

            }
          })
          .get();

        server.start().await();

        return  server;
    }
	
	@Bean
    public CountDownLatch closeLatch() {
        return new CountDownLatch(1);
    }
	
	public static void main(String[] args)  throws InterruptedException {
	    ApplicationContext ctx = SpringApplication.run(Application.class, args);
	      // Reactor's TCP servers are non-blocking so we have to do something to keep from exiting the main thread
        CountDownLatch closeLatch = ctx.getBean(CountDownLatch.class);
	    closeLatch.await();
	}
}
