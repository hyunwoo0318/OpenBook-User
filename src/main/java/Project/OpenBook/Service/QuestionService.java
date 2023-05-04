package Project.OpenBook.Service;

import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.choice.ChoiceContentIdDto;
import Project.OpenBook.Dto.description.DescriptionContentIdDto;
import Project.OpenBook.Dto.question.QuestionDto;
import Project.OpenBook.Repository.CategoryRepository;
import Project.OpenBook.Repository.QuestionChoiceRepository;
import Project.OpenBook.Repository.QuestionDescriptionRepository;
import Project.OpenBook.Repository.question.QuestionRepository;
import Project.OpenBook.Repository.choice.ChoiceRepository;
import Project.OpenBook.Repository.description.DescriptionRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final TopicRepository topicRepository;
    private final ChoiceRepository choiceRepository;
    private final DescriptionRepository descriptionRepository;

    private final QuestionRepository questionRepository;

    private final QuestionChoiceRepository questionChoiceRepository;
    private final QuestionDescriptionRepository questionDescriptionRepository;

    private final CategoryRepository categoryRepository;
    private final Environment env;

    private final int choiceNum = 5;

    @Transactional
    public QuestionDto makeQuestionTimeAndDescription(Long type, String categoryName, String topicTitle) {

        Topic answerTopic = null;
        //정답 토픽 선정
        if (topicTitle != null) {
            Optional<Topic> topicOptional = topicRepository.findTopicByTitle(topicTitle);
            if (topicOptional.isEmpty()) {
                return null;
            }
            answerTopic = topicOptional.get();
        }else{
            answerTopic = topicRepository.queryRandTopicByCategory(categoryName);
        }

        TempQ tempQ = null;
        if (type == 1) {
            tempQ = makeDescriptionQuestion(answerTopic);
        } else if(type >= 2 && type <=4){
            tempQ = makeTimeQuestion(answerTopic, type);
        } else if(type == 5){
            tempQ = makeTimeFlowQuestion(type);
        }

        if (tempQ.choiceList.size() != 5) {
            return null;
        }
        List<ChoiceContentIdDto> choiceList = new ArrayList<>();
        Long answerId = null;
        DescriptionContentIdDto descriptionContentIdDto = new DescriptionContentIdDto(tempQ.description.getId(), tempQ.description.getContent());

        if(type >= 1 && type <= 4){
            choiceList = tempQ.choiceList.stream().map(c -> new ChoiceContentIdDto(c.getContent(), c.getId())).collect(Collectors.toList());
            answerId = choiceList.get(choiceNum-1).getId();
        } else if (type == 5) {
            int index = tempQ.choiceList.indexOf(null);
            answerId = Integer.toUnsignedLong(index);
            choiceList.subList(index, index + 1).clear();
        }

        return QuestionDto.builder()
                .categoryName(categoryName)
                .answerChoiceId(answerId)
                .prompt(tempQ.prompt)
                .type(type)
                .description(descriptionContentIdDto)
                .choiceList(choiceList)
                .build();
    }


    //특정 주제에 대한 설명으로 옳은것을 찾는 문제생성 메서드
    private TempQ makeDescriptionQuestion(Topic answerTopic) {

        String title = answerTopic.getTitle();
        String categoryName = answerTopic.getCategory().getName();

        //문제 생성
        String prefix = env.getProperty("description.prefix",String.class);
        String suffix = env.getProperty("description.suffix",String.class);
        String prompt = prefix + " " +  categoryName + suffix;

        //정답 주제에 대한 보기와 선지를 가져옴
        Choice answerChoice = choiceRepository.queryRandChoiceByTopic(title);
        Description description = descriptionRepository.findRandDescriptionByTopic(title);

        //정답 주제와 같은 카테고리를 가진 나머지 주제들에서 선지를 가져옴
        List<Choice> choiceList = choiceRepository.queryRandChoicesByCategory(title, categoryName, choiceNum-1);
        choiceList.add(answerChoice);

        return new TempQ(prompt, description, choiceList);
    }

    private TempQ makeTimeQuestion(Topic answerTopic, Long type) {

        String title = answerTopic.getTitle();
        String categoryName = answerTopic.getCategory().getName();
        LocalDate startDate = answerTopic.getStartDate();
        LocalDate endDate = answerTopic.getEndDate();

        String prompt = setPrompt(categoryName, type);

        Description description = descriptionRepository.findRandDescriptionByTopic(title);

        List<Choice> choiceList = new ArrayList<>();
        if (type == 2) {
            //정답 사건의 startDate, endDate가 겹치지 않는 4개의 사건 고르기
            choiceList = choiceRepository.queryChoicesType2(startDate, endDate, choiceNum, 0, categoryName);
        } else if (type == 3) {
            //보기에서 주어진 사건보다 나중에 발생한 사건 찾는 문제
            choiceList = choiceRepository.queryChoicesType3(startDate, endDate,choiceNum, 0, categoryName);
        } else if (type == 4) {
            //보기에 주어진 사건보다 이전에 발생한 사건 찾는 문제
            choiceList = choiceRepository.queryChoicesType4(startDate,endDate, choiceNum, 0, categoryName);
        }

        return new TempQ(prompt, description, choiceList);
    }

    public TempQ makeTimeFlowQuestion(Long type) {
        String prompt = setPrompt("사건", type);

        List<Choice> choiceList = choiceRepository.queryRandChoicesByCategory("사건", 7);
        Random rand = new Random();
        int answerNum = rand.nextInt(5)+1;
        Choice answerChoice = choiceList.get(answerNum);
        Description description = descriptionRepository.findRandDescriptionByTopic(answerChoice.getTopic().getTitle());
        choiceList.set(answerNum, null);

        return new TempQ(prompt, description, choiceList);
    }

    @Transactional
    public Question addQuestion(QuestionDto questionDto){

        Long type = questionDto.getType();
        String categoryName = questionDto.getCategoryName();
        Optional<Category> categoryOptional = categoryRepository.findCategoryByName(categoryName);
        if (categoryOptional.isEmpty() || type > 3) {
            return null;
        }

        List<Long> choiceIdList = questionDto.getChoiceList().stream().map(c -> c.getId()).collect(Collectors.toList());
        List<Choice> choiceList = choiceRepository.queryChoicesById(choiceIdList);
        Long descriptionId = questionDto.getDescription().getId();

        //문제 저장
        Question question = Question.builder()
                .answerChoiceId(questionDto.getAnswerChoiceId())
                .prompt(questionDto.getPrompt())
                .type(type)
                .category(categoryOptional.get())
                .build();
        questionRepository.save(question);

        //선지 저장
        List<QuestionChoice> questionChoiceList = new ArrayList<>();
        for (Choice choice : choiceList) {
            questionChoiceList.add(new QuestionChoice(question, choice));
        }
        questionChoiceRepository.saveAll(questionChoiceList);

        //보기 저장
        Optional<Description> descriptionOptional = descriptionRepository.findById(descriptionId);
        if (descriptionOptional.isEmpty()) {
            return null;
        }
        Description description = descriptionOptional.get();
        questionDescriptionRepository.save(new QuestionDescription(question, description));

        return question;
    }

    @Transactional
    public Question updateQuestion(Long questionId, QuestionDto questionDto) {

        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if (questionOptional.isEmpty()) {
            return null;
        }
        Question question = questionOptional.get();

        Long type = questionDto.getType();
        String categoryName = questionDto.getCategoryName();
        Optional<Category> categoryOptional = categoryRepository.findCategoryByName(categoryName);
        if (categoryOptional.isEmpty() || type > 3) {
            return null;
        }
        Category category = categoryOptional.get();

        Question updatedQuestion = question.updateQuestion(questionDto.getPrompt(), questionDto.getAnswerChoiceId(), type, category);

        return updatedQuestion;
    }

    @Transactional
    public QuestionDto queryQuestion(Long id) {
        return questionRepository.findQuestionById(id);
    }

    public boolean deleteQuestion(Long questionId) {

        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if (questionOptional.isEmpty()) {
            return false;
        }
        questionRepository.deleteById(questionId);
        return true;
    }

    private class TempQ {
        private String prompt;
        private Description description;
        private List<Choice> choiceList;

        public TempQ(String prompt, Description description, List<Choice> choiceList) {
            this.prompt = prompt;
            this.description = description;
            this.choiceList = choiceList;
        }
    }

    private String setPrompt(String categoryName, Long type) {
        String prompt = null;
        if(categoryName.equals("사건")){
            if (type == 2) {
                prompt = env.getProperty("time.case.between", String.class);
            } else if (type == 3) {
                prompt = env.getProperty("time.case.after", String.class);
            } else if (type == 4) {
                prompt = env.getProperty("time.case.before", String.class);
            } else if (type == 5) {
                prompt = env.getProperty("timeFlow.case", String.class);
            }
        }
        //나중에 다른 일에 대한 문제 생성시 추가
        return prompt;
    }

}
