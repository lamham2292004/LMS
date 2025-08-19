package com.app.lms.service;

import com.app.lms.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseService {
   @Autowired
   private CourseRepository courseRepository;

}
