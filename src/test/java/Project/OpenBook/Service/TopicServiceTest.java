package Project.OpenBook.Service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
class TopicServiceTest {

    @Autowired
    private TopicService topicService;

    @Test
    public void parseKeywordListTest() {
        List<String> keywordList = Arrays.asList("t1", "t2", "t3", "t4");
        String s = topicService.parseKeywordList(keywordList);
        System.out.println(s);
    }

}