package Project.OpenBook.Domain.Search;

import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.Search.ChapterSearch.ChapterSearchRepository;
import Project.OpenBook.Domain.Search.Dto.ChapterSearchResultDto;
import Project.OpenBook.Domain.Search.Dto.KeywordSearchResultDto;
import Project.OpenBook.Domain.Search.Dto.SearchResultDto;
import Project.OpenBook.Domain.Search.Dto.TopicSearchResultDto;
import Project.OpenBook.Domain.Search.KeywordSearch.KeywordSearchRepository;
import Project.OpenBook.Domain.Search.TopicSearch.TopicSearchRepository;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SearchService {

    private final TopicSearchRepository topicSearchRepository;
    private final KeywordSearchRepository keywordSearchRepository;
    private final ChapterSearchRepository chapterSearchRepository;

    private final TopicRepository topicRepository;
    private final ChapterRepository chapterRepository;
    private final KeywordRepository keywordRepository;

    @Transactional
    public SearchResultDto searchByInput(String input) {
        input = input.replaceAll(" ", "");
        //        List<TopicSearch> topicSearchList =
        //                topicSearchRepository.queryTopicSearchNameByInput(input);
        //        List<KeywordSearch> keywordNameSearchList =
        //                keywordSearchRepository.queryKeywordSearchNameByInput(input);
        //        List<ChapterSearch> chapterSearchList =
        //                chapterSearchRepository.queryChapterSearchNameByInput(input);

        //        List<ChapterSearchResultDto> chapterList =
        //                chapterSearchList.stream()
        //                        .map(cs -> new ChapterSearchResultDto(cs.getChapterNumber(),
        // cs.getTitle()))
        //
        // .sorted(Comparator.comparing(ChapterSearchResultDto::getChapterNumber))
        //                        .collect(Collectors.toList());

        //        List<TopicSearchResultDto> topicList = topicSearchList.stream()
        //            .map(ts -> new TopicSearchResultDto(ts.getChapterNumber(),
        // ts.getChapterTitle(),
        //                ts.getTitle()))
        //            .sorted(Comparator.comparing(TopicSearchResultDto::getChapterNumber))
        //            .collect(Collectors.toList());

        List<TopicSearchResultDto> topicList =
                topicRepository.searchTopic(input).stream()
                        .map(TopicSearchResultDto::new)
                        .collect(Collectors.toList());

        List<ChapterSearchResultDto> chapterList =
                chapterRepository.searchChapter(input).stream()
                        .map(ChapterSearchResultDto::new)
                        .collect(Collectors.toList());

        List<KeywordSearchResultDto> keywordNameList =
                keywordRepository.searchKeywords(input).stream()
                        .map(KeywordSearchResultDto::new)
                        .collect(Collectors.toList());

        return new SearchResultDto(chapterList, topicList, keywordNameList);
    }
}
