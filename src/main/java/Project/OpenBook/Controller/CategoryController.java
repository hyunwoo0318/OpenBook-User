package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Category;
import Project.OpenBook.Dto.CategoryDto;
import Project.OpenBook.Dto.ErrorDto;
import Project.OpenBook.Service.CategoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.NamingStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @ApiOperation(value = "전체 카테고리 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 카테고리 조회 성공")
    })
    @GetMapping
    public ResponseEntity queryCategories() {
        List<String> categoryList = categoryService.queryCategories();
        return new ResponseEntity(categoryList, HttpStatus.OK);
    }

    @ApiOperation(value = "카테고리 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "카테고리 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 카테고리 생성 실패")
    })
    @PostMapping
    public ResponseEntity createCategory(@Validated @RequestBody CategoryDto categoryDto, BindingResult bindingResult) {
        List<ErrorDto> errorDtoList = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            errorDtoList = bindingResult.getFieldErrors().stream().map(err -> new ErrorDto(err.getField(), err.getDefaultMessage())).collect(Collectors.toList());
            return new ResponseEntity(errorDtoList, HttpStatus.BAD_REQUEST);
        }

        Category category = categoryService.createCategory(categoryDto.getName(), errorDtoList);
        if (category == null) {
            return new ResponseEntity(errorDtoList, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(errorDtoList, HttpStatus.CREATED);
    }


    @ApiOperation(value = "카테고리 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인하여 카테고리 수정 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 카테고리 수정 시도")
    })
    @PatchMapping("/{categoryName}")
    public ResponseEntity updateCategory(@PathVariable("categoryName") String categoryName, @Validated @RequestBody CategoryDto categoryDto, BindingResult bindingResult) {
        List<ErrorDto> errorDtoList = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            errorDtoList = bindingResult.getFieldErrors().stream().map(err -> new ErrorDto(err.getField(), err.getDefaultMessage())).collect(Collectors.toList());
            return new ResponseEntity(errorDtoList, HttpStatus.BAD_REQUEST);
        }

        Category category = categoryService.updateCategory(categoryName, categoryDto.getName(), errorDtoList);
        if (category == null && errorDtoList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        if (category == null) {
            return new ResponseEntity(errorDtoList, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(errorDtoList, HttpStatus.OK);
    }

    @ApiOperation(value = "카테고리 삭제", notes = "해당 카테고리에 속하던 상세정보의 카테고리 정보는 null")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "존재히지 않는 카테고리 삭제 시도")
    })
    @DeleteMapping("/{categoryName}")
    public ResponseEntity deleteCategory(@PathVariable("categoryName") String categoryName) {
        if(!categoryService.deleteCategory(categoryName)){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
