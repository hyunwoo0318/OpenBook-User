package Project.OpenBook.Service;

public class TempQuestionService {

    /**
     * 이전 버전의 question controller, service 모음
     */
    //    @ApiOperation("문제를 임의로 생성해 보여줌")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "성공적인 문제 생성"),
//            @ApiResponse(responseCode = "400", description = "문제 생성 실패"),
//            @ApiResponse(responseCode = "404", description = "존재하지 카테고리나 문제 타입 입력")
//    })
//    @GetMapping("/admin/temp-question")
//    public ResponseEntity makeTempQuestion(@RequestParam("category") String categoryName, @RequestParam("type") Long type,
//                                           @RequestParam(value = "topic", required = false) String topicTitle){
//        QuestionDto questionDto = questionService.makeQuestionTimeAndDescription(type, categoryName,topicTitle);
//        return new ResponseEntity(questionDto, HttpStatus.OK);
//    }
//
//    @ApiOperation("문제 조회")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "성공적인 문제 조회"),
//            @ApiResponse(responseCode = "404", description = "존재하지 않는 문제 조회 요청")
//    })
//    @GetMapping("/questions/{id}")
//    public ResponseEntity queryQuestion(@PathVariable Long id) {
//        QuestionDto questionDto = questionService.queryQuestion(id);
//        return new ResponseEntity(questionDto, HttpStatus.OK);
//    }
//
//    @ApiOperation("문제 생성")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "성공적인 문제 생성"),
//            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인한 문제 생성 실패"),
//            @ApiResponse(responseCode = "404", description = "존재하지 않는 카테고리나 문제 타입 입력으로 인한 문제 생성 실패")
//    })
//    @PostMapping("/admin/questions")
//    public ResponseEntity addQuestion(@Validated @RequestBody QuestionDto questionDto) {
//
//        Question question = questionService.addQuestion(questionDto);
//
//        return new ResponseEntity(question.getId(), HttpStatus.CREATED);
//    }
//
//    @ApiOperation("문제 수정")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "성공적인 문제 수정"),
//            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인한 문제 수정 실패"),
//            @ApiResponse(responseCode = "404", description = "존재하지 않는 카테고리, 문제 타입, 문제 아이디 입력으로 인한 문제 수정 실패")
//    })
//    @PatchMapping("/admin/questions/{questionId}")
//    public ResponseEntity updateQuestion(@PathVariable Long questionId,@Validated @RequestBody QuestionDto questionDto) {
//
//        Question question = questionService.updateQuestion(questionId, questionDto);
//
//        return new ResponseEntity(question.getId(),HttpStatus.OK);
//    }
//
//    @ApiOperation("문제 삭제")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "성공적인 문제 삭제"),
//            @ApiResponse(responseCode = "404", description = "존재하지 않는 문제 아이디 입력으로 인한 문제 수정 실패")
//    })
//    @DeleteMapping("/admin/questions/{question-id}")
//    public ResponseEntity deleteQuestion(@PathVariable("question-id") Long questionId) {
//
//        questionService.deleteQuestion(questionId);
//
//        return new ResponseEntity(HttpStatus.OK);
//    }
    
    //    @Transactional
//    public QuestionDto makeQuestionTimeAndDescription(Long type, String categoryName, String topicTitle) {
//
//        checkCategory(categoryName);
//        Topic answerTopic = null;
//        //정답 토픽 선정
//        if (topicTitle != null) {
//            answerTopic = checkTopic(topicTitle);
//        }else{
//            answerTopic = topicRepository.queryRandTopicByCategory(categoryName);
//        }
//
//        TempQ tempQ = null;
//        if (type == 1) {
//            tempQ = makeDescriptionQuestion(answerTopic);
//        } else if (type == 2) {
//            tempQ = makeTimeType2Question();
//        } else if (type > 2 && type <= 4) {
//            tempQ = makeTimeType34Question(answerTopic, type);
//        } else if (type == 5) {
//            tempQ = makeTimeFlowQuestion(type);
//        }
//
//        if (tempQ.choiceList.size() != 5) {
//            throw new CustomException(NOT_ENOUGH_CHOICE);
//        }
//
//        List<ChoiceContentIdDto> choiceList = new ArrayList<>();
//        Long answerId = null;
//        DescriptionContentIdDto descriptionContentIdDto = new DescriptionContentIdDto(tempQ.description.getId(), tempQ.description.getContent());
//
//        if(type >= 1 && type <= 4){
//            choiceList = tempQ.choiceList.stream().map(c -> new ChoiceContentIdDto(c.getContent(), c.getId())).collect(Collectors.toList());
//            answerId = choiceList.get(choiceNum-1).getId();
//        } else if (type == 5) {
//            int index = tempQ.choiceList.indexOf(null);
//            answerId = Integer.toUnsignedLong(index);
//            choiceList.subList(index, index + 1).clear();
//        }
//
//        return QuestionDto.builder()
//                .categoryName(categoryName)
//                .answerChoiceId(answerId)
//                .prompt(tempQ.prompt)
//                .type(type)
//                .description(descriptionContentIdDto)
//                .choiceList(choiceList)
//                .build();
//    }
//
//
//    //특정 주제에 대한 설명으로 옳은것을 찾는 문제생성 메서드
//    private TempQ makeDescriptionQuestion(Topic answerTopic) {
//
//        String title = answerTopic.getTitle();
//        String categoryName = answerTopic.getCategory().getName();
//
//        //문제 생성
//        String prefix = env.getProperty("description.prefix",String.class);
//        String suffix = env.getProperty("description.suffix",String.class);
//        String prompt = prefix + " " +  categoryName + suffix;
//
//        //정답 주제에 대한 보기와 선지를 가져옴
//        Description description = descriptionRepository.findRandDescriptionByTopic(title);
//        Choice answerChoice = choiceRepository.queryRandChoiceByTopic(answerTopic.getId(),description.getId());
//
//        //정답 주제와 같은 카테고리를 가진 나머지 주제들에서 선지를 가져옴
//        List<Choice> choiceList = choiceRepository.queryRandChoicesByCategory(title, categoryName, choiceNum-1);
//        choiceList.add(answerChoice);
//
//        return new TempQ(prompt, description, choiceList);
//    }
//
//    private TempQ makeTimeType34Question(Topic answerTopic, Long type) {
//        String title = answerTopic.getTitle();
//        String categoryName = answerTopic.getCategory().getName();
//        Integer startDate = answerTopic.getStartDate();
//        Integer endDate = answerTopic.getEndDate();
//
//        String prompt = setPrompt(categoryName, type);
//
//        Description description = descriptionRepository.findRandDescriptionByTopic(title);
//
//        List<Choice> choiceList = new ArrayList<>();
//        if (type == 3) {
//            //보기에서 주어진 사건보다 나중에 발생한 사건 찾는 문제
//            choiceList = choiceRepository.queryChoicesType3(title, startDate, endDate,choiceNum, 0, categoryName);
//        } else if (type == 4) {
//            //보기에 주어진 사건보다 이전에 발생한 사건 찾는 문제
//            choiceList = choiceRepository.queryChoicesType4(title, startDate,endDate, choiceNum, 0, categoryName);
//        }
//
//        return new TempQ(prompt, description, choiceList);
//    }
//
//    private TempQ makeTimeType2Question() {
//
//        Topic answerTopic = dupDateRepository.queryRandomAnswerTopic();
//        if (answerTopic == null) {
//            throw new CustomException(QUESTION_ERROR);
//        }
//        Topic descriptionTopic = dupDateRepository.queryRandomDescriptionTopic(answerTopic);
//
//        String categoryName = answerTopic.getCategory().getName();
//
//        String prompt = setPrompt(categoryName, 2L);
//
//        Description description = descriptionRepository.findRandDescriptionByTopic(descriptionTopic.getTitle());
//
//        List<Choice> choiceList = choiceRepository.queryChoicesType2(answerTopic, descriptionTopic, choiceNum, 0, categoryName);
//
//        return new TempQ(prompt, description, choiceList);
//    }
//
//
//
//    public TempQ makeTimeFlowQuestion(Long type) {
//        String prompt = setPrompt("사건", type);
//
//        List<Choice> choiceList = choiceRepository.queryRandChoicesByCategory("사건", 7);
//        Random rand = new Random();
//        int answerNum = rand.nextInt(5)+1;
//        Choice answerChoice = choiceList.get(answerNum);
//        Description description = descriptionRepository.findRandDescriptionByTopic(answerChoice.getTopic().getTitle());
//        choiceList.set(answerNum, null);
//
//        return new TempQ(prompt, description, choiceList);
//    }
//
//    @Transactional
//    public Question addQuestion(QuestionDto questionDto){
//
//        Long type = questionDto.getType();
//        String categoryName = questionDto.getCategoryName();
//        Category category = checkCategory(categoryName);
//
//        List<Long> choiceIdList = questionDto.getChoiceList().stream().map(c -> c.getId()).collect(Collectors.toList());
//        List<Choice> choiceList = choiceRepository.queryChoicesById(choiceIdList);
//        Long descriptionId = questionDto.getDescription().getId();
//
//        if (type > 5) {
//            throw new CustomException(INVALID_PARAMETER);
//        }
//
//        //문제 저장
//        Question question = Question.builder()
//                .answerChoiceId(questionDto.getAnswerChoiceId())
//                .prompt(questionDto.getPrompt())
//                .type(type)
//                .category(category)
//                .build();
//        questionRepository.save(question);
//
//        //선지 저장
//        List<QuestionChoice> questionChoiceList = new ArrayList<>();
//        for (Choice choice : choiceList) {
//            questionChoiceList.add(new QuestionChoice(question, choice));
//        }
//        questionChoiceRepository.saveAll(questionChoiceList);
//
//        //보기 저장
//        Description description = descriptionRepository.findById(descriptionId).orElseThrow(() -> {
//            throw new CustomException(DESCRIPTION_NOT_FOUND);
//        });
//        questionDescriptionRepository.save(new QuestionDescription(question, description));
//
//        return question;
//    }
//
//    @Transactional
//    public Question updateQuestion(Long questionId, QuestionDto questionDto) {
//
//        Question question = checkQuestion(questionId);
//
//        Long type = questionDto.getType();
//        String categoryName = questionDto.getCategoryName();
//        Category category = checkCategory(categoryName);
//
//        if (type > 5) {
//            throw new CustomException(INVALID_PARAMETER);
//        }
//
//        Question updatedQuestion = question.updateQuestion(questionDto.getPrompt(), questionDto.getAnswerChoiceId(), type, category);
//
//        return updatedQuestion;
//    }
//
//    @Transactional
//    public QuestionDto queryQuestion(Long id) {
//        return questionRepository.findQuestionById(id);
//    }
//
//    public boolean deleteQuestion(Long questionId) {
//
//        checkQuestion(questionId);
//        questionRepository.deleteById(questionId);
//        return true;
//    }
//
//    public void queryTimeFlowQuestion(Integer num) {
//    }

//    private class TempQ {
//        private String prompt;
//        private Description description;
//        private List<Choice> choiceList;
//
//        public TempQ(String prompt, Description description, List<Choice> choiceList) {
//            this.prompt = prompt;
//            this.description = description;
//            this.choiceList = choiceList;
//        }
//    }

//    private String setPrompt(String categoryName, Long type) {
//        String prompt = null;
//        if(categoryName.equals("사건")){
//            if (type == 2) {
//                prompt = env.getProperty("time.case.between", String.class);
//            } else if (type == 3) {
//                prompt = env.getProperty("time.case.after", String.class);
//            } else if (type == 4) {
//                prompt = env.getProperty("time.case.before", String.class);
//            } else if (type == 5) {
//                prompt = env.getProperty("timeFlow.case", String.class);
//            }
//        }
//        //나중에 다른 일에 대한 문제 생성시 추가
//        return prompt;
//    }

    //@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(properties = {  "spring.config.location=classpath:application-test.yml",
//                                    "spring.config.location=classpath:prompt-template.yml"})
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class QuestionControllerTest {
//
//    @LocalServerPort
//    int port;
//
//    @Autowired
//    TestRestTemplate restTemplate;
//
//    @Autowired
//    ChapterRepository chapterRepository;
//
//    @Autowired
//    CategoryRepository categoryRepository;
//
//    @Autowired
//    TopicRepository topicRepository;
//
//    @Autowired
//    ChoiceRepository choiceRepository;
//
//    @Autowired
//    DescriptionRepository descriptionRepository;
//
//    private final String prefix = "http://localhost:";
//
//    String URL;
//
//
//    @BeforeAll
//    public void initTestForChoiceController() {
//        URL = prefix + port;
//        restTemplate = restTemplate.withBasicAuth("admin1", "admin1");
//        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
//        init();
//    }
//
//    private void init(){
//
//        //카테고리 전체 저장
//        Category c1 = new Category("유물");
//        Category c2 = new Category("사건");
//        Category c3 = new Category("국가");
//        Category c4 = new Category("인물");
//
//        categoryRepository.saveAllAndFlush(Arrays.asList(c1, c2, c3, c4));
//
//        //단원 전체 저장
//        Chapter ch1 = new Chapter("ch1", 1);
//        Chapter ch2 = new Chapter("ch2", 2);
//        Chapter ch3 = new Chapter("ch3", 3);
//
//        chapterRepository.saveAllAndFlush(Arrays.asList(ch1, ch2, ch3));
//
//        //topic 전체 생성
//        Random rand = new Random();
//        List<Topic> topicList = new ArrayList<>();
//
//
//        for (int i = 1; i <= 1000; i++) {
//            int year = rand.nextInt(2000) + 1;
//            int month = rand.nextInt(12) + 1; // 1~12 사이의 월을 랜덤으로 생성
//            int day = rand.nextInt(26) + 1; // 1부터 최대 일수 사이의 일을 랜덤으로 생성
//            int length = rand.nextInt(500);
//
//            Integer startDate = year * 1000 + month * 100 + day;
//            Integer endDate = startDate + length;
//
//            Category c  = null;
//
//            if(i <= 10){
//                c = c1;
//            }else if(i <= 20){
//                c = c2;
//            } else if (i <= 30) {
//                c = c3;
//            }else {
//                c = c4;
//            }
//
//            Topic topic = new Topic("topic" + i, startDate, endDate, 0, 0, "detail" + i, ch1, c);
//            topicList.add(topic);
//        }
//
//        topicRepository.saveAllAndFlush(topicList);
//
//        //선지, 보기 생성
//        for (Topic topic : topicList) {
//            for (int i = 1; i <= 5; i++) {
//                choiceRepository.save(new Choice("choice" + i + " in " + topic.getTitle(), topic));
//                descriptionRepository.save(new Description("description" + i + " in " + topic.getTitle(), topic));
//            }
//        }
//    }
//
//    @DisplayName("type1 question 생성 성공 - GET /admin/temp-question?type=1&category=사건")
//    @Test
//    public void type1QuestionSuccess(){
//        Long type = 1L;
//        String categoryName = "사건";
//        String prompt = "해당 사건에 대한 설명으로 옳은 것은?";
//
//        List<Category> all = categoryRepository.findAll();
//
//
//        ResponseEntity<QuestionDto> response = restTemplate.getForEntity(URL + "/admin/temp-question?type=1&category=사건", QuestionDto.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        QuestionDto questionDto = response.getBody();
//
//        assertThat(questionDto.getType()).isEqualTo(type);
//        assertThat(questionDto.getCategoryName()).isEqualTo(categoryName);
//        assertThat(questionDto.getPrompt()).isEqualTo(prompt);
//
//        //정답 선지와 보기의 topic이 동일한지 확인
//        Long descriptionId = questionDto.getDescription().getId();
//        Optional<Description> descriptionOptional = descriptionRepository.findById(descriptionId);
//        assertThat(descriptionOptional.isEmpty()).isFalse();
//        Long descriptionTopicId = descriptionOptional.get().getTopic().getId();
//
//        Long answerChoiceId = questionDto.getAnswerChoiceId();
//        Optional<Choice> choiceOptional = choiceRepository.findById(answerChoiceId);
//        assertThat(choiceOptional.isEmpty()).isFalse();
//        Long choiceTopicId = choiceOptional.get().getTopic().getId();
//
//        assertThat(descriptionTopicId).isEqualTo(choiceTopicId);
//
//        //총 선지가 5개 들어갔는지 테스트
//        List<Long> choiceIdList = questionDto.getChoiceList().stream().map(q -> q.getId()).collect(Collectors.toList());
//        List<Choice> choiceList = choiceRepository.findAllById(choiceIdList);
//        assertThat(choiceList.size()).isEqualTo(5);
//
//        //선지 5개의 topic이 모두 category가 요구사항과 맞는지 테스트
//
//
//        //오답 선지들은 보기와 topic이 다른지 테스트
//        for (Choice choice : choiceList) {
//            Category category = choiceRepository.queryCategoryByChoice(choice.getId());
//            assertThat(category.getName()).isEqualTo(categoryName);
//            if(choice.getId() == questionDto.getAnswerChoiceId()){
//                continue;
//            }else{
//                assertThat(choice.getTopic().getId()).isNotEqualTo(descriptionTopicId);
//            }
//        }
//    }
//
//    @DisplayName("type2 question 생성 성공 -  GET /admin/temp-question?type=2&category=사건")
//    @Test
//    public void type2QuestionSuccess() {
//        Long type = 2L;
//        String categoryName = "사건";
//        String prompt = "해당 사건이 발생한 시기에 동아시아에서 볼 수 있는 모습으로 가장 적절한 것은?";
//
//        ResponseEntity<QuestionDto> response = restTemplate.getForEntity(URL + "/admin/temp-question?type=2&category=사건", QuestionDto.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        QuestionDto questionDto = response.getBody();
//
//        assertThat(questionDto.getType()).isEqualTo(type);
//        assertThat(questionDto.getCategoryName()).isEqualTo(categoryName);
//        assertThat(questionDto.getPrompt()).isEqualTo(prompt);
//
//        //보기의 topic의 시간대의 사이에 정답 선지의 시간대가 있는지 테스트
//        Long descriptionId = questionDto.getDescription().getId();
//        Topic descriptionTopic = topicRepository.queryTopicByDescription(descriptionId);
//        Topic answerTopic = topicRepository.queryTopicByChoice(questionDto.getAnswerChoiceId());
//
//        assertThat(answerTopic.getEndDate()>=(descriptionTopic.getEndDate())).isTrue();
//        assertThat(answerTopic.getStartDate()<=(descriptionTopic.getStartDate())).isTrue();
//
//        //오답 선지들은 해당 범위에 없는지 테스트
//        List<ChoiceContentIdDto> choiceList = questionDto.getChoiceList();
//        for (ChoiceContentIdDto c : choiceList) {
//            if(c.getId() == questionDto.getAnswerChoiceId()) continue;
//            else{
//                Topic topic = topicRepository.queryTopicByChoice(c.getId());
//                assertThat((topic.getEndDate()<(descriptionTopic.getEndDate())) &&
//                        (topic.getStartDate()>(descriptionTopic.getStartDate()))).isFalse();
//            }
//        }
//    }
//
//    @DisplayName("type3 question 생성 성공 -  GET /admin/temp-question?type=3&category=사건")
//    @Test
//    public void type3QuestionSuccess() {
//        Long type = 3L;
//        String categoryName = "사건";
//        String prompt = "해당 사건이 발생한 이후에 동아시아에서 볼 수 있는 모습으로 가장 적절한 것은?";
//
//        ResponseEntity<QuestionDto> response = restTemplate.getForEntity(URL + "/admin/temp-question?type=3&category=사건", QuestionDto.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        QuestionDto questionDto = response.getBody();
//
//        assertThat(questionDto.getType()).isEqualTo(type);
//        assertThat(questionDto.getCategoryName()).isEqualTo(categoryName);
//        assertThat(questionDto.getPrompt()).isEqualTo(prompt);
//
//        //보기의 topic의 시간대의 사이에 정답 선지의 시간대가 있는지 테스트
//        Long descriptionId = questionDto.getDescription().getId();
//        Topic descriptionTopic = topicRepository.queryTopicByDescription(descriptionId);
//        Topic answerTopic = topicRepository.queryTopicByChoice(questionDto.getAnswerChoiceId());
//
//        assertThat(answerTopic.getStartDate()>(descriptionTopic.getEndDate())).isTrue();
//
//        //오답 선지들은 해당 범위에 없는지 테스트
//        List<ChoiceContentIdDto> choiceList = questionDto.getChoiceList();
//        for (ChoiceContentIdDto c : choiceList) {
//            if(c.getId() == questionDto.getAnswerChoiceId()) continue;
//            else{
//                Topic topic = topicRepository.queryTopicByChoice(c.getId());
//                assertThat(topic.getStartDate()>(descriptionTopic.getEndDate())).isFalse();
//            }
//        }
//    }
//
//    @DisplayName("type4 question 생성 성공 -  GET /admin/temp-question?type=4&category=사건")
//    @Test
//    public void type4QuestionSuccess() {
//        Long type = 4L;
//        String categoryName = "사건";
//        String prompt = "해당 사건이 발생한 이전에 동아시아에서 볼 수 있는 모습으로 가장 적절한 것은?";
//
//        ResponseEntity<QuestionDto> response = restTemplate.getForEntity(URL + "/admin/temp-question?type=4&category=사건", QuestionDto.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        QuestionDto questionDto = response.getBody();
//
//        assertThat(questionDto.getType()).isEqualTo(type);
//        assertThat(questionDto.getCategoryName()).isEqualTo(categoryName);
//        assertThat(questionDto.getPrompt()).isEqualTo(prompt);
//
//        //보기의 topic의 시간대의 사이에 정답 선지의 시간대가 있는지 테스트
//        Long descriptionId = questionDto.getDescription().getId();
//        Topic descriptionTopic = topicRepository.queryTopicByDescription(descriptionId);
//        Topic answerTopic = topicRepository.queryTopicByChoice(questionDto.getAnswerChoiceId());
//
//        assertThat(answerTopic.getEndDate()<(descriptionTopic.getStartDate())).isTrue();
//
//        //오답 선지들은 해당 범위에 없는지 테스트
//        List<ChoiceContentIdDto> choiceList = questionDto.getChoiceList();
//        for (ChoiceContentIdDto c : choiceList) {
//            if(c.getId() == questionDto.getAnswerChoiceId()) continue;
//            else{
//                Topic topic = topicRepository.queryTopicByChoice(c.getId());
//                assertThat(topic.getEndDate()<(descriptionTopic.getStartDate())).isFalse();
//            }
//        }
//    }
//
////    @DisplayName("type5 question 생성 성공 -  GET /admin/temp-question?type=5&category=사건")
////    @Test
////    public void type5QuestionSuccess() {
////        Long type = 5L;
////        String categoryName = "사건";
////        String prompt = "해당 사건이 발생한 시기를 연표에서 옳게 고른 것은?";
////
////        ResponseEntity<QuestionDto> response = restTemplate.getForEntity(URL + "/admin/temp-question?type=5&category=사건", QuestionDto.class);
////        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
////
////        QuestionDto questionDto = response.getBody();
////
////        assertThat(questionDto.getType()).isEqualTo(type);
////        assertThat(questionDto.getCategoryName()).isEqualTo(categoryName);
////        assertThat(questionDto.getPrompt()).isEqualTo(prompt);
////    }
//
//
//
//}
}



