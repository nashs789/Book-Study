package spring.jpa.jpaselftaught.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Test {

    @Id @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;
}
