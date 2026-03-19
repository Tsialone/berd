package com.berd.dev.controllers.public_api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.berd.dev.models.User;
import com.berd.dev.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/public_api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/getByLikeName")
    public ResponseEntity<List<User>> getByLikeName(@RequestParam("name") String name) throws InterruptedException {
        Thread.sleep(1000);
        return ResponseEntity.ok(userService.getByLikeName(name));
    }


    @PostMapping("/sendRequest")
    public ResponseEntity<List<User>> sendRequest(@RequestParam("username") String userName , HttpServletRequest request) throws InterruptedException {
        System.out.println("teststttttttttttttttttttttttt");
        userService.resetPasswordRequest(userName , request);
        return ResponseEntity.ok(userService.getByLikeName(userName));
    }


}