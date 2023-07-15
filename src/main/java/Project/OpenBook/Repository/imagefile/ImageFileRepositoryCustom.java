package Project.OpenBook.Repository.imagefile;

import Project.OpenBook.Domain.ImageFile;

import java.util.List;

public interface ImageFileRepositoryCustom {

    public List<ImageFile> queryByKeyword(Long keywordId);
}
