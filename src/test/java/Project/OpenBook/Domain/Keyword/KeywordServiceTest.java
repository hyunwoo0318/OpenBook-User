package Project.OpenBook.Domain.Keyword;

import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.Keyword.Service.KeywordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("KeywordService Class")
public class KeywordServiceTest {

    @InjectMocks
    KeywordService keywordService;

    @Mock
    KeywordRepository keywordRepository;




}
