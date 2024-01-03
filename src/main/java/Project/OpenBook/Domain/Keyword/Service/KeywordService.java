package Project.OpenBook.Domain.Keyword.Service;

import Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword.DescriptionKeywordRepository;
import Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Repository.KeywordPrimaryDateRepository;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.Search.KeywordSearch.KeywordSearchRepository;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final KeywordSearchRepository keywordSearchRepository;
    private final DescriptionKeywordRepository descriptionKeywordRepository;
    private final KeywordPrimaryDateRepository keywordPrimaryDateRepository;
    private final TopicRepository topicRepository;
    private final ImageService imageService;






}
