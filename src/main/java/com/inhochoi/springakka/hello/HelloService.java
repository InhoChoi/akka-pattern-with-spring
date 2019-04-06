package com.inhochoi.springakka.hello;

import org.springframework.stereotype.Service;

@Service
public class HelloService {
    public String getHelloWorld(String name) {
        return "Hello " + name;
    }
}
