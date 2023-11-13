package Project.OpenBook.Domain.Round;

import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.Round.Domain.Round;
import Project.OpenBook.Domain.Round.Repo.RoundRepository;
import Project.OpenBook.Domain.Round.Service.RoundService;
import Project.OpenBook.Domain.Round.Service.dto.RoundDto;
import Project.OpenBook.Handler.Exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;

import static Project.OpenBook.Constants.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoundService Class")
public class RoundServiceTest {

    @InjectMocks
    RoundService roundService;

    @Mock RoundRepository roundRepository;

    @Mock
    RoundValidator roundValidator;

    @Nested
    @DisplayName("createRound() 메서드는")
    public class createRoundTest{

        private final Integer roundNum = 1;
        private final Integer date = 1123;

        @Nested
        @DisplayName("번호와 날짜를 입력받으면")
        public class inputNumDate {

            @Test
            @DisplayName("입력한 정보를 가진 round를 생성하고 DB에 저장하고 생성한 round를 리턴한다.")
            public void returnNewRound(){
                //given

                Round round = new Round(date, roundNum);

                doNothing().when(roundValidator).checkDupRoundNumber(roundNum);

                //when
                Round newRound = roundService.createRound(new RoundDto(roundNum, date));

                //then
                assertThat(newRound).usingRecursiveComparison().isEqualTo(round);
            }

            @Nested
            @DisplayName("해당 번호를 가진 round가 이미 존재하면")
            public class dupRound {

                @Test
                @DisplayName("DUP_ROUND_NUMBER Exception을 던진다.")
                public void throwDupRoundNumException() {
                    //given
                    doThrow(new CustomException(DUP_ROUND_NUMBER)).when(roundValidator).checkDupRoundNumber(roundNum);

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        roundService.createRound(new RoundDto(roundNum, date));
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison().isEqualTo(new CustomException(DUP_ROUND_NUMBER));
                }
            }
        }
    }

    @Nested
    @DisplayName("updateRound() 메서드는")
    public class updateRoundTest {

        @Nested
        @DisplayName("기존의 round 번호와 변경할 번호, 날짜를 입력하면")
        public class inputNewNumDate{

            @Test
            @DisplayName("입력한 번호와 날짜로 round 정보를 변경하고 변경한 round를 리턴한다.")
            public void returnNewRound(){
                //given
                Round mockRound = mock(Round.class);
                Integer prevNum = 1;
                Integer prevDate = 123;

                Integer afterNum = 2;
                Integer afterDate = 456;
                Round round = new Round(afterDate, afterNum);

                doReturn(new Round(prevDate, prevNum)).when(roundValidator).checkRound(prevNum);
                doNothing().when(roundValidator).checkDupRoundNumber(afterNum);

                //when
                Round newRound = roundService.updateRound(prevNum, new RoundDto(afterNum, afterDate));


                //then
                assertThat(newRound).usingRecursiveComparison().isEqualTo(round);
            }

            @Nested
            @DisplayName("기존의 이름과 변경할 번호가 동일한 경우")
            public class sameInputName{

                @Test
                @DisplayName("이름 중복여부를 체크하지 않고 변경한다.")
                public void returnPrevRound(){
                    //given
                    Integer prevNum = 1;
                    Integer prevDate = 123;

                    Integer afterNum = 1;
                    Integer afterDate = 456;

                    doReturn(new Round(prevDate, prevNum)).when(roundValidator).checkRound(prevNum);

                    //when
                    Round newRound = roundService.updateRound(prevNum, new RoundDto(afterNum, afterDate));

                    //then
                    Round round = new Round(afterDate, afterNum);
                    assertThat(newRound).usingRecursiveComparison().isEqualTo(round);
                }
            }

            @Nested
            @DisplayName("기존 번호를 가진 round가 존재하지 않을 경우")
            public class notExistRound{

                @Test
                @DisplayName("ROUND_NOT_FOUND Exception을 날린다.")
                public void throwRoundNotFoundException(){
                    //given
                    Integer prevNum = 1;
                    Integer prevDate = 123;

                    Integer afterNum = 2;
                    Integer afterDate = 456;

                    doThrow(new CustomException(ROUND_NOT_FOUND)).when(roundValidator).checkRound(prevNum);

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        roundService.updateRound(prevNum, new RoundDto(afterNum, afterDate));
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison().isEqualTo(new CustomException(ROUND_NOT_FOUND));
                }
            }

            @Nested
            @DisplayName("변경할 번호를 가진 round가 존재하는 경우")
            public class existRound{

                @Test
                @DisplayName("DUP_ROUND_NUMBER Exception을 던진다.")
                public void throwDupRoundNumberException(){
                    //given
                    Integer prevNum = 1;
                    Integer prevDate = 123;

                    Integer afterNum = 2;
                    Integer afterDate = 456;

                    doReturn(new Round(prevDate, prevNum)).when(roundValidator).checkRound(prevNum);
                    doThrow(new CustomException(DUP_ROUND_NUMBER)).when(roundValidator).checkDupRoundNumber(afterNum);

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        roundService.updateRound(prevNum, new RoundDto(afterNum, afterDate));
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison().isEqualTo(new CustomException(DUP_ROUND_NUMBER));

                }
            }
        }
    }

    @Nested
    @DisplayName("deleteRound() 메서드는")
    public class deleteRoundTest{

        private final Integer roundNum = 1;
        private final Integer date = 123;
        @Nested
        @DisplayName("round 번호를 입력받으면")
        public class inputRoundNum{

            @Nested
            @DisplayName("해당 번호를 가진 round가 존재하지 않는 경우")
            public class notExistRound {

                @Test
                @DisplayName("ROUND_NOT_FOUND Exception을 던진다.")
                public void throwRoundNotFoundException(){
                    //given
                    doThrow(new CustomException(ROUND_NOT_FOUND)).when(roundValidator).checkRound(roundNum);

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        roundService.deleteRound(roundNum);
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison().isEqualTo(new CustomException(ROUND_NOT_FOUND));

                }
            }

            @Nested
            @DisplayName("해당 카테고리에 존재하는 문제가 존재하지 않는경우")
            public class notExistExamQuestion{
                @Test
                @DisplayName("해당 번호를 가지는 round를 삭제하고 true를 리턴한다.")
                public void returnTrue() {
                    //given
                    Round mockRound = mock(Round.class);
                    doReturn(mockRound).when(roundValidator).checkRound(roundNum);
                    when(mockRound.getExamQuestionList()).thenReturn(new ArrayList<>());

                    //when
                    boolean ret = roundService.deleteRound(roundNum);

                    //then
                    assertThat(ret).isTrue();
                }
            }

            @Nested
            @DisplayName("해당 카테고리에 존재하는 토픽이 존재하는 경우")
            public class existExamQuestion {

                @Test
                @DisplayName("ROUND_HAS_QUESTION Exception을 던진다.")
                public void throwRoundHasQuestionException() {
                    //given
                    Round mockRound = mock(Round.class);
                    ExamQuestion mockQuestion = mock(ExamQuestion.class);
                    doReturn(mockRound).when(roundValidator).checkRound(roundNum);
                    when(mockRound.getExamQuestionList()).thenReturn(Arrays.asList(mockQuestion));

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        roundService.deleteRound(roundNum);
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison().isEqualTo(new CustomException(ROUND_HAS_QUESTION));
                }
            }


        }
    }
}
