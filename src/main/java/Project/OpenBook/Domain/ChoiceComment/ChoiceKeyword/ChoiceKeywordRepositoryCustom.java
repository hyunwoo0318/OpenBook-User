package Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword;

import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.ChoiceComment.Service.Dto.ChoiceCommentInfoDto;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.Topic.Domain.Topic;

import java.util.List;
import java.util.Map;

public interface ChoiceKeywordRepositoryCustom {

      public List<ChoiceKeyword> queryChoiceKeywordsForInit();
      public Map<Choice, List<ChoiceCommentInfoDto>> queryChoiceKeywordsForAdmin(List<Choice> choiceList);
      public List<ChoiceKeyword> queryChoiceKeywordsForTopicList(String topicTitle);
      public List<ChoiceKeyword> queryChoiceKeywordsForTopicList(Integer chapterNumber);
      public List<ChoiceKeyword> queryChoiceKeywordsForTopicList(List<Topic> topicList);
      public List<ChoiceKeyword> queryChoiceKeywordsForExamQuestion(Integer roundNumber);
      public List<ChoiceKeyword> queryChoiceKeywordsForExamQuestion(List<ExamQuestion> questionList);
      public List<ChoiceKeyword> queryChoiceKeywordsForExamQuestion(Long examQuestionId);


}
