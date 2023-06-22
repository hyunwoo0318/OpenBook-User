package Project.OpenBook;

import Project.OpenBook.Domain.Description;
import Project.OpenBook.Repository.choice.ChoiceRepository;
import Project.OpenBook.Repository.description.DescriptionRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OpenBookApplication {

	/**
	 * TODO : 유물 topic에 대해서 사진 저장 기능
	 * TODO : 학습진도 도메인 구현 -> 해당 회원이 해당 주제에 대해서 마지막으로 공부한 날짜, 문제 푼 수, 오답률
	 * TODO : 각 보기와 선지별 연관 난이도 설정 -> 	1. 특정 보기가 문제의 보기로 나왔을때의 보기의 정답률
	 * 										2. 특정 보기가 문제의 보기이고 정답 선지가 제공되었을때 둘 간의 연관 난이도
	 * 										3. 특정 선지가 정답 선지로 제공되었을때 오답 선지를 골랐을때 그 둘 간의 연관 난이도
	 */

	public static void main(String[] args) {
		SpringApplication.run(OpenBookApplication.class, args);
	}
}
