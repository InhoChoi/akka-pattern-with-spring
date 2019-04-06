package com.inhochoi.springakka.child;

import org.springframework.stereotype.Service;

@Service
public class ChildService {

    public Long multipy(Long a, Long b) {
        return a * b;
    }
}
