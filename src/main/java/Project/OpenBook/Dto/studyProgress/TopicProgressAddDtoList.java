package Project.OpenBook.Dto.studyProgress;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TopicProgressAddDtoList {
    private List<TopicProgressAddDto> progressAddDtoList;
}
