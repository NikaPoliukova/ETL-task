package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@Table("sports")
public class Sport {

    @Id
    private Integer id;
    private String name;

}
