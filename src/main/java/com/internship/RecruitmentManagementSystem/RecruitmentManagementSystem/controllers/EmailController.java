//package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;
//
//import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.EmailService;
//import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.HtmlTemplateBuilder;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/emails")
//@RequiredArgsConstructor
//public class EmailController {
//
//    private final EmailService emailService;
//    private final HtmlTemplateBuilder templateBuilder;
//
//    @GetMapping
//    public ResponseEntity<?> doMail(){
//        String candidateMail = templateBuilder.buildCandidateInterviewTemplate(
//            "kartik",
//                "10-05-2025",
//                "10:10",
//                "manav,ronak",
//                "Java Developer",
//                "http://localhost:8080/"
//        );
//        String interviewerMail = templateBuilder.buildInterviewerInterviewTemplate(
//                "Manav",
//                "Kartik",
//                "10-05-2025",
//                "10:10",
//                "Java Developer",
//                "Ronak",
//                "http://localhost:8080/"
//        );
//        try{
//            emailService.sendMail(
//                    "kartikpatel7892@gmail.com",
//                    "kartikmpatel1302@gmail.com",
//                    "Your Interview Schedule For Java Developer",
//                    candidateMail
//            );
//            emailService.sendMail(
//                    "kartikpatel7892@gmail.com",
//                    "kartikmpatel1302@gmail.com",
//                    "Your Select For Take Interview",
//                    interviewerMail
//            );
//            return new ResponseEntity<>("Mailed !", HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>("Failed !!", HttpStatus.BAD_REQUEST);
//        }
//    }
//}
