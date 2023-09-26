package Project.OpenBook.Domain.ChoiceComment;

import Project.OpenBook.Domain.Choice.Repository.ChoiceRepository;
import Project.OpenBook.Domain.Choice.Service.ChoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChoiceCommentService {

    private final ChoiceRepository choiceRepository;


}
