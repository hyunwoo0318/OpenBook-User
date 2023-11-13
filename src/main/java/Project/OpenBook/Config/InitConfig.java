package Project.OpenBook.Config;

import Project.OpenBook.Constants.KeywordUsageConst;
import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.Category.Repository.CategoryRepository;
import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Domain.Choice.Repository.ChoiceRepository;
import Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword.ChoiceKeyword;
import Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword.ChoiceKeywordRepository;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
import Project.OpenBook.Domain.Description.Repository.DescriptionRepository;
import Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword.DescriptionKeyword;
import Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword.DescriptionKeywordRepository;
import Project.OpenBook.Domain.Era.EraRepository;
import Project.OpenBook.Domain.ExamQuestion.Repo.ExamQuestionRepository;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.KeywordAssociation.KeywordAssociationRepository;
import Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Repository.ExamQuestionLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Repo.KeywordLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Repo.QuestionCategoryLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.RoundLearningRecord.RoundLearningRecordRepository;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.QuestionCategory.Repo.QuestionCategoryRepository;
import Project.OpenBook.Domain.Round.Repo.RoundRepository;
import Project.OpenBook.Domain.Search.ChapterSearch.ChapterSearchRepository;
import Project.OpenBook.Domain.Search.KeywordSearch.KeywordSearchRepository;
import Project.OpenBook.Domain.Search.TopicSearch.TopicSearchRepository;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Image.ImageService;
import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class InitConfig {

    private final TopicSearchRepository topicSearchRepository;
    private final TopicRepository topicRepository;
    private final ChapterRepository chapterRepository;
    private final ChapterSearchRepository chapterSearchRepository;

    private final KeywordRepository keywordRepository;
    private final KeywordSearchRepository keywordSearchRepository;
    private final KeywordLearningRecordRepository keywordLearningRecordRepository;
    private final QuestionCategoryLearningRecordRepository questionCategoryLearningRecordRepository;

    private final QuestionCategoryRepository questionCategoryRepository;
    private final EraRepository eraRepository;
    private final CategoryRepository categoryRepository;

    private final DescriptionRepository descriptionRepository;
    private final ChoiceRepository choiceRepository;
    private final ImageService imageService;

    private final RoundRepository roundRepository;
    private final RoundLearningRecordRepository roundLearningRecordRepository;

    private final ChoiceKeywordRepository choiceKeywordRepository;
    private final DescriptionKeywordRepository descriptionKeywordRepository;

    private final ExamQuestionLearningRecordRepository examQuestionLearningRecordRepository;
    private final ExamQuestionRepository examQuestionRepository;

    private final CustomerRepository customerRepository;
    private final KeywordAssociationRepository keywordAssociationRepository;
    private final PasswordEncoder passwordEncoder;

    private final AmazonS3Client amazonS3;



    /**
     * ElasticSearch를 위한 init
     * 각 topic의 title, id를 저장
     */
//    @Bean
//    public void initElasticSearchIndex() {
//        chapterSearchRepository.deleteAll();
//        topicSearchRepository.deleteAll();
//        keywordSearchRepository.deleteAll();
//
//        List<ChapterSearch> chapterSearchList = chapterRepository.findAll().stream()
//                .map(ChapterSearch::new)
//                .collect(Collectors.toList());
//
//        List<TopicSearch> topicSearchList = topicRepository.queryTopicsWithChapter().stream()
//                .map(TopicSearch::new)
//                .collect(Collectors.toList());
//
//        List<KeywordSearch> keywordSearchList = keywordRepository.queryKeywordsWithChapter().stream()
//                .map(KeywordSearch::new)
//                .collect(Collectors.toList());
//
//        chapterSearchRepository.saveAll(chapterSearchList);
//        topicSearchRepository.saveAll(topicSearchList);
//        keywordSearchRepository.saveAll(keywordSearchList);
//
//    }

    /**
     * 기본 관리자 아이디 세팅
     */
    @Bean
    public void initAdmin(){
        if(customerRepository.findByNickName("admin1").isEmpty()){
            Customer admin1 = new Customer("admin1", passwordEncoder.encode("admin1"), Role.ADMIN);
            Customer admin2 = new Customer("admin2", passwordEncoder.encode("admin2"), Role.ADMIN);
            customerRepository.saveAll(Arrays.asList(admin1, admin2));
        }
    }

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
//
//        for (Customer customer : customerList) {
//            List<ExamQuestionLearningRecord> recordList = examQuestionRepository.findAll().stream()
//                    .map(r -> new ExamQuestionLearningRecord(customer, r))
//                    .collect(Collectors.toList());
//
//            examQuestionLearningRecordRepository.saveAll(recordList);
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



}
