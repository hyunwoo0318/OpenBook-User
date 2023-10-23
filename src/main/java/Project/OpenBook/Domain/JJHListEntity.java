package Project.OpenBook.Domain;

import lombok.Getter;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
public abstract class JJHListEntity {
    private Integer jjhListNumber;
}
