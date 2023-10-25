package Project.OpenBook.Domain.Search;

import Project.OpenBook.Domain.Search.ChapterSearch.ChapterSearch;
import Project.OpenBook.Domain.Search.ChapterSearch.ChapterSearchRepository;
import Project.OpenBook.Domain.Search.KeywordSearch.KeywordSearch;
import Project.OpenBook.Domain.Search.KeywordSearch.KeywordSearchRepository;
import Project.OpenBook.Domain.Search.TopicSearch.TopicSearch;
import Project.OpenBook.Domain.Search.TopicSearch.TopicSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SearchService {
    private final TopicSearchRepository topicSearchRepository;
    private final KeywordSearchRepository keywordSearchRepository;
    private final ChapterSearchRepository chapterSearchRepository;

    @Transactional
    public SearchResultDto searchByInput(String input) {
        List<TopicSearch> topicSearchList = topicSearchRepository.queryTopicSearchNameByInput(input);
        List<KeywordSearch> keywordSearchList = keywordSearchRepository.queryKeywordSearchNameByInput(input);
        List<ChapterSearch> chapterSearchList = chapterSearchRepository.queryChapterSearchNameByInput(input);

        List<ChapterSearchResultDto> chapterList = chapterSearchList.stream()
                .map(cs -> new ChapterSearchResultDto(cs.getChapterNumber(), cs.getTitle()))
                .sorted(Comparator.comparing(ChapterSearchResultDto::getChapterNumber))
                .collect(Collectors.toList());

        List<TopicSearchResultDto> topicList = topicSearchList.stream()
                .map(ts -> new TopicSearchResultDto(ts.getChapterNumber(), ts.getChapterTitle(), ts.getTitle()))
                .sorted(Comparator.comparing(TopicSearchResultDto::getChapterNumber))
                .collect(Collectors.toList());

        List<KeywordSearchResultDto> keywordList = keywordSearchList.stream()
                .map(ks -> new KeywordSearchResultDto(ks.getChapterNumber(), ks.getChapterTitle(), ks.getTopicTitle(),
                        ks.getName(), ks.getComment()))
                .sorted(Comparator.comparing(KeywordSearchResultDto::getChapterNumber))
                .collect(Collectors.toList());

        return new SearchResultDto(chapterList, topicList, keywordList);
    }
}
