package Project.OpenBook.Domain.Question;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Domain.Era.Era;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Domain.KeywordPrimaryDate;
import Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Repository.KeywordPrimaryDateRepository;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Repo.KeywordLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Repo.QuestionCategoryLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Repo.TopicLearningRecordRepository;
import Project.OpenBook.Domain.Question.Dto.TimeFlowQuestionDto;
import Project.OpenBook.Domain.Question.Service.GetKeywordByTopicQuestion;
import Project.OpenBook.Domain.Question.Service.GetTopicByKeywordQuestion;
import Project.OpenBook.Domain.Question.Service.QuestionService;
import Project.OpenBook.Domain.QuestionCategory.Repo.QuestionCategoryRepository;
import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import Project.OpenBook.Domain.Timeline.Repo.TimelineRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Topic.TopicPrimaryDate.Domain.TopicPrimaryDate;
import Project.OpenBook.Domain.Topic.TopicPrimaryDate.Repository.TopicPrimaryDateRepository;
import Project.OpenBook.WeightedRandomSelection.WeightedRandomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("ExamQuestionService Class")
public class QuestionServiceTest {
    @InjectMocks
    QuestionService questionService;
    @Mock
    private TopicRepository topicRepository;

    @Mock
    private ChapterRepository chapterRepository;
    @Mock
    private KeywordRepository keywordRepository;
    @Mock
    private TimelineRepository timelineRepository;
    @Mock
    private QuestionCategoryRepository questionCategoryRepository;
    @Mock
    private KeywordPrimaryDateRepository keywordPrimaryDateRepository;
    @Mock
    private TopicPrimaryDateRepository topicPrimaryDateRepository;

    @Mock
    private TopicLearningRecordRepository topicLearningRecordRepository;
    @Mock
    private KeywordLearningRecordRepository keywordLearningRecordRepository;

    @Mock
    private QuestionCategoryLearningRecordRepository questionCategoryLearningRecordRepository;

    @Mock
    private WeightedRandomService weightedRandomService;

    @Mock
    private GetKeywordByTopicQuestion type1;
    @Mock
    private GetTopicByKeywordQuestion type2;

    @Nested
    @DisplayName("queryTimeFlowQuestion() 메서드는")
    @Transactional
    public class queryTimeFlowQuestionTest {

        @Nested
        @DisplayName("존재하는 timelineId를 입력하면")
        @Transactional
        public class inputExistTimelineId {


            @DisplayName("해당 timeline에 속한 모든 primaryDate를 조회해 연도에 맞게 정렬한 후 List<TimeFlowQuestionDto>로 변환해 리턴한다.")
            @Test
            @Transactional
            public void returnExistPrimaryDate() {
                Long timelineId = 3L;
                Long eraId = 2L;
                Chapter ch1 = new Chapter("ch1", 1);
                Chapter mockedChapter = mock(Chapter.class);
                Chapter ch2 = new Chapter("ch2", 2);
                Timeline mockedTimeline = mock(Timeline.class);
                Era mockedEra = mock(Era.class);
                Topic mockedTopic = mock(Topic.class);

                Topic t1 = new Topic("t1", ch1);
                Topic t2 = new Topic("t2", ch2);

                Keyword k1 = new Keyword("k1", "c1.c2.c3", t1);
                Keyword k2 = new Keyword("k2", "c4.c5.c6", t2);

                TopicPrimaryDate tp1 = mock(TopicPrimaryDate.class);
                TopicPrimaryDate tp2 = new TopicPrimaryDate(200, "tp2", t2);

                KeywordPrimaryDate kp1 = new KeywordPrimaryDate(120, "kp1", k1);
                KeywordPrimaryDate kp2 = new KeywordPrimaryDate(150, "kp2", k2);

                List<TopicPrimaryDate> topicPrimaryDateList = Arrays.asList(tp1, tp2);
                List<KeywordPrimaryDate> keywordPrimaryDateList = Arrays.asList(kp1, kp2);

                List<TimeFlowQuestionDto> expectedResultList = Arrays.asList(
                        new TimeFlowQuestionDto(100, "tp1", Arrays.asList(ch1.getTitle(), k1.getName(), k2.getName())),
                        new TimeFlowQuestionDto(kp1.getExtraDate(), kp1.getExtraDateComment(), Arrays.asList("ch1 - t1", "c1", "c2", "c3")),
                        new TimeFlowQuestionDto(kp2.getExtraDate(), kp2.getExtraDateComment(), Arrays.asList("ch2 - t2", "c4", "c5", "c6")),
                        new TimeFlowQuestionDto(tp2.getExtraDate(), tp2.getExtraDateComment(), Arrays.asList(ch2.getTitle()))
                );

                // given
                given(timelineRepository.queryTimelineWithEra(timelineId))
                        .willReturn(Optional.of(mockedTimeline));
                doReturn(mockedEra).when(mockedTimeline).getEra();
                doReturn(100).when(mockedTimeline).getStartDate();
                doReturn(200).when(mockedTimeline).getEndDate();
                doReturn(eraId).when(mockedEra).getId();

                lenient().doReturn(mockedTopic).when(tp1).getTopic();
                lenient().doReturn(100).when(tp1).getExtraDate();
                lenient().doReturn("tp1").when(tp1).getExtraDateComment();
                doReturn(mockedChapter).when(mockedTopic).getChapter();
                doReturn("ch1").when(mockedChapter).getTitle();

                doReturn(mockedTopic).when(tp1).getTopic();
                doReturn(Arrays.asList(k1, k2)).when(mockedTopic).getKeywordList();

                given(topicPrimaryDateRepository.queryTopicPrimaryDateInTimeline(eraId, 100, 200))
                        .willReturn(topicPrimaryDateList);
                given(keywordPrimaryDateRepository.queryKeywordPrimaryDateInTimeline(eraId, 100, 200))
                        .willReturn(keywordPrimaryDateList);

                // when
                List<TimeFlowQuestionDto> timeFlowQuestionDtoList = questionService.queryTimeFlowQuestion(timelineId);

                // then
                assertThat(timeFlowQuestionDtoList).usingRecursiveComparison()
                        .isEqualTo(expectedResultList);
            }
        }

        @Nested
        @DisplayName("존재하지 않는 timelineId를 입력하면")
        public class inputNotExistTimelineId {

            @Test
            @DisplayName("TIMELINE_NOT_FOUND Exception을 던짐.")
            public void throwTimelineNotFoundException() {
            }

        }

        @Nested
        @DisplayName("timelineId == 0인경우")
        public class inputTimelineIdIs0 {

            @Test
            @DisplayName("존재하는 timeline중 하나를 선정해 로직 수행")
            public void selectRandomTimeline_AndReturnTimeFlowQuestion() {

            }
        }

        @Nested
        @DisplayName("timelineId == -1인경우")
        public class inputTimelineIdIsMinus1{

            @Test
            @DisplayName("전체 primaryDate를 조회해서 TimeFlowQuestionDto List 리턴")
            public void selectAll_AndReturnTimeFlowQuestion(){

            }

        }
    }
}