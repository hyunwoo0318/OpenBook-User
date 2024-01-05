package Project.OpenBook.Redis;

import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void saveQuestionListToRedis(Integer roundNumber, List<ExamQuestionDto> questionDtoList)
        throws JsonProcessingException {
        String key = makeQuestionKey(roundNumber);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonValue = objectMapper.writeValueAsString(questionDtoList);
        redisTemplate.opsForValue().set(key, jsonValue);
    }

    public List<ExamQuestionDto> getQuestionListFromRedis(Integer roundNumber)
        throws JsonProcessingException {
        String jsonValue = redisTemplate.opsForValue().get(makeQuestionKey(roundNumber));
        if (jsonValue != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonValue, new TypeReference<List<ExamQuestionDto>>() {
            });
        }
        return null;
    }

    private String makeQuestionKey(Integer roundNumber) {
        return "questionList:" + roundNumber;
    }
}
