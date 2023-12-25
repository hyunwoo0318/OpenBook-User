package Project.OpenBook.Domain.JJH;

import Project.OpenBook.Constants.ContentConst;
import Project.OpenBook.Constants.StateConst;
import Project.OpenBook.Domain.Bookmark.Service.BookmarkService;
import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.JJH.JJHContent.JJHContent;
import Project.OpenBook.Domain.JJH.JJHContent.JJHContentRepository;
import Project.OpenBook.Domain.JJH.JJHContentProgress.JJHContentProgress;
import Project.OpenBook.Domain.JJH.JJHContentProgress.JJHContentProgressRepository;
import Project.OpenBook.Domain.JJH.JJHList.JJHList;
import Project.OpenBook.Domain.JJH.JJHList.JJHListRepository;
import Project.OpenBook.Domain.JJH.JJHListProgress.JJHListProgress;
import Project.OpenBook.Domain.JJH.JJHListProgress.JJHListProgressRepository;
import Project.OpenBook.Domain.JJH.dto.*;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Repo.TopicLearningRecordRepository;
import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import Project.OpenBook.Domain.Timeline.Repo.TimelineRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;
import static Project.OpenBook.Constants.JJHForFreeConst.JJH_NUMBER_FREE_LIMIT;

@Service
@RequiredArgsConstructor
public class JJHService {

    private final ChapterRepository chapterRepository;
    private final TimelineRepository timelineRepository;
    private final JJHListRepository jjhListRepository;
    private final JJHListProgressRepository jjhListProgressRepository;

    private final JJHContentRepository jjhContentRepository;
    private final JJHContentProgressRepository jjhContentProgressRepository;

    private final TopicLearningRecordRepository topicLearningRecordRepository;
    private final BookmarkService bookmarkService;

    @Transactional(readOnly = true)
    public JJHListAdminQueryDto queryJJHAdmin() {
        List<ChapterJJHAdminQueryDto> chapterList = chapterRepository.queryChaptersWithjjhList().stream()
                .map(c -> {
                    Integer jjhNumber = (!c.getJjhLists().isEmpty()) ? c.getJjhLists().get(0).getNumber() : 1000;
                    return new ChapterJJHAdminQueryDto(c.getNumber(), c.getTitle(),jjhNumber, c.getId());
                })
                .sorted(Comparator.comparing(ChapterJJHAdminQueryDto::getJjhNumber))
                .collect(Collectors.toList());
        List<TimelineJJHAdminQueryDto> timelineList = timelineRepository.queryTimelinesWithEraAndjjhList().stream()
                .map(t -> {
                    Integer jjhNumber = (!t.getJjhLists().isEmpty()) ? t.getJjhLists().get(0).getNumber() : 1000;
                    return new TimelineJJHAdminQueryDto(t.getTitle(),t.getEra().getName(), t.getStartDate(), t.getEndDate(), jjhNumber, t.getId());
                })
                .sorted(Comparator.comparing(TimelineJJHAdminQueryDto::getJjhNumber))
                .collect(Collectors.toList());

        return new JJHListAdminQueryDto(chapterList, timelineList);
    }

    @Transactional(readOnly = true)
    public JJHListCustomerQueryDto queryJJHCustomer(Customer customer) {

        List<ChapterJJHCustomerQueryDto> chapterList = new ArrayList<>();
        List<TimelineJJHCustomerQueryDto> timelineList = new ArrayList<>();

        List<JJHListProgress> jjhListProgressList = jjhListProgressRepository.queryJJHListProgressWithJJHList(customer);
        for (JJHListProgress progress : jjhListProgressList) {
            JJHList jjhList = progress.getJjhList();
            Chapter chapter = jjhList.getChapter();
            Timeline timeline = jjhList.getTimeline();

            if (chapter != null && timeline == null) {
                ChapterJJHCustomerQueryDto dto = new ChapterJJHCustomerQueryDto(chapter.getTitle(), chapter.getNumber(),
                        progress.getState().getName(),  jjhList.getNumber(), chapter.getDateComment());
                chapterList.add(dto);
            } else if (chapter == null && timeline != null) {
                TimelineJJHCustomerQueryDto dto = new TimelineJJHCustomerQueryDto(timeline.getEra().getName(), timeline.getStartDate(), timeline.getEndDate(),
                        progress.getState().getName(), jjhList.getNumber(), timeline.getId(), timeline.getTitle());
                timelineList.add(dto);
            }
        }
        return new JJHListCustomerQueryDto(chapterList, timelineList);
    }

    @Transactional(readOnly = true)
    public List<JJHContentsTableQueryDto> queryJJHContentsTable(Customer customer, Integer jjhNumber) {

        JJHList jjhList = jjhListRepository.queryJJHList(jjhNumber).orElseThrow(() -> {
            throw new CustomException(INVALID_PARAMETER);
        });

        List<JJHContentProgress> jjhContentProgressList
                = jjhContentProgressRepository.queryJJHContentProgressForCustomer(customer,jjhNumber);

        /**
         * 단원의 contents
         */
        if (jjhList.getChapter() != null) {
           return makeContentsTableForChapter(customer, jjhContentProgressList, jjhList);
        }
        /**
         * timeline의 contents
         */
        else if (jjhList.getTimeline() != null) {
           return makeContentsTableForTimeline(customer, jjhContentProgressList, jjhList);
        }else{
            throw new CustomException(INVALID_PARAMETER);
        }
    }

    public List<JJHContentsTableQueryDto> queryJJHContentsTableForFree(Integer jjhNumber) {
        JJHList jjhList = jjhListRepository.queryJJHList(jjhNumber).orElseThrow(() -> {
            throw new CustomException(INVALID_PARAMETER);
        });

        Chapter chapter = jjhList.getChapter();
        Timeline timeline = jjhList.getTimeline();
        if (chapter != null) {
            return makeContentsTableForChapterForFree(chapter);
        } else if (timeline != null){
            return makeContentsTableForTimelineForFree(timeline);
        }else{
            throw new CustomException(INVALID_PARAMETER);
        }
    }

    private List<JJHContentsTableQueryDto> makeContentsTableForTimelineForFree(Timeline timeline) {
        List<JJHContentsTableQueryDto> dtoList = new ArrayList<>();
        String title = timeline.getTitle();
        String category = null;
        String dateComment = null;

        JJHContentsTableQueryDto dto1 = new JJHContentsTableQueryDto(null,title, ContentConst.TIMELINE_STUDY.name(),
                StateConst.COMPLETE.getName(),null,dateComment, category);
        dtoList.add(dto1);

        return dtoList;
    }

    private List<JJHContentsTableQueryDto> makeContentsTableForChapterForFree(Chapter chapter) {
        List<JJHContentsTableQueryDto> dtoList = new ArrayList<>();

        List<Topic> topicList = chapter.getTopicList();
        for (Topic topic : topicList) {
            JJHContentsTableQueryDto dto = new JJHContentsTableQueryDto(null, topic.getTitle(), ContentConst.TOPIC_STUDY.name(), StateConst.COMPLETE.getName(), null,
                    topic.getDateComment(), topic.getQuestionCategory().getCategory().getName());
            dtoList.add(dto);
        }

        dtoList.add(new JJHContentsTableQueryDto(null, chapter.getTitle(), ContentConst.CHAPTER_COMPLETE_QUESTION.name(), StateConst.COMPLETE.getName(),
                null,chapter.getDateComment(),null));


        return dtoList;
    }

    private List<JJHContentsTableQueryDto> makeContentsTableForTimeline(Customer customer, List<JJHContentProgress> jjhContentProgressList, JJHList jjhList) {

        List<JJHContentsTableQueryDto> dtoList = new ArrayList<>();
        JJHContentProgress progress = jjhContentProgressList.get(0);
        JJHContent jjhContent = progress.getJjhContent();
        Timeline timeline = jjhContent.getTimeline();
        String title = timeline.getTitle();
        String category = null;
        String dateComment = null;

        JJHContentsTableQueryDto dto = new JJHContentsTableQueryDto(null,title, jjhContent.getContent().name(),
                progress.getState().getName(), jjhContent.getNumber(),dateComment, category);
        dtoList.add(dto);


        return dtoList;
    }

    private List<JJHContentsTableQueryDto> makeContentsTableForChapter(Customer customer, List<JJHContentProgress> jjhContentProgressList, JJHList jjhList) {

        List<JJHContentsTableQueryDto> dtoList = new ArrayList<>();

        Map<Topic, Boolean> bookmarkMap = bookmarkService.queryBookmarks(customer, jjhList.getChapter().getTopicList());

        for (JJHContentProgress progress : jjhContentProgressList) {
            JJHContent jjhContent = progress.getJjhContent();
            Chapter chapter = jjhContent.getChapter();
            Topic topic = jjhContent.getTopic();
            String title = "";
            String category = null;
            String dateComment = null;
            if (chapter != null) {
                title = chapter.getTitle();
                dateComment = chapter.getDateComment();
            }  else if (topic != null) {
                title = topic.getTitle();
                category = topic.getQuestionCategory().getCategory().getName();
                dateComment = topic.getDateComment();
            }

            JJHContentsTableQueryDto dto = new JJHContentsTableQueryDto(bookmarkMap.get(topic),title, jjhContent.getContent().name(),
                    progress.getState().getName(), jjhContent.getNumber(),dateComment, category);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Transactional
    public void updateJJHList(JJHListUpdateDto dto) {

        Integer chapterType= -1;
        Integer timelineType = 1;
        Map<jjhListType, JJHList> m = new HashMap<>();

        List<JJHUpdateDto> chapterList = dto.getChapterList();
        List<JJHUpdateDto> timelineList = dto.getTimelineList();

        List<JJHList> jjhLists = jjhListRepository.queryJJHListsWithChapterAndTimeline();
        for (JJHList jjhList : jjhLists) {
            if (jjhList.getChapter() == null && jjhList.getTimeline() != null) {
                m.put(new jjhListType(timelineType, jjhList.getTimeline().getId()), jjhList);
            } else if (jjhList.getTimeline() == null && jjhList.getChapter() != null) {
                m.put(new jjhListType(chapterType, jjhList.getChapter().getId()), jjhList);
            } else {
                jjhListRepository.delete(jjhList);
            }
        }

        for (JJHUpdateDto jjhUpdateDto : chapterList) {
            Long chapterId = jjhUpdateDto.getId();
            Integer jjhNumber = jjhUpdateDto.getJjhNumber();
            jjhNumber += 1;
            JJHList jjhList = m.get(new jjhListType(chapterType, jjhUpdateDto.getId()));
            if (jjhList == null) {
                Chapter chapter = chapterRepository.findById(chapterId).orElseThrow(() -> {
                    throw new CustomException(CHAPTER_NOT_FOUND);
                });
                JJHList newJJHList = new JJHList(jjhNumber, chapter);
                jjhListRepository.save(newJJHList);
            }else {
                jjhList.updateNumber(jjhNumber);
            }
        }

        for (JJHUpdateDto jjhUpdateDto : timelineList) {
            Long timelineId = jjhUpdateDto.getId();
            Integer jjhNumber = jjhUpdateDto.getJjhNumber();
            jjhNumber += 1;
            JJHList jjhList = m.get(new jjhListType(timelineType, jjhUpdateDto.getId()));
            if (jjhList == null) {
                Timeline timeline = timelineRepository.findById(timelineId).orElseThrow(() -> {
                    throw new CustomException(TIMELINE_NOT_FOUND);
                });
                JJHList newJJHList = new JJHList(jjhNumber, timeline);
                jjhListRepository.save(newJJHList);
            } else {
                jjhList.updateNumber(jjhNumber);
            }
        }

        jjhLists.sort(Comparator.comparing(JJHList::getNumber));

        updateJJHContent();

    }

    public void updateJJHContent() {
        Integer idx = 1;

        Map<jjhContentType, JJHContent> m = new HashMap<>();
        List<JJHContent> jjhContents = jjhContentRepository.queryJJHContents();
        for (JJHContent jjhContent : jjhContents) {
            if (jjhContent.getTopic() != null) {
                m.put(new jjhContentType(jjhContent.getContent(), jjhContent.getTopic().getId()), jjhContent);
            } else if (jjhContent.getTimeline() != null) {
                m.put(new jjhContentType(jjhContent.getContent(), jjhContent.getTimeline().getId()), jjhContent);
            } else if (jjhContent.getChapter() != null) {
                m.put(new jjhContentType(jjhContent.getContent(), jjhContent.getChapter().getId()), jjhContent);
            }
        }
        List<JJHList> jjhLists = jjhListRepository.queryJJHListsWithChapterAndTimeline()
                .stream().sorted(Comparator.comparing(JJHList::getNumber))
                .collect(Collectors.toList());
        for (JJHList jjhList : jjhLists) {
            Chapter chapter = jjhList.getChapter();
            Timeline timeline = jjhList.getTimeline();
            if (chapter != null) {
                //1. 단원학습 체크
                JJHContent chapterInfoJJHContent = m.get(new jjhContentType(ContentConst.CHAPTER_INFO, chapter.getId()));
                if (chapter.getContent() != null) {
                    if (chapterInfoJJHContent == null) {
                        JJHContent newJJHContent = new JJHContent(ContentConst.CHAPTER_INFO,idx++, jjhList, chapter);
                        jjhContentRepository.save(newJJHContent);
                    }else{
                        chapterInfoJJHContent.updateNumber(idx++);
                    }
                }else{
                    if (chapterInfoJJHContent != null) {
                        jjhContentRepository.delete(chapterInfoJJHContent);
                    }
                }

                //2. 단원 내 토픽들 체크
                List<Topic> topicList = chapter.getTopicList();
                for (Topic topic : topicList) {
                    JJHContent jjhContent = m.get(new jjhContentType(ContentConst.TOPIC_STUDY, topic.getId()));
                    if (jjhContent == null) {
                        JJHContent newJJHContent = new JJHContent(ContentConst.TOPIC_STUDY, idx++, jjhList, topic);
                        jjhContentRepository.save(newJJHContent);
                    }else{
                        jjhContent.updateNumber(idx++);
                    }
                }

                //3. 단원 마무리 문제 체크
                JJHContent jjhContent = m.get(new jjhContentType(ContentConst.CHAPTER_COMPLETE_QUESTION, chapter.getId()));
                if (jjhContent == null) {
                    JJHContent newJJHContent = new JJHContent(ContentConst.CHAPTER_COMPLETE_QUESTION,idx++, jjhList, chapter);
                    jjhContentRepository.save(newJJHContent);
                }else{
                    jjhContent.updateNumber(idx++);
                }

            } else if (timeline != null) {
                //4. 연표학습 체크
                JJHContent timelineJJHContent = m.get(new jjhContentType(ContentConst.TIMELINE_STUDY, timeline.getId()));
                if (timelineJJHContent == null) {
                    JJHContent newJJHContent = new JJHContent(ContentConst.TIMELINE_STUDY, idx++, jjhList, timeline);
                    jjhContentRepository.save(newJJHContent);
                }else{
                    timelineJJHContent.updateNumber(idx++);
                }
            }
        }

    }

    @Transactional
    public void updateProgress(Customer customer, ContentProgressUpdateDto dto) {
        Integer contentNumber = dto.getContentNumber();
        JJHContentProgress curProgress = jjhContentProgressRepository.queryJJHContentProgressWithJJHContent(customer,contentNumber - 1).orElseThrow(() -> {
            throw new CustomException(NOT_VALIDATE_CONTENT_NUMBER);
        });
        JJHContentProgress nextProgress = jjhContentProgressRepository.queryJJHContentProgressWithJJHContent(customer,contentNumber).orElseThrow(() -> {
            throw new CustomException(NOT_VALIDATE_CONTENT_NUMBER);
        });

        if (nextProgress.getState() != StateConst.LOCKED) return;

        ContentConst curContent = curProgress.getJjhContent().getContent();
        ContentConst nextContent = nextProgress.getJjhContent().getContent();

        if (checkJJHListEnd(curContent, nextContent)) {
            //jjhList간의 이동이 있는 경우
            //1. 현재 jjhListProgress의 progress를 완료로 변경
            JJHList curJJHList = curProgress.getJjhContent().getJjhList();
            JJHListProgress curListProgress = jjhListProgressRepository.findByCustomerAndJjhList(customer, curJJHList).orElseThrow(() -> {
                throw new CustomException(NOT_VALIDATE_CONTENT_NUMBER);
            });
            curListProgress.updateState(StateConst.COMPLETE);
            //2. 다음 jjhListProgress의 state를 open으로 변경, progress는 진행중으로 변경
            JJHList nextJJHList = nextProgress.getJjhContent().getJjhList();
            JJHListProgress nextListProgress = jjhListProgressRepository.findByCustomerAndJjhList(customer, nextJJHList).orElseThrow(() -> {
                throw new CustomException(NOT_VALIDATE_CONTENT_NUMBER);
            });
            nextListProgress.updateState(StateConst.IN_PROGRESS);

        }
        curProgress.updateState(StateConst.COMPLETE);
        nextProgress.updateState(StateConst.IN_PROGRESS);
    }

    private boolean checkJJHListEnd(ContentConst cur, ContentConst next) {
        if(cur.equals(ContentConst.CHAPTER_COMPLETE_QUESTION) && (
                        next.equals(ContentConst.TOPIC_STUDY) ||
                        next.equals(ContentConst.CHAPTER_INFO) ||
                        next.equals(ContentConst.TIMELINE_STUDY)
                )) return true;

        if (cur.equals(ContentConst.TIMELINE_STUDY) && (
                next.equals(ContentConst.TOPIC_STUDY) ||
                        next.equals(ContentConst.CHAPTER_INFO)
                )) return true;

        return false;
    }

    @Transactional(readOnly = true)
    public TotalProgressDto queryTotalProgress(Customer customer) {
        return jjhContentProgressRepository.queryTotalProgressDto(customer);
    }

    @Transactional(readOnly = true)
    public JJHListCustomerQueryDto queryJJHCustomerForFree() {
        List<ChapterJJHCustomerQueryDto> chapterList = chapterRepository.queryChaptersWithjjhList().stream()
                .map(c -> {
                    Integer jjhNumber = c.getJjhLists().get(0).getNumber();
                    if (jjhNumber < JJH_NUMBER_FREE_LIMIT) {
                        return new ChapterJJHCustomerQueryDto(c.getTitle(), c.getNumber(), StateConst.COMPLETE.getName(),
                                jjhNumber,c.getDateComment());
                    }else if (jjhNumber == JJH_NUMBER_FREE_LIMIT) {
                        return new ChapterJJHCustomerQueryDto(c.getTitle(), c.getNumber(), StateConst.IN_PROGRESS.getName(),
                                jjhNumber,c.getDateComment());
                    }else{
                        return new ChapterJJHCustomerQueryDto(c.getTitle(), c.getNumber(), StateConst.LOCKED.getName(),
                                jjhNumber,c.getDateComment());
                    }
                })
                .sorted(Comparator.comparing(ChapterJJHCustomerQueryDto::getJjhNumber))
                .collect(Collectors.toList());
        List<TimelineJJHCustomerQueryDto> timelineList = timelineRepository.queryTimelinesWithEraAndjjhList().stream()
                .map(t -> {
                    Integer jjhNumber = t.getJjhLists().get(0).getNumber();
                    if (jjhNumber < JJH_NUMBER_FREE_LIMIT) {
                        return new TimelineJJHCustomerQueryDto(t.getEra().getName(), t.getStartDate(), t.getEndDate(),
                                StateConst.COMPLETE.getName(), jjhNumber, t.getId(),t.getTitle());
                    }else if (jjhNumber == JJH_NUMBER_FREE_LIMIT) {
                        return new TimelineJJHCustomerQueryDto(t.getEra().getName(), t.getStartDate(), t.getEndDate(),
                                StateConst.IN_PROGRESS.getName(), jjhNumber, t.getId(),t.getTitle());
                    }else{
                        return new TimelineJJHCustomerQueryDto(t.getEra().getName(), t.getStartDate(), t.getEndDate(),
                                StateConst.LOCKED.getName(), jjhNumber, t.getId(), t.getTitle());
                    }
                    })
                .sorted(Comparator.comparing(TimelineJJHCustomerQueryDto::getJjhNumber))
                .collect(Collectors.toList());

        return new JJHListCustomerQueryDto(chapterList, timelineList);
    }




    @AllArgsConstructor
    @EqualsAndHashCode
    private class jjhListType{
        public Integer type;
        public Long id;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    private class jjhContentType{
        public ContentConst content;
        public Long id;
    }
}
