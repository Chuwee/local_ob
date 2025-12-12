package es.onebox.fever.service;

import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.fever.converter.PostBookingQuestionConverter;
import es.onebox.fever.dto.UpdatePostBookingQuestionsDTO;
import org.springframework.stereotype.Service;


@Service
public class PostBookingQuestionService {

    private final MsEventRepository msEventRepository;

    public PostBookingQuestionService(MsEventRepository msEventRepository) {

        this.msEventRepository = msEventRepository;
    }

    public void updatePostBookingQuestions(UpdatePostBookingQuestionsDTO request) {

        msEventRepository.updatePostBookingQuestions(PostBookingQuestionConverter.toMsUpdatePostBookingQuestions(request));
    }
}
