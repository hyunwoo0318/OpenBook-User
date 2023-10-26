package Project.OpenBook.Domain.StudyHistory;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.StudyHistory.KeywordLearningRecord.KeywordLearningRecord;
import Project.OpenBook.Domain.StudyHistory.KeywordLearningRecord.KeywordLearningRecordRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.KEYWORD_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class StudyHistoryService {

    private final KeywordLearningRecordRepository keywordLearningRecordRepository;
    private final KeywordRepository keywordRepository;


    @Transactional
    public void saveKeywordWrongCount(Customer customer, List<WrongCountAddDto> dtoList) {
        List<Long> keywordIdList = dtoList.stream()
                .map(d -> d.getId())
                .collect(Collectors.toList());

        //TODO : topic이랑 q.c까지 fetchJoin해서 가져오기
        Map<Long, Keyword> keywordMap = keywordRepository.findAllById(keywordIdList).stream()
                .collect(Collectors.toMap(k -> k.getId(), k -> k));

        Map<Keyword, KeywordLearningRecord> keywordRecordMap = keywordLearningRecordRepository.queryKeywordLearningRecordsInKeywords(customer, keywordIdList).stream()
                .collect(Collectors.toMap(kl -> kl.getKeyword(), kl -> kl));

        for (WrongCountAddDto dto : dtoList) {
            Long keywordId = dto.getId();
            Integer wrongCount = dto.getWrongCount();
            Integer answerCount = dto.getAnswerCount();

            Keyword keyword = keywordMap.get(keywordId);
            if (keyword == null) {
                throw new CustomException(KEYWORD_NOT_FOUND);
            }

            /**
             * 키워드 -> 토픽 -> q.c의 흐름으로 update하기
             */
            //1.키워드
            KeywordLearningRecord record = keywordRecordMap.get(keyword);
            if (record == null) {
                KeywordLearningRecord newRecord = new KeywordLearningRecord(keyword, customer,answerCount, wrongCount);
                keywordLearningRecordRepository.save(newRecord);
            }else{
                record.updateCount(answerCount, wrongCount);
            }

            //2. 토픽

            //3. q.c

        }

    }
}
