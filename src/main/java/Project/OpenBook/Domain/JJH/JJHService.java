package Project.OpenBook.Domain.JJH;

import Project.OpenBook.Constants.ContentConst;
import Project.OpenBook.Constants.ProgressConst;
import Project.OpenBook.Constants.StateConst;
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
import Project.OpenBook.Domain.TimeLine.Timeline;
import Project.OpenBook.Domain.TimeLine.TimelineRepository;
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

@Service
@RequiredArgsConstructor
public class JJHService {

    private final ChapterRepository chapterRepository;
    private final TimelineRepository timelineRepository;
    private final JJHListRepository jjhListRepository;
    private final JJHListProgressRepository jjhListProgressRepository;

    private final JJHContentRepository jjhContentRepository;
    private final JJHContentProgressRepository jjhContentProgressRepository;

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
                    return new TimelineJJHAdminQueryDto(t.getEra().getName(), t.getStartDate(), t.getEndDate(), jjhNumber, t.getId());
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
                        progress.getState().getName(), progress.getProgress().getName(), jjhList.getNumber());
                chapterList.add(dto);
            } else if (chapter == null && timeline != null) {
                TimelineJJHCustomerQueryDto dto = new TimelineJJHCustomerQueryDto(timeline.getEra().getName(), timeline.getStartDate(), timeline.getEndDate(),
                        progress.getState().getName(), progress.getProgress().getName(), jjhList.getNumber(), timeline.getId());
                timelineList.add(dto);
            }
        }
        return new JJHListCustomerQueryDto(chapterList, timelineList);
    }

    @Transactional(readOnly = true)
    public List<JJHContentsTableQueryDto> queryJJHContentsTable(Customer customer, Integer jjhNumber) {
        List<JJHContentsTableQueryDto> dtoList = new ArrayList<>();

        List<JJHContentProgress> jjhContentProgressList
                = jjhContentProgressRepository.queryJJHContentProgressForCustomer(customer,jjhNumber);

        for (JJHContentProgress progress : jjhContentProgressList) {
            JJHContent jjhContent = progress.getJjhContent();
            Chapter chapter = jjhContent.getChapter();
            Timeline timeline = jjhContent.getTimeline();
            Topic topic = jjhContent.getTopic();
            String title = "";
            if (chapter != null) {
                title = chapter.getTitle();
            } else if (timeline != null) {
                Integer startDate = timeline.getStartDate();
                Integer endDate = timeline.getEndDate();
                String eraName = timeline.getEra().getName();
                title = eraName + " ( " + startDate + " ~ " + endDate + " ) ";
            } else if (topic != null) {
                title = topic.getTitle();
            }

            JJHContentsTableQueryDto dto = new JJHContentsTableQueryDto(title, jjhContent.getContent().name(),
                    progress.getState().getName(), jjhContent.getNumber());
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
                if (chapter.getContent() != null) {
                    JJHContent jjhContent = m.get(new jjhContentType(ContentConst.CHAPTER_INFO, chapter.getId()));
                    if (jjhContent == null) {
                        JJHContent newJJHContent = new JJHContent(ContentConst.CHAPTER_INFO,idx++, jjhList, chapter);
                        jjhContentRepository.save(newJJHContent);
                    }else{
                        jjhContent.updateNumber(idx++);
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
                JJHContent jjhContent = m.get(new jjhContentType(ContentConst.TIMELINE_STUDY, timeline.getId()));
                if (jjhContent == null) {
                    JJHContent newJJHContent = new JJHContent(ContentConst.TIMELINE_STUDY, idx++, jjhList, timeline);
                    jjhContentRepository.save(newJJHContent);
                }else{
                    jjhContent.updateNumber(idx++);
                }

                //5. 연표문제 체크
                JJHContent jjhContent2 = m.get(new jjhContentType(ContentConst.TIMELINE_QUESTION, timeline.getId()));
                if (jjhContent2 == null) {
                    JJHContent newJJHContent = new JJHContent(ContentConst.TIMELINE_QUESTION, idx++, jjhList, timeline);
                    jjhContentRepository.save(newJJHContent);
                }else{
                    jjhContent2.updateNumber(idx++);
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

        if (nextProgress.getState() == StateConst.OPEN) return;

        ContentConst curContent = curProgress.getJjhContent().getContent();
        ContentConst nextContent = nextProgress.getJjhContent().getContent();

        if (checkJJHListEnd(curContent, nextContent)) {
            //jjhList간의 이동이 있는 경우
            //1. 현재 jjhListProgress의 progress를 완료로 변경
            JJHList curJJHList = curProgress.getJjhContent().getJjhList();
            JJHListProgress curListProgress = jjhListProgressRepository.findByCustomerAndJjhList(customer, curJJHList).orElseThrow(() -> {
                throw new CustomException(NOT_VALIDATE_CONTENT_NUMBER);
            });
            curListProgress.updateProgress(ProgressConst.COMPLETED);
            //2. 다음 jjhListProgress의 state를 open으로 변경, progress는 진행중으로 변경
            JJHList nextJJHList = nextProgress.getJjhContent().getJjhList();
            JJHListProgress nextListProgress = jjhListProgressRepository.findByCustomerAndJjhList(customer, nextJJHList).orElseThrow(() -> {
                throw new CustomException(NOT_VALIDATE_CONTENT_NUMBER);
            });
            nextListProgress.updateProgressState(StateConst.OPEN, ProgressConst.IN_PROGRESS);

        }
        nextProgress.updateState(StateConst.OPEN);
    }

    private boolean checkJJHListEnd(ContentConst cur, ContentConst next) {
        if(cur.equals(ContentConst.CHAPTER_COMPLETE_QUESTION) && (
                        next.equals(ContentConst.TOPIC_STUDY) ||
                        next.equals(ContentConst.CHAPTER_INFO) ||
                        next.equals(ContentConst.TIMELINE_STUDY)
                )) return true;

        if (cur.equals(ContentConst.TIMELINE_QUESTION) && (
                next.equals(ContentConst.TOPIC_STUDY) ||
                        next.equals(ContentConst.CHAPTER_INFO)
                )) return true;

        return false;
    }

    @Transactional(readOnly = true)
    public TotalProgressDto queryTotalProgress(Customer customer) {
        return jjhContentProgressRepository.queryTotalProgressDto(customer);
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
