package spring.jpa.jpaselftaught.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Data
@Builder
@Entity
@Table(name = "MEMBER")
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @Column(name = "ID")
    private String id;

    private int age;
}
