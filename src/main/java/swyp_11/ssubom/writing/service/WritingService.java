package swyp_11.ssubom.writing.service;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import swyp_11.ssubom.writing.dto.WritingCreateRequest;
import swyp_11.ssubom.writing.dto.WritingCreateResponse;
import swyp_11.ssubom.writing.repository.PostRepository;

public interface WritingService {
    WritingCreateResponse createWriting(Long userId, WritingCreateRequest request);
}