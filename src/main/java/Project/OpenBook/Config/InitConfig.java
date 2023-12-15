package Project.OpenBook.Config;

import Project.OpenBook.Constants.KeywordUsageConst;
import Project.OpenBook.Domain.Category.Repository.CategoryRepository;
import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Domain.Choice.Repository.ChoiceRepository;
import Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword.ChoiceKeyword;
import Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword.ChoiceKeywordRepository;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
import Project.OpenBook.Domain.Customer.Service.CustomerService;
import Project.OpenBook.Domain.Description.Repository.DescriptionRepository;
import Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword.DescriptionKeyword;
import Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword.DescriptionKeywordRepository;
import Project.OpenBook.Domain.Era.EraRepository;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.ExamQuestion.Repo.ExamQuestionRepository;
import Project.OpenBook.Domain.JJH.JJHContent.JJHContent;
import Project.OpenBook.Domain.JJH.JJHContent.JJHContentRepository;
import Project.OpenBook.Domain.JJH.JJHContentProgress.JJHContentProgress;
import Project.OpenBook.Domain.JJH.JJHContentProgress.JJHContentProgressRepository;
import Project.OpenBook.Domain.JJH.JJHList.JJHList;
import Project.OpenBook.Domain.JJH.JJHList.JJHListRepository;
import Project.OpenBook.Domain.JJH.JJHListProgress.JJHListProgress;
import Project.OpenBook.Domain.JJH.JJHListProgress.JJHListProgressRepository;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Domain.KeywordPrimaryDate;
import Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Repository.KeywordPrimaryDateRepository;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Domain.ExamQuestionLearningRecord;
import Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Repository.ExamQuestionLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Repo.KeywordLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Domain.QuestionCategoryLearningRecord;
import Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Repo.QuestionCategoryLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.RoundLearningRecord.RoundLearningRecord;
import Project.OpenBook.Domain.LearningRecord.RoundLearningRecord.RoundLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.TimelineLearningRecord.Domain.TimelineLearningRecord;
import Project.OpenBook.Domain.LearningRecord.TimelineLearningRecord.Repo.TimelineLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain.TopicLearningRecord;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Repo.TopicLearningRecordRepository;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.QuestionCategory.Repo.QuestionCategoryRepository;
import Project.OpenBook.Domain.Round.Domain.Round;
import Project.OpenBook.Domain.Round.Repo.RoundRepository;
import Project.OpenBook.Domain.Search.ChapterSearch.ChapterSearch;
import Project.OpenBook.Domain.Search.ChapterSearch.ChapterSearchRepository;
import Project.OpenBook.Domain.Search.KeywordSearch.KeywordSearch;
import Project.OpenBook.Domain.Search.KeywordSearch.KeywordSearchRepository;
import Project.OpenBook.Domain.Search.TopicSearch.TopicSearch;
import Project.OpenBook.Domain.Search.TopicSearch.TopicSearchRepository;
import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import Project.OpenBook.Domain.Timeline.Repo.TimelineRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Topic.TopicPrimaryDate.Domain.TopicPrimaryDate;
import Project.OpenBook.Domain.Topic.TopicPrimaryDate.Repository.TopicPrimaryDateRepository;
import Project.OpenBook.Image.ImageService;
import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class InitConfig {

    private final CustomerService customerService;

    private final TopicSearchRepository topicSearchRepository;
    private final TopicRepository topicRepository;
    private final TopicLearningRecordRepository topicLearningRecordRepository;
    private final ChapterRepository chapterRepository;
    private final ChapterSearchRepository chapterSearchRepository;

    private final KeywordRepository keywordRepository;
    private final KeywordSearchRepository keywordSearchRepository;
    private final KeywordLearningRecordRepository keywordLearningRecordRepository;
    private final QuestionCategoryLearningRecordRepository questionCategoryLearningRecordRepository;

    private final TimelineRepository timelineRepository;
    private final TimelineLearningRecordRepository timelineLearningRecordRepository;
    private final KeywordPrimaryDateRepository keywordPrimaryDateRepository;
    private final TopicPrimaryDateRepository topicPrimaryDateRepository;

    private final JJHListRepository jjhListRepository;
    private final JJHContentRepository jjhContentRepository;

    private final QuestionCategoryRepository questionCategoryRepository;
    private final EraRepository eraRepository;
    private final CategoryRepository categoryRepository;

    private final DescriptionRepository descriptionRepository;
    private final ChoiceRepository choiceRepository;
    private final ImageService imageService;

    private final JJHListProgressRepository jjhListProgressRepository;
    private final JJHContentProgressRepository jjhContentProgressRepository;

    private final RoundRepository roundRepository;
    private final RoundLearningRecordRepository roundLearningRecordRepository;

    private final ChoiceKeywordRepository choiceKeywordRepository;
    private final DescriptionKeywordRepository descriptionKeywordRepository;

    private final ExamQuestionLearningRecordRepository examQuestionLearningRecordRepository;
    private final ExamQuestionRepository examQuestionRepository;

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    private final AmazonS3Client amazonS3;



    /**
     * ElasticSearch를 위한 init
     * 각 topic의 title, id를 저장
     */
    @Bean
    public void initElasticSearchIndex() {
        chapterSearchRepository.deleteAll();
        topicSearchRepository.deleteAll();
        keywordSearchRepository.deleteAll();

        List<ChapterSearch> chapterSearchList = chapterRepository.findAll().stream()
                .map(ChapterSearch::new)
                .collect(Collectors.toList());

        List<TopicSearch> topicSearchList = topicRepository.queryTopicsWithChapter().stream()
                .map(TopicSearch::new)
                .collect(Collectors.toList());

        List<KeywordSearch> keywordSearchList = keywordRepository.queryKeywordsWithChapter().stream()
                .map(KeywordSearch::new)
                .collect(Collectors.toList());

        chapterSearchRepository.saveAll(chapterSearchList);
        topicSearchRepository.saveAll(topicSearchList);
        keywordSearchRepository.saveAll(keywordSearchList);

    }

    /**
     * 연표 속해있는 날짜 개수 세팅
     */
    @Bean
    public void initTimelineCounts() {
        List<Timeline> timelineList = timelineRepository.queryAllForInit();
        List<KeywordPrimaryDate> keywordPrimaryDateList = keywordPrimaryDateRepository.queryAllForInit();
        List<TopicPrimaryDate> topicPrimaryDateList = topicPrimaryDateRepository.queryAllForInit();
        for (TopicPrimaryDate date : topicPrimaryDateList) {
            for (Timeline timeline : timelineList) {
                if(timeline.getEra() == date.getTopic().getQuestionCategory().getEra() &&
                    timeline.getStartDate() <= date.getExtraDate() &&
                    timeline.getEndDate() >= date.getExtraDate()){
                    timeline.updateCount();
                    break;
                }
            }
        }

        for (KeywordPrimaryDate date : keywordPrimaryDateList) {
            for (Timeline timeline : timelineList) {
                if(timeline.getEra() == date.getKeyword().getTopic().getQuestionCategory().getEra() &&
                        timeline.getStartDate() <= date.getExtraDate() &&
                        timeline.getEndDate() >= date.getExtraDate()){
                    timeline.updateCount();
                    break;
                }
            }
        }

    }

    /**
     * 기본 관리자 아이디 세팅
     */
//    @Bean
//    public void initAdmin(){
//        if(customerRepository.findByNickName("admin1").isEmpty()){
//            Customer admin1 = new Customer("admin1", passwordEncoder.encode("admin1"), Role.ADMIN);
//            Customer admin2 = new Customer("admin2", passwordEncoder.encode("admin2"), Role.ADMIN);
//            customerRepository.saveAll(Arrays.asList(admin1, admin2));
//        }
//    }

    /**
     * 문제 분석 로직
     */

    @Bean
    @Transactional
    public void initQuestionInfo() {
        //각 문제별 keywordList 생성
        Map<Long, List<Keyword>> choiceKeywordMap = choiceKeywordRepository.queryChoiceKeywordsForInit().stream()
                .map(ChoiceKeyword::getKeyword)
                .collect(Collectors.groupingBy(Keyword::getId));
        Map<Long, List<Keyword>> descriptionKeywordMap = descriptionKeywordRepository.queryDescriptionKeywordsForInit().stream()
                .map(DescriptionKeyword::getKeyword)
                .collect(Collectors.groupingBy(Keyword::getId));
        Map<Long, List<Keyword>> newKeywordMap = new HashMap<>(choiceKeywordMap);
        for (Long key : descriptionKeywordMap.keySet()) {
            List<Keyword> descKeywordList = descriptionKeywordMap.get(key);
            List<Keyword> keywordList = newKeywordMap.get(key);
            if (keywordList == null) {
                newKeywordMap.put(key, descKeywordList);
            }else{
                keywordList.addAll(descKeywordList);
                newKeywordMap.put(key, keywordList);
            }
        }

        //보기/선지 출현 빈도 저장 로직
        initKeywordUsageCounts(newKeywordMap);

        initScore();

        //키워드간 연관성 저장 로직
        //initKeywordAssociations(newKeywordMap);
    }

    /**
     * 추가된 내용에 대한 기존 회원에 대한 record 생성
     */

    @Bean
    @Transactional
    public void checkRecords() {
        List<Customer> customerList = customerRepository.findAll();


        List<JJHList> jjhLists = jjhListRepository.findAll();
        List<JJHContent> jjhContents = jjhContentRepository.findAll();
        List<Topic> topicList = topicRepository.findAll();
        List<Keyword> keywordList = keywordRepository.findAll();
        List<QuestionCategory> questionCategoryList = questionCategoryRepository.findAll();
        List<Round> roundList = roundRepository.findAll();
        List<ExamQuestion> examQuestionList = examQuestionRepository.findAll();
        List<Timeline> timelineList = timelineRepository.findAll();

        Map<Customer, List<JJHListProgress>> jjhListProgressMap = jjhListProgressRepository.queryAllJJHList().stream()
                .collect(Collectors.groupingBy(JJHListProgress::getCustomer));
        Map<Customer, List<JJHContentProgress>> jjhContentProgressMap = jjhContentProgressRepository.findAll().stream()
                .collect(Collectors.groupingBy(JJHContentProgress::getCustomer));
        Map<Customer, List<TopicLearningRecord>> topicProgressMap = topicLearningRecordRepository.findAll().stream()
                .collect(Collectors.groupingBy(TopicLearningRecord::getCustomer));
        Map<Customer, List<KeywordLearningRecord>> keywordProgressMap = keywordLearningRecordRepository.findAll().stream()
                .collect(Collectors.groupingBy(KeywordLearningRecord::getCustomer));
        Map<Customer, List<QuestionCategoryLearningRecord>> qcProgressMap = questionCategoryLearningRecordRepository.findAll().stream()
                .collect(Collectors.groupingBy(QuestionCategoryLearningRecord::getCustomer));
        Map<Customer, List<RoundLearningRecord>> roundProgressMap = roundLearningRecordRepository.findAll().stream()
                .collect(Collectors.groupingBy(RoundLearningRecord::getCustomer));
        Map<Customer, List<ExamQuestionLearningRecord>> examQuestionProgressMap = examQuestionLearningRecordRepository.findAll().stream()
                .collect(Collectors.groupingBy(ExamQuestionLearningRecord::getCustomer));
        Map<Customer, List<TimelineLearningRecord>> timelineProgressMap = timelineLearningRecordRepository.findAll().stream()
                .collect(Collectors.groupingBy(TimelineLearningRecord::getCustomer));

        for (Customer customer : customerList) {
            //1. jjhList
            List<JJHListProgress> jjhListProgressList = jjhListProgressMap.get(customer);
            Map<JJHList, JJHListProgress> jjhListCustomerProgressMap = new HashMap<>();
            if (jjhListProgressList != null) {
                jjhListCustomerProgressMap = jjhListProgressList.stream()
                        .collect(Collectors.toMap(JJHListProgress::getJjhList, j -> j));
            }


            for (JJHList j : jjhLists) {
                JJHListProgress progress = jjhListCustomerProgressMap.get(j);
                if (progress == null) {
                    JJHListProgress newProgress = new JJHListProgress(customer, j);
                    jjhListProgressRepository.save(newProgress);
                }
            }

            //2.jjhContent
            List<JJHContentProgress> jjhContentProgressList = jjhContentProgressMap.get(customer);
            Map<JJHContent, JJHContentProgress> jjhContentCustomerProgressMap = new HashMap<>();
            if (jjhContentProgressList != null) {
                jjhContentCustomerProgressMap = jjhContentProgressList.stream()
                        .collect(Collectors.toMap(JJHContentProgress::getJjhContent, j -> j));
            }


            for (JJHContent j : jjhContents) {
                JJHContentProgress progress = jjhContentCustomerProgressMap.get(j);
                if (progress == null) {
                    JJHContentProgress newProgress = new JJHContentProgress(customer, j);
                    jjhContentProgressRepository.save(newProgress);
                }
            }

            //3. topic
            List<TopicLearningRecord> topicRecordList = topicProgressMap.get(customer);
            Map<Topic, TopicLearningRecord> topicCustomerRecordMap = new HashMap<>();
            if (topicRecordList != null) {
                topicCustomerRecordMap = topicRecordList.stream()
                        .collect(Collectors.toMap(TopicLearningRecord::getTopic, t -> t));
            }

            for (Topic t : topicList) {
                TopicLearningRecord record = topicCustomerRecordMap.get(t);
                if (record == null) {
                    TopicLearningRecord newRecord = new TopicLearningRecord(t, customer);
                    topicLearningRecordRepository.save(newRecord);
                }
            }

            //4. Keyword
            List<KeywordLearningRecord> keywordRecordList = keywordProgressMap.get(customer);
            Map<Keyword, KeywordLearningRecord> keywordLearningRecordMap = new HashMap<>();
            if (keywordRecordList != null) {
                keywordLearningRecordMap = keywordRecordList.stream()
                        .collect(Collectors.toMap(KeywordLearningRecord::getKeyword, k -> k));
            }

            for (Keyword k : keywordList) {
                KeywordLearningRecord record = keywordLearningRecordMap.get(k);
                if (record == null) {
                    KeywordLearningRecord newRecord = new KeywordLearningRecord(k, customer);
                    keywordLearningRecordRepository.save(newRecord);
                }
            }

            //5. Timeline
            List<TimelineLearningRecord> timelineRecordList = timelineProgressMap.get(customer);
            Map<Timeline, TimelineLearningRecord> timelineLearningRecordMap = new HashMap<>();
            if (timelineRecordList != null) {
                timelineLearningRecordMap = timelineRecordList.stream()
                        .collect(Collectors.toMap(TimelineLearningRecord::getTimeline, t -> t));
            }

            for (Timeline t : timelineList) {
                TimelineLearningRecord record = timelineLearningRecordMap.get(t);
                if (record == null) {
                    TimelineLearningRecord newRecord = new TimelineLearningRecord(t, customer);
                    timelineLearningRecordRepository.save(newRecord);
                }
            }

            //6. QuestionCategory
            List<QuestionCategoryLearningRecord> qcLearningRecordList = qcProgressMap.get(customer);
            Map<QuestionCategory, QuestionCategoryLearningRecord> qcLearningRecordMap = new HashMap<>();
            if (qcLearningRecordList != null) {
                qcLearningRecordMap = qcLearningRecordList.stream()
                        .collect(Collectors.toMap(QuestionCategoryLearningRecord::getQuestionCategory, qc -> qc));
            }

            for (QuestionCategory qc : questionCategoryList) {
                QuestionCategoryLearningRecord record = qcLearningRecordMap.get(qc);
                if (record == null) {
                    QuestionCategoryLearningRecord newRecord = new QuestionCategoryLearningRecord(qc, customer);
                    questionCategoryLearningRecordRepository.save(newRecord);
                }
            }

            //7. Round
            List<RoundLearningRecord> roundRecordList = roundProgressMap.get(customer);
            Map<Round, RoundLearningRecord> roundLearningRecordMap = new HashMap<>();
            if (roundRecordList != null) {
                roundLearningRecordMap = roundRecordList.stream()
                        .collect(Collectors.toMap(RoundLearningRecord::getRound, r -> r));
            }

            for (Round r : roundList) {
                RoundLearningRecord record = roundLearningRecordMap.get(r);
                if (record == null) {
                    RoundLearningRecord newRecord = new RoundLearningRecord(r, customer);
                    roundLearningRecordRepository.save(newRecord);
                }
            }

            //8. ExamQuestion
            List<ExamQuestionLearningRecord> examQuestionRecordList = examQuestionProgressMap.get(customer);
            Map<ExamQuestion, ExamQuestionLearningRecord> examQuestionLearningRecordMap = new HashMap<>();
            if (examQuestionList != null) {
                examQuestionLearningRecordMap = examQuestionRecordList.stream()
                        .collect(Collectors.toMap(ExamQuestionLearningRecord::getExamQuestion, e -> e));
            }

            for (ExamQuestion ex : examQuestionList) {
                ExamQuestionLearningRecord record = examQuestionLearningRecordMap.get(ex);
                if (record == null) {
                    ExamQuestionLearningRecord newRecord = new ExamQuestionLearningRecord(customer, ex);
                    examQuestionLearningRecordRepository.save(newRecord);
                }
            }

        }
    }

    private void initKeywordUsageCounts(Map<Long, List<Keyword>> newKeywordMap) {
        Map<Keyword, Integer> keywordCountMap = new HashMap<>();

        for (Long key : newKeywordMap.keySet()) {
            List<Keyword> keywordList = newKeywordMap.get(key);
            for (Keyword keyword : keywordList) {
                Integer count = keywordCountMap.get(keyword);
                if (count == null) {
                    keywordCountMap.put(keyword, 1);
                }else{
                    keywordCountMap.put(keyword, count + 1);
                }
            }
        }

        for (Keyword keyword : keywordCountMap.keySet()) {
            Integer count = keywordCountMap.get(keyword);
            keyword.updateCount(KeywordUsageConst.getKeywordProb(count));
        }
    }


    private void initScore() {
//        Map<Customer, List<KeywordLearningRecord>> customerKeywordRecordMap = keywordLearningRecordRepository.queryAllForInit().stream()
//                .collect(Collectors.groupingBy(record -> record.getCustomer()));
        Map<QuestionCategory, List<Keyword>> qcKeywordMap = keywordRepository.queryKeywordsForInit().stream()
                .collect(Collectors.groupingBy(k -> k.getTopic().getQuestionCategory()));
//        Map<Customer, List<QuestionCategoryLearningRecord>> customerQCRecordMap = questionCategoryLearningRecordRepository.queryQuestionRecordsForInit().stream()
//                .collect(Collectors.groupingBy(record -> record.getCustomer()));

        for (QuestionCategory questionCategory : qcKeywordMap.keySet()) {
            List<Keyword> keywordList = qcKeywordMap.get(questionCategory);
            int totalQuestionProb = 0;
            for (Keyword keyword : keywordList) {
                totalQuestionProb += keyword.getQuestionProb();
            }
            questionCategory.updateTotalQuestionProb(totalQuestionProb);
        }


    }

//    @Bean
//    public void resizeImage() throws IOException {
//        List<Description> descriptionList = descriptionRepository.findAll();
//        List<Choice> choiceList = choiceRepository.findAll();
//        for (Description description : descriptionList) {
//            String content = description.getContent();
//            if (content != null) {
//                String[] splitDot = content.split("[.]");
//                String ext = splitDot[splitDot.length-1];
//
//                String[] splitSlash = content.split("[/]");
//                String fileName = splitSlash[splitSlash.length-1];
//                byte[] bytes = imageService.downloadFile(fileName);
//                imageService.deleteFile(fileName);
//                InputStream inputStream = imageService.resizePicture(ext, bytes);
//                String newName = content + "/v2";
//                imageService.saveImage(newName, inputStream);
//                description.updateContent(newName);
//            }
//        }
//        for (Choice choice : choiceList) {
//            ChoiceType type = choice.getType();
//            if (type == ChoiceType.Image) {
//                String content = choice.getContent();
//                if (content != null) {
//                    String[] splitDot = content.split("[.]");
//                    String ext = splitDot[splitDot.length-1];
//
//                    String[] splitSlash = content.split("[/]");
//                    String fileName = splitSlash[splitSlash.length-1];
//                    byte[] bytes = imageService.downloadFile(fileName);
//                    imageService.deleteFile(fileName);
//                    InputStream inputStream = imageService.resizePicture(ext, bytes);
//                    String newName = content + "/v2";
//                    imageService.saveImage(newName, inputStream);
//                    choice.updateContent(newName);
//                }
//            }
//        }
//    }

//    private void initKeywordAssociations(Map<Long, List<Keyword>> newKeywordMap) {
//        keywordAssociationRepository.deleteAllInBatch();
//
//        Map<KeywordComb, Integer> keywordCombMap = new HashMap<>();
//        Map<Long, Set<Keyword>> keywordMap = new HashMap<>();
//        List<KeywordAssociation> keywordAssociationList = new ArrayList<>();
//
//        newKeywordMap.forEach((key, value) -> {
//            keywordMap.merge(key, new HashSet<>(value), (existingSet, newSet) -> {
//                existingSet.addAll(newSet);
//                return existingSet;
//            });
//        });
//
//        //3. 각 문제 별로 돌면서 키워드 연관성 체크
//        for (Long id : keywordMap.keySet()) {
//            Set<Keyword> keywordSet = keywordMap.get(id);
//
//            List<List<Keyword>> combination = getCombination(keywordSet);
//            for (List<Keyword> keywords : combination) {
//                Keyword k1 = keywords.get(0);
//                Keyword k2 = keywords.get(1);
//
//                if(k1.getId() < k2.getId()){
//                    Keyword temp = null;
//                    temp = k1;
//                    k1 = k2;
//                    k2 = temp;
//                }
//                KeywordComb keywordComb = new KeywordComb(k1, k2);
//                Integer val = keywordCombMap.get(keywordComb);
//                if (val == null) {
//                    keywordCombMap.put(keywordComb, 1);
//                }else{
//                    keywordCombMap.put(keywordComb, val + 1);
//                }
//
//            }
//        }
//
//        //4. 저장
//        for (KeywordComb k : keywordCombMap.keySet()) {
//            Integer val = keywordCombMap.get(k);
//            KeywordAssociation keywordAssociation = new KeywordAssociation(k.getK1(), k.getK2(), val);
//            keywordAssociationList.add(keywordAssociation);
//        }
//
//        keywordAssociationRepository.saveAll(keywordAssociationList);
//    }

//    private List<List<Keyword>> getCombination(Set<Keyword> keywordSet) {
//        // Set<Keyword>을 List<Keyword>으로 변환
//        List<Keyword> keywordList = new ArrayList<>(keywordSet);
//
//        // 2차원 배열을 저장할 리스트
//        List<List<Keyword>> combinations = new ArrayList<>();
//
//        // 모든 2개씩 조합을 만들어서 combinations에 추가
//        for (int i = 0; i < keywordList.size(); i++) {
//            for (int j = i + 1; j < keywordList.size(); j++) {
//                List<Keyword> combination = new ArrayList<>();
//                combination.add(keywordList.get(i));
//                combination.add(keywordList.get(j));
//                combinations.add(combination);
//            }
//        }
//
//        // 2차원 배열로 변환
//        Keyword[][] combinationArray = new Keyword[combinations.size()][2];
//
//        for (int i = 0; i < combinations.size(); i++) {
//            combinationArray[i] = combinations.get(i).toArray(new Keyword[0]);
//        }
//
//        return combinations;
//    }


//    @Bean
//    public void initExamQuestionLearningRecord() {
//        List<Customer> customerList = customerRepository.findAll();
//        examQuestionLearningRecordRepository.deleteAllInBatch();
//        roundLearningRecordRepository.deleteAllInBatch();
//        keywordLearningRecordRepository.deleteAllInBatch();
//
//        for (Customer customer : customerList) {
//            List<ExamQuestionLearningRecord> recordList = examQuestionRepository.findAll().stream()
//                    .map(r -> new ExamQuestionLearningRecord(customer, r))
//                    .collect(Collectors.toList());
//
//            List<RoundLearningRecord> rrecordList = roundRepository.findAll().stream()
//                    .map(r -> new RoundLearningRecord(r, customer))
//                    .collect(Collectors.toList());
//
//            List<KeywordLearningRecord> krecordList = keywordRepository.findAll().stream()
//                    .map(r -> new KeywordLearningRecord(r, customer))
//                    .collect(Collectors.toList());
//
//            examQuestionLearningRecordRepository.saveAll(recordList);
//            roundLearningRecordRepository.saveAll(rrecordList);
//            keywordLearningRecordRepository.saveAll(krecordList);
//        }
//    }

//    @AllArgsConstructor
//    @EqualsAndHashCode
//    @Getter
//    private class KeywordComb{
//        public Keyword k1;
//        public Keyword k2;
//
//    }

//    @Bean
//    public void makePreparedCustomer() {
//        List<Customer> preparedCustomerList = customerRepository.queryCustomersNotValidated();
//        int customerSize = preparedCustomerList.size();
//        for (int i = customerSize; i <= 50; i++) {
//            Customer customer = new Customer("mock" + i);
//            customerRepository.save(customer);
//            customerService.initCustomerData(customer);
//        }
//    }@Bean
//    public void makePreparedCustomer() {
//        List<Customer> preparedCustomerList = customerRepository.queryCustomersNotValidated();
//        int customerSize = preparedCustomerList.size();
//        for (int i = customerSize; i <= 50; i++) {
//            Customer customer = new Customer("mock" + i);
//            customerRepository.save(customer);
//            customerService.initCustomerData(customer);
//        }
//    }



}
