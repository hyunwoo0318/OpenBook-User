package Project.OpenBook.Domain.JJH;

import static Project.OpenBook.Constants.ErrorCode.INVALID_PARAMETER;
import static Project.OpenBook.Constants.ErrorCode.NOT_VALIDATE_CONTENT_NUMBER;
import static Project.OpenBook.Constants.JJHForFreeConst.JJH_NUMBER_FREE_LIMIT;

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
import Project.OpenBook.Domain.JJH.dto.ChapterJJHCustomerQueryDto;
import Project.OpenBook.Domain.JJH.dto.ContentProgressUpdateDto;
import Project.OpenBook.Domain.JJH.dto.JJHContentsTableQueryDto;
import Project.OpenBook.Domain.JJH.dto.JJHListCustomerQueryDto;
import Project.OpenBook.Domain.JJH.dto.TimelineJJHCustomerQueryDto;
import Project.OpenBook.Domain.JJH.dto.TotalProgressDto;
import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import Project.OpenBook.Domain.Timeline.Repo.TimelineRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Handler.Exception.CustomException;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JJHService {

    private final ChapterRepository chapterRepository;
    private final TimelineRepository timelineRepository;
    private final JJHListRepository jjhListRepository;
    private final JJHListProgressRepository jjhListProgressRepository;

    private final JJHContentRepository jjhContentRepository;
    private final JJHContentProgressRepository jjhContentProgressRepository;

    private final BookmarkService bookmarkService;

    @Transactional(readOnly = true)
    public JJHListCustomerQueryDto queryJJHCustomer(Customer customer) {

        List<ChapterJJHCustomerQueryDto> chapterList = new ArrayList<>();
        List<TimelineJJHCustomerQueryDto> timelineList = new ArrayList<>();

        List<JJHListProgress> jjhListProgressList =
                jjhListProgressRepository.queryJJHListProgressWithJJHList(customer);
        for (JJHListProgress progress : jjhListProgressList) {
            JJHList jjhList = progress.getJjhList();
            Chapter chapter = jjhList.getChapter();
            Timeline timeline = jjhList.getTimeline();

            if (chapter != null && timeline == null) {
                ChapterJJHCustomerQueryDto dto =
                        new ChapterJJHCustomerQueryDto(
                                chapter.getTitle(),
                                chapter.getNumber(),
                                progress.getState().getName(),
                                jjhList.getNumber(),
                                chapter.getDateComment());
                chapterList.add(dto);
            } else if (chapter == null && timeline != null) {
                TimelineJJHCustomerQueryDto dto =
                        new TimelineJJHCustomerQueryDto(
                                timeline.getEra().getName(),
                                timeline.getStartDate(),
                                timeline.getEndDate(),
                                progress.getState().getName(),
                                jjhList.getNumber(),
                                timeline.getId(),
                                timeline.getTitle());
                timelineList.add(dto);
            }
        }
        return new JJHListCustomerQueryDto(chapterList, timelineList);
    }

    @Transactional(readOnly = true)
    public List<JJHContentsTableQueryDto> queryJJHContentsTable(
            Customer customer, Integer jjhNumber) {

        JJHList jjhList =
                jjhListRepository
                        .queryJJHList(jjhNumber)
                        .orElseThrow(
                                () -> {
                                    throw new CustomException(INVALID_PARAMETER);
                                });

        List<JJHContentProgress> jjhContentProgressList =
                jjhContentProgressRepository.queryJJHContentProgressForCustomer(
                        customer, jjhNumber);

        /** 단원의 contents */
        if (jjhList.getChapter() != null) {
            return makeContentsTableForChapter(customer, jjhContentProgressList, jjhList);
        }
        /** timeline의 contents */
        else if (jjhList.getTimeline() != null) {
            return makeContentsTableForTimeline(customer, jjhContentProgressList, jjhList);
        } else {
            throw new CustomException(INVALID_PARAMETER);
        }
    }

    public List<JJHContentsTableQueryDto> queryJJHContentsTableForFree(Integer jjhNumber) {
        JJHList jjhList =
                jjhListRepository
                        .queryJJHList(jjhNumber)
                        .orElseThrow(
                                () -> {
                                    throw new CustomException(INVALID_PARAMETER);
                                });

        Chapter chapter = jjhList.getChapter();
        Timeline timeline = jjhList.getTimeline();
        if (chapter != null) {
            return makeContentsTableForChapterForFree(jjhList, chapter);
        } else if (timeline != null) {
            return makeContentsTableForTimelineForFree(jjhList, timeline);
        } else {
            throw new CustomException(INVALID_PARAMETER);
        }
    }

    private List<JJHContentsTableQueryDto> makeContentsTableForTimelineForFree(
            JJHList jjhList, Timeline timeline2) {
        List<JJHContentsTableQueryDto> dtoList = new ArrayList<>();
        List<JJHContent> jjhContentList = jjhList.getJjhContentList();

        for (JJHContent jjhContent : jjhContentList) {
            Timeline timeline = jjhContent.getTimeline();
            String title = timeline.getTitle();
            String category = null;
            String dateComment = null;

            JJHContentsTableQueryDto dto =
                    new JJHContentsTableQueryDto(
                            null,
                            title,
                            jjhContent.getContent().name(),
                            null,
                            jjhContent.getNumber(),
                            dateComment,
                            category);
            dtoList.add(dto);
        }

        return dtoList.stream()
                .sorted(Comparator.comparing(JJHContentsTableQueryDto::getContentNumber))
                .collect(Collectors.toList());
    }

    private List<JJHContentsTableQueryDto> makeContentsTableForChapterForFree(
            JJHList jjhList, Chapter chapter2) {
        List<JJHContentsTableQueryDto> dtoList = new ArrayList<>();

        List<JJHContent> jjhContentList = jjhList.getJjhContentList();

        for (JJHContent jjhContent : jjhContentList) {
            Chapter chapter = jjhContent.getChapter();
            Topic topic = jjhContent.getTopic();
            String title = "";
            String category = null;
            String dateComment = null;
            if (chapter != null) {
                title = chapter.getTitle();
                dateComment = chapter.getDateComment();
            } else if (topic != null) {
                title = topic.getTitle();
                category = topic.getQuestionCategory().getCategory().getName();
                dateComment = topic.getDateComment();
            }

            JJHContentsTableQueryDto dto =
                    new JJHContentsTableQueryDto(
                            null,
                            title,
                            jjhContent.getContent().name(),
                            StateConst.COMPLETE.getName(),
                            jjhContent.getNumber(),
                            dateComment,
                            category);
            dtoList.add(dto);
        }

        return dtoList.stream()
                .sorted(Comparator.comparing(JJHContentsTableQueryDto::getContentNumber))
                .collect(Collectors.toList());
    }

    private List<JJHContentsTableQueryDto> makeContentsTableForTimeline(
            Customer customer, List<JJHContentProgress> jjhContentProgressList, JJHList jjhList) {

        List<JJHContentsTableQueryDto> dtoList = new ArrayList<>();
        JJHContentProgress progress = jjhContentProgressList.get(0);
        JJHContent jjhContent = progress.getJjhContent();
        Timeline timeline = jjhContent.getTimeline();
        String title = timeline.getTitle();
        String category = null;
        String dateComment = null;

        JJHContentsTableQueryDto dto =
                new JJHContentsTableQueryDto(
                        null,
                        title,
                        jjhContent.getContent().name(),
                        progress.getState().getName(),
                        jjhContent.getNumber(),
                        dateComment,
                        category);
        dtoList.add(dto);

        return dtoList;
    }

    private List<JJHContentsTableQueryDto> makeContentsTableForChapter(
            Customer customer, List<JJHContentProgress> jjhContentProgressList, JJHList jjhList) {

        List<JJHContentsTableQueryDto> dtoList = new ArrayList<>();

        Map<JJHContent, JJHContentProgress> jjhContentMap =
                jjhContentProgressList.stream()
                        .collect(Collectors.toMap(JJHContentProgress::getJjhContent, c -> c));
        Map<Topic, Boolean> bookmarkMap =
                bookmarkService.queryBookmarks(customer, jjhList.getChapter().getTopicList());

        List<JJHContent> jjhContentList = jjhList.getJjhContentList();

        for (JJHContent jjhContent : jjhContentList) {
            Chapter chapter = jjhContent.getChapter();
            Topic topic = jjhContent.getTopic();
            String title = "";
            String category = null;
            String dateComment = null;
            if (chapter != null) {
                title = chapter.getTitle();
                dateComment = chapter.getDateComment();
            } else if (topic != null) {
                title = topic.getTitle();
                category = topic.getQuestionCategory().getCategory().getName();
                dateComment = topic.getDateComment();
            }

            JJHContentsTableQueryDto dto =
                    new JJHContentsTableQueryDto(
                            bookmarkMap.get(topic),
                            title,
                            jjhContent.getContent().name(),
                            jjhContentMap.get(jjhContent).getState().getName(),
                            jjhContent.getNumber(),
                            dateComment,
                            category);
            dtoList.add(dto);
        }

        dtoList.stream().sorted(Comparator.comparing(JJHContentsTableQueryDto::getContentNumber));
        return dtoList;

        //        for (JJHContentProgress progress : jjhContentProgressList) {
        //            JJHContent jjhContent = progress.getJjhContent();
        //            Chapter chapter = jjhContent.getChapter();
        //            Topic topic = jjhContent.getTopic();
        //            String title = "";
        //            String category = null;
        //            String dateComment = null;
        //            if (chapter != null) {
        //                title = chapter.getTitle();
        //                dateComment = chapter.getDateComment();
        //            } else if (topic != null) {
        //                title = topic.getTitle();
        //                category = topic.getQuestionCategory().getCategory().getName();
        //                dateComment = topic.getDateComment();
        //            }
        //
        //            JJHContentsTableQueryDto dto = new
        // JJHContentsTableQueryDto(bookmarkMap.get(topic),
        //                title, jjhContent.getContent().name(),
        //                progress.getState().getName(), jjhContent.getNumber(), dateComment,
        // category);
        //            dtoList.add(dto);
        //        }
        //
        //
        // dtoList.stream().sorted(Comparator.comparing(JJHContentsTableQueryDto::getContentNumber));
        //        return dtoList;
    }

    public void updateJJHContent() {
        Integer idx = 1;

        Map<jjhContentType, JJHContent> m = new HashMap<>();
        List<JJHContent> jjhContents = jjhContentRepository.queryJJHContents();
        for (JJHContent jjhContent : jjhContents) {
            if (jjhContent.getTopic() != null) {
                m.put(
                        new jjhContentType(jjhContent.getContent(), jjhContent.getTopic().getId()),
                        jjhContent);
            } else if (jjhContent.getTimeline() != null) {
                m.put(
                        new jjhContentType(
                                jjhContent.getContent(), jjhContent.getTimeline().getId()),
                        jjhContent);
            } else if (jjhContent.getChapter() != null) {
                m.put(
                        new jjhContentType(
                                jjhContent.getContent(), jjhContent.getChapter().getId()),
                        jjhContent);
            }
        }
        List<JJHList> jjhLists =
                jjhListRepository.queryJJHListsWithChapterAndTimeline().stream()
                        .sorted(Comparator.comparing(JJHList::getNumber))
                        .collect(Collectors.toList());
        for (JJHList jjhList : jjhLists) {
            Chapter chapter = jjhList.getChapter();
            Timeline timeline = jjhList.getTimeline();
            if (chapter != null) {
                // 1. 단원학습 체크
                JJHContent chapterInfoJJHContent =
                        m.get(new jjhContentType(ContentConst.CHAPTER_INFO, chapter.getId()));
                if (chapter.getContent() != null) {
                    if (chapterInfoJJHContent == null) {
                        JJHContent newJJHContent =
                                new JJHContent(ContentConst.CHAPTER_INFO, idx++, jjhList, chapter);
                        jjhContentRepository.save(newJJHContent);
                    } else {
                        chapterInfoJJHContent.updateNumber(idx++);
                    }
                } else {
                    if (chapterInfoJJHContent != null) {
                        jjhContentRepository.delete(chapterInfoJJHContent);
                    }
                }

                // 2. 단원 내 토픽들 체크
                List<Topic> topicList = chapter.getTopicList();
                for (Topic topic : topicList) {
                    JJHContent jjhContent =
                            m.get(new jjhContentType(ContentConst.TOPIC_STUDY, topic.getId()));
                    if (jjhContent == null) {
                        JJHContent newJJHContent =
                                new JJHContent(ContentConst.TOPIC_STUDY, idx++, jjhList, topic);
                        jjhContentRepository.save(newJJHContent);
                    } else {
                        jjhContent.updateNumber(idx++);
                    }
                }

                // 3. 단원 마무리 문제 체크
                JJHContent jjhContent =
                        m.get(
                                new jjhContentType(
                                        ContentConst.CHAPTER_COMPLETE_QUESTION, chapter.getId()));
                if (jjhContent == null) {
                    JJHContent newJJHContent =
                            new JJHContent(
                                    ContentConst.CHAPTER_COMPLETE_QUESTION,
                                    idx++,
                                    jjhList,
                                    chapter);
                    jjhContentRepository.save(newJJHContent);
                } else {
                    jjhContent.updateNumber(idx++);
                }

            } else if (timeline != null) {
                // 4. 연표학습 체크
                JJHContent timelineJJHContent =
                        m.get(new jjhContentType(ContentConst.TIMELINE_STUDY, timeline.getId()));
                if (timelineJJHContent == null) {
                    JJHContent newJJHContent =
                            new JJHContent(ContentConst.TIMELINE_STUDY, idx++, jjhList, timeline);
                    jjhContentRepository.save(newJJHContent);
                } else {
                    timelineJJHContent.updateNumber(idx++);
                }
            }
        }
    }

    @Transactional
    public void updateProgress(Customer customer, ContentProgressUpdateDto dto) {
        Integer contentNumber = dto.getContentNumber();
        JJHContentProgress curProgress =
                jjhContentProgressRepository
                        .queryJJHContentProgressWithJJHContent(customer, contentNumber - 1)
                        .orElseThrow(
                                () -> {
                                    throw new CustomException(NOT_VALIDATE_CONTENT_NUMBER);
                                });
        JJHContentProgress nextProgress =
                jjhContentProgressRepository
                        .queryJJHContentProgressWithJJHContent(customer, contentNumber)
                        .orElseThrow(
                                () -> {
                                    throw new CustomException(NOT_VALIDATE_CONTENT_NUMBER);
                                });

        if (nextProgress.getState() != StateConst.LOCKED) {
            return;
        }

        ContentConst curContent = curProgress.getJjhContent().getContent();
        ContentConst nextContent = nextProgress.getJjhContent().getContent();

        if (checkJJHListEnd(curContent, nextContent)) {
            // jjhList간의 이동이 있는 경우
            // 1. 현재 jjhListProgress의 progress를 완료로 변경
            JJHList curJJHList = curProgress.getJjhContent().getJjhList();
            JJHListProgress curListProgress =
                    jjhListProgressRepository
                            .findByCustomerAndJjhList(customer, curJJHList)
                            .orElseThrow(
                                    () -> {
                                        throw new CustomException(NOT_VALIDATE_CONTENT_NUMBER);
                                    });
            curListProgress.updateState(StateConst.COMPLETE);
            // 2. 다음 jjhListProgress의 state를 open으로 변경, progress는 진행중으로 변경
            JJHList nextJJHList = nextProgress.getJjhContent().getJjhList();
            JJHListProgress nextListProgress =
                    jjhListProgressRepository
                            .findByCustomerAndJjhList(customer, nextJJHList)
                            .orElseThrow(
                                    () -> {
                                        throw new CustomException(NOT_VALIDATE_CONTENT_NUMBER);
                                    });
            nextListProgress.updateState(StateConst.IN_PROGRESS);
        }
        curProgress.updateState(StateConst.COMPLETE);
        nextProgress.updateState(StateConst.IN_PROGRESS);
    }

    private boolean checkJJHListEnd(ContentConst cur, ContentConst next) {
        if (cur.equals(ContentConst.CHAPTER_COMPLETE_QUESTION)) {
            return true;
        }

        if (cur.equals(ContentConst.TIMELINE_STUDY)) {
            return true;
        }

        return false;
    }

    @Transactional(readOnly = true)
    public TotalProgressDto queryTotalProgress(Customer customer) {
        return jjhContentProgressRepository.queryTotalProgressDto(customer);
    }

    @Transactional(readOnly = true)
    public JJHListCustomerQueryDto queryJJHCustomerForFree() {
        List<ChapterJJHCustomerQueryDto> chapterList =
                chapterRepository.queryChaptersWithjjhList().stream()
                        .map(
                                c -> {
                                    Integer jjhNumber = c.getJjhLists().get(0).getNumber();
                                    if (jjhNumber < JJH_NUMBER_FREE_LIMIT) {
                                        return new ChapterJJHCustomerQueryDto(
                                                c.getTitle(),
                                                c.getNumber(),
                                                StateConst.COMPLETE.getName(),
                                                jjhNumber,
                                                c.getDateComment());
                                    } else if (jjhNumber == JJH_NUMBER_FREE_LIMIT) {
                                        return new ChapterJJHCustomerQueryDto(
                                                c.getTitle(),
                                                c.getNumber(),
                                                StateConst.IN_PROGRESS.getName(),
                                                jjhNumber,
                                                c.getDateComment());
                                    } else {
                                        return new ChapterJJHCustomerQueryDto(
                                                c.getTitle(),
                                                c.getNumber(),
                                                StateConst.LOCKED.getName(),
                                                jjhNumber,
                                                c.getDateComment());
                                    }
                                })
                        .sorted(Comparator.comparing(ChapterJJHCustomerQueryDto::getJjhNumber))
                        .collect(Collectors.toList());
        List<TimelineJJHCustomerQueryDto> timelineList =
                timelineRepository.queryTimelinesWithEraAndjjhList().stream()
                        .map(
                                t -> {
                                    Integer jjhNumber = t.getJjhLists().get(0).getNumber();
                                    if (jjhNumber < JJH_NUMBER_FREE_LIMIT) {
                                        return new TimelineJJHCustomerQueryDto(
                                                t.getEra().getName(),
                                                t.getStartDate(),
                                                t.getEndDate(),
                                                StateConst.COMPLETE.getName(),
                                                jjhNumber,
                                                t.getId(),
                                                t.getTitle());
                                    } else if (jjhNumber == JJH_NUMBER_FREE_LIMIT) {
                                        return new TimelineJJHCustomerQueryDto(
                                                t.getEra().getName(),
                                                t.getStartDate(),
                                                t.getEndDate(),
                                                StateConst.IN_PROGRESS.getName(),
                                                jjhNumber,
                                                t.getId(),
                                                t.getTitle());
                                    } else {
                                        return new TimelineJJHCustomerQueryDto(
                                                t.getEra().getName(),
                                                t.getStartDate(),
                                                t.getEndDate(),
                                                StateConst.LOCKED.getName(),
                                                jjhNumber,
                                                t.getId(),
                                                t.getTitle());
                                    }
                                })
                        .sorted(Comparator.comparing(TimelineJJHCustomerQueryDto::getJjhNumber))
                        .collect(Collectors.toList());

        return new JJHListCustomerQueryDto(chapterList, timelineList);
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    private class jjhContentType {

        public ContentConst content;
        public Long id;
    }
}
