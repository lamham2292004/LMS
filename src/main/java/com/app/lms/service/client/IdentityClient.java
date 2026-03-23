package com.app.lms.service.client;

import com.app.lms.dto.response.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "identity-service", url = "${identity.service.url}")
public interface IdentityClient {
    
    @GetMapping("/api/v1/lecturers/{lecturerId}")
    UserInfoResponse getLecturerInfo(@PathVariable("lecturerId") Long lecturerId);
}

