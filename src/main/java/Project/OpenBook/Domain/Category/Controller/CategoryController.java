package Project.OpenBook.Domain.Category.Controller;

import Project.OpenBook.Domain.Category.Service.CategoryService;
import Project.OpenBook.Domain.Category.Service.Dto.CategoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "전체 카테고리 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 카테고리 조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<CategoryDto>> queryCategories() {
        List<CategoryDto> categoryList = categoryService.queryCategories();
        return new ResponseEntity<List<CategoryDto>>(categoryList, HttpStatus.OK);
    }

    @Operation(summary = "카테고리 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "카테고리 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 카테고리 생성 실패"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 카테고리 이름 입력")
    })
    @PostMapping
    public ResponseEntity<Void> createCategory(@Validated @RequestBody CategoryDto categoryDto) {
        categoryService.createCategory(categoryDto);

        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }


    @Operation(summary = "카테고리 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인하여 카테고리 수정 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 카테고리 수정 시도"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 카테고리 이름 입력")
    })
    @PatchMapping("/{categoryName}")
    public ResponseEntity<Void> updateCategory(@PathVariable("categoryName") String prevCategoryName, @Validated @RequestBody CategoryDto categoryDto) {
        categoryService.updateCategory(prevCategoryName, categoryDto.getName());

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Operation(summary = "카테고리 삭제", description = "해당 카테고리에 속하던 상세정보의 카테고리 정보는 null")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "해당 카테고리에 토픽이 존재하는경우"),
            @ApiResponse(responseCode = "404", description = "존재히지 않는 카테고리 삭제 시도")
    })
    @DeleteMapping("/{categoryName}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("categoryName") String categoryName) {
        categoryService.deleteCategory(categoryName);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
