package com.myadvice.myadvice.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class InquiryService {

    private final AtomicInteger inquiryIds = new AtomicInteger(1);
    private final AtomicInteger responseIds = new AtomicInteger(1);
    private final List<CurriculumInquiry> inquiries = Collections.synchronizedList(new ArrayList<>());
    private final List<CurriculumResponse> responses = Collections.synchronizedList(new ArrayList<>());

    public CurriculumInquiry addInquiry(String studentId, String studentName, String body) {
        CurriculumInquiry inquiry = new CurriculumInquiry(
                String.valueOf(inquiryIds.getAndIncrement()),
                studentId,
                studentName,
                body,
                LocalDateTime.now().toString()
        );
        inquiries.add(inquiry);
        return inquiry;
    }

    public List<CurriculumInquiry> getAllInquiries() {
        synchronized (inquiries) {
            return new ArrayList<>(inquiries);
        }
    }

    public CurriculumResponse addResponse(String inquiryId, String facultyName, String body) {
        CurriculumInquiry inquiry = findInquiry(inquiryId);
        CurriculumResponse response = new CurriculumResponse(
                String.valueOf(responseIds.getAndIncrement()),
                inquiryId,
                inquiry == null ? null : inquiry.studentId(),
                facultyName,
                body,
                LocalDateTime.now().toString()
        );
        responses.add(response);
        return response;
    }

    public List<CurriculumResponse> getResponsesForStudent(String studentId) {
        List<CurriculumResponse> result = new ArrayList<>();
        synchronized (responses) {
            for (CurriculumResponse response : responses) {
                if (studentId.equals(response.studentId())) {
                    result.add(response);
                }
            }
        }
        return result;
    }

    private CurriculumInquiry findInquiry(String inquiryId) {
        synchronized (inquiries) {
            for (CurriculumInquiry inquiry : inquiries) {
                if (inquiryId.equals(inquiry.inquiryId())) {
                    return inquiry;
                }
            }
        }
        return null;
    }

    public record CurriculumInquiry(String inquiryId, String studentId, String studentName,
                                    String body, String createdAt) {}

    public record CurriculumResponse(String responseId, String inquiryId, String studentId,
                                     String facultyName, String body, String createdAt) {}
}
