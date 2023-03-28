package Project.OpenBook.Domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public abstract class BaseEntity {
    @Column(updatable = false)
    private String createdTime;
    @Column
    private String modifiedTime;

    @PrePersist
    public void prePersist() {
        this.createdTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        this.modifiedTime = this.createdTime;
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
