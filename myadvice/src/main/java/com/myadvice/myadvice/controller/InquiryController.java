package com.myadvice.myadvice.controller;

import com.myadvice.myadvice.service.InquiryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/curriculum")
public class InquiryController {

    private final InquiryService inquiryService;

    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @PostMapping("/inquiries")
    public ResponseEntity<CurriculumInquiryDto> addInquiry(@RequestBody CreateInquiryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toInquiryDto(
                inquiryService.addInquiry(request.studentId(), request.studentName(), request.message())
        ));
    }

    @GetMapping("/inquiries")
    public List<CurriculumInquiryDto> getAllInquiries() {
        return inquiryService.getAllInquiries().stream().map(this::toInquiryDto).toList();
    }

    @PostMapping("/inquiries/{inquiryId}/responses")
    public ResponseEntity<CurriculumResponseDto> addResponse(@PathVariable String inquiryId,
                                                             @RequestBody CreateResponseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(
                inquiryService.addResponse(inquiryId, request.facultyName(), request.response())
        ));
    }

    @GetMapping("/students/{studentId}/responses")
    public List<CurriculumResponseDto> getStudentResponses(@PathVariable String studentId) {
        return inquiryService.getResponsesForStudent(studentId).stream().map(this::toResponseDto).toList();
    }

    private CurriculumInquiryDto toInquiryDto(InquiryService.CurriculumInquiry inquiry) {
        return new CurriculumInquiryDto(
                inquiry.inquiryId(),
                inquiry.studentId(),
                inquiry.studentName(),
                inquiry.body(),
                inquiry.createdAt()
        );
    }

    private CurriculumResponseDto toResponseDto(InquiryService.CurriculumResponse response) {
        return new CurriculumResponseDto(
                response.responseId(),
                response.inquiryId(),
                response.studentId(),
                response.facultyName(),
                response.body(),
                response.createdAt()
        );
    }

    public record CreateInquiryRequest(String studentId, String studentName, String message) {}
    public record CreateResponseRequest(String facultyName, String response) {}
    public record CurriculumInquiryDto(String inquiryId, String studentId, String studentName,
                                       String body, String createdAt) {}
    public record CurriculumResponseDto(String responseId, String inquiryId, String studentId,
                                        String facultyName, String body, String createdAt) {}
}
