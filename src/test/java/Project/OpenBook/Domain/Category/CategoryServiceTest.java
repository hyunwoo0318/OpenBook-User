//package Project.OpenBook.Domain.Category;
//
//import Project.OpenBook.Domain.Category.Domain.Category;
//import Project.OpenBook.Domain.Category.Repository.CategoryRepository;
//import Project.OpenBook.Domain.Category.Service.CategoryService;
//import Project.OpenBook.Domain.Category.Service.Dto.CategoryDto;
//import Project.OpenBook.Domain.Topic.Domain.Topic;
//import Project.OpenBook.Handler.Exception.CustomException;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Optional;
//
//import static Project.OpenBook.Constants.ErrorCode.*;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("CategoryService Class")
//public class CategoryServiceTest {
//
//    @InjectMocks
//    CategoryService categoryService;
//    @Mock
//    CategoryRepository categoryRepository;
//
//    @Nested
//    @DisplayName("createCategory() 메서드는")
//    public class createCategoryTest{
//
//        private final String categoryName = "categoryName";
//
//        @Nested
//        @DisplayName("이름을 입력받으면")
//        public class inputName {
//
//            @Test
//            @DisplayName("해당 이름을 가진 category를 생성하고 DB에 저장하고 생성한 category를 리턴한다.")
//            public void returnNewCategory(){
//                //given
//                Category category = new Category(categoryName);
//
//                given(categoryRepository.findCategoryByName(categoryName))
//                        .willReturn(Optional.empty());
//
//                //when
//                Category newCategory = categoryService.createCategory(new CategoryDto(categoryName));
//
//                //then
//                assertThat(newCategory).usingRecursiveComparison().isEqualTo(category);
//            }
//
//            @Nested
//            @DisplayName("해당 이름을 가진 category가 이미 존재하면")
//            public class dupCategory {
//
//                @Test
//                @DisplayName("DUP_CATEGORY_NAME Exception을 던진다.")
//                public void throwDupCategoryNameException() {
//                    //given
//                    given(categoryRepository.findCategoryByName(categoryName))
//                            .willReturn(Optional.of(new Category(categoryName)));
//
//                    //when
//                    CustomException customException = assertThrows(CustomException.class, () -> {
//                        categoryService.createCategory(new CategoryDto(categoryName));
//                    });
//
//                    //then
//                    assertThat(customException).usingRecursiveComparison().isEqualTo(new CustomException(DUP_CATEGORY_NAME));
//                }
//            }
//        }
//    }
//
//    @Nested
//    @DisplayName("updateCategory() 메서드는")
//    public class updateCategoryTest {
//
//        @Nested
//        @DisplayName("기존의 카테고리 이름과 변경할 카테고리 이름을 입력받으면")
//        public class inputNewName{
//
//            @Test
//            @DisplayName("변경할 카테고리 이름으로 이름을 변경하고 변경한 카테고리를 리턴한다.")
//            public void returnNewCategory(){
//                //given
//                String prevName = "c1";
//                String afterName = "c2";
//
//                given(categoryRepository.findCategoryByName(prevName))
//                        .willReturn(Optional.ofNullable(new Category(prevName)));
//
//                given(categoryRepository.findCategoryByName(afterName))
//                        .willReturn(Optional.empty());
//
//                //when
//                Category newCategory = categoryService.updateCategory(prevName, afterName);
//
//                //then
//                assertThat(newCategory).usingRecursiveComparison().isEqualTo(new Category(afterName));
//            }
//
//            @Nested
//            @DisplayName("기존의 이름과 변경할 이름이 동일한 경우")
//            public class sameInputName{
//
//                @Test
//                @DisplayName("기존의 카테고리를 리턴한다.")
//                public void returnPrevCategory(){
//                    //given
//                    String prevName = "c1";
//                    String afterName = "c1";
//
//                    given(categoryRepository.findCategoryByName(prevName))
//                            .willReturn(Optional.ofNullable(new Category(prevName)));
//
//                    //when
//                    Category newCategory = categoryService.updateCategory(prevName, afterName);
//
//                    //then
//                    assertThat(newCategory).usingRecursiveComparison().isEqualTo(new Category(prevName));
//                }
//            }
//
//            @Nested
//            @DisplayName("기존 이름을 가진 카테고리가 존재하지 않을 경우")
//            public class notExistCategory{
//
//                @Test
//                @DisplayName("CATEGORY_NOT_FOUND Exception을 날린다.")
//                public void throwCategoryNotFoundException(){
//                    //given
//                    String prevName = "c1";
//                    String afterName = "c2";
//
//                    given(categoryRepository.findCategoryByName(prevName))
//                            .willReturn(Optional.empty());
//
//                    //when
//                    CustomException customException = assertThrows(CustomException.class, () -> {
//                        categoryService.updateCategory(prevName, afterName);
//                    });
//
//                    //then
//                    assertThat(customException).usingRecursiveComparison().isEqualTo(new CustomException(CATEGORY_NOT_FOUND));
//
//                }
//            }
//
//            @Nested
//            @DisplayName("변경할 이름을 가진 카테고리가 존재하는 경우")
//            public class existCategory{
//
//                @Test
//                @DisplayName("DUP_CATEGORY_NAME Exception을 던진다.")
//                public void throwDupCategoryNameException(){
//                    //given
//                    String prevName = "c1";
//                    String afterName = "c2";
//
//                    given(categoryRepository.findCategoryByName(prevName))
//                            .willReturn(Optional.ofNullable(new Category(prevName)));
//
//                    given(categoryRepository.findCategoryByName(afterName))
//                            .willReturn(Optional.of(new Category(afterName)));
//
//                    //when
//                    CustomException customException = assertThrows(CustomException.class, () -> {
//                        categoryService.updateCategory(prevName, afterName);
//                    });
//
//                    //then
//                    assertThat(customException).usingRecursiveComparison().isEqualTo(new CustomException(DUP_CATEGORY_NAME));
//
//                }
//            }
//        }
//    }
//
//    @Nested
//    @DisplayName("deleteCategory() 메서드는")
//    public class deleteCategoryTest{
//
//        private final String categoryName = "categoryName";
//        @Nested
//        @DisplayName("카테고리 이름을 입력받으면")
//        public class inputCategoryName{
//
//            @Nested
//            @DisplayName("해당 이름을 가진 카테고리가 존재하지 않는 경우")
//            public class notExistCategory {
//
//                @Test
//                @DisplayName("CATEGORY_NOT_FOUND Exception을 던진다.")
//                public void throwCategoryNotFoundException(){
//                    //given
//                    given(categoryRepository.findCategoryByName(categoryName))
//                            .willReturn(Optional.empty());
//
//                    //when
//                    CustomException customException = assertThrows(CustomException.class, () -> {
//                        categoryService.deleteCategory(categoryName);
//                    });
//
//                    //then
//                    assertThat(customException).usingRecursiveComparison().isEqualTo(new CustomException(CATEGORY_NOT_FOUND));
//
//                }
//            }
//
//            @Nested
//            @DisplayName("해당 카테고리에 존재하는 토픽이 존재하지 않는경우")
//            public class notExistTopic{
//                @Test
//                @DisplayName("해당 이름을 가지는 카테고리를 삭제하고 true를 리턴한다.")
//                public void returnTrue() {
//                    //given
//                    Category mockCategory = mock(Category.class);
//                    given(categoryRepository.findCategoryByName(categoryName))
//                            .willReturn(Optional.ofNullable(mockCategory));
//                    when(mockCategory.getTopicList()).thenReturn(new ArrayList<>());
//
//                    //when
//                    boolean ret = categoryService.deleteCategory(categoryName);
//
//                    //then
//                    assertThat(ret).isTrue();
//                }
//            }
//
//            @Nested
//            @DisplayName("해당 카테고리에 존재하는 토픽이 존재하는 경우")
//            public class existTopic {
//
//                @Test
//                @DisplayName("CATEGORY_HAS_TOPIC Exception을 던진다.")
//                public void throwCategoryHasTopicException() {
//                    //given
//                    Category mockCategory = mock(Category.class);
//                    Topic mockTopic = mock(Topic.class);
//                    given(categoryRepository.findCategoryByName(categoryName))
//                            .willReturn(Optional.ofNullable(mockCategory));
//                    when(mockCategory.getTopicList()).thenReturn(Arrays.asList(mockTopic));
//
//                    //when
//                    CustomException customException = assertThrows(CustomException.class, () -> {
//                        categoryService.deleteCategory(categoryName);
//                    });
//
//                    //then
//                    assertThat(customException).usingRecursiveComparison().isEqualTo(new CustomException(CATEGORY_HAS_TOPIC));
//                }
//            }
//
//
//        }
//    }
//}
