package Project.OpenBook.Repository.imagefile;

import Project.OpenBook.Domain.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public interface ImageFileRepository extends JpaRepository<ImageFile, Long>, ImageFileRepositoryCustom {

}
