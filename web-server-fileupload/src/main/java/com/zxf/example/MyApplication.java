package com.zxf.example;

import com.zxf.example.monitor.DescriptorMonitor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;

@SpringBootApplication
public class MyApplication {

	public static void main(String[] args) {
		DescriptorMonitor monitor = new DescriptorMonitor(Duration.ofSeconds(30), 10, true);
		monitor.start();
		SpringApplication.run(MyApplication.class, args);
	}

}
