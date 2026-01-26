package swyp_11.ssubom.domain.topic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import swyp_11.ssubom.domain.topic.entity.TopicGeneration;
import swyp_11.ssubom.domain.topic.repository.TopicGenerationRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TopicStatusService {
    private final TopicGenerationRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateToSuccess(Long id) {
        repository.findById(id).ifPresent(TopicGeneration::complete);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateToCompleteWithErrors(Long id, String msg) {
        repository.findById(id).ifPresent(tg -> tg.completeWithErrors(msg));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateToFail(Long id, String msg) {
        repository.findById(id).ifPresent(tg -> tg.fail("시스템 오류 : " + msg));
    }
}
