package com.hieunguyen.podcastai.entity;

import com.hieunguyen.podcastai.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "tags")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag extends AuditableEntity {
    
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
    
    @Column(name = "description", length = 200)
    private String description;
    
    @Column(name = "color", length = 7)
    private String color;
    
    @Column(name = "usage_count")
    private Integer usageCount = 0;
    
    @Column(name = "is_trending", nullable = false)
    private Boolean isTrending = false;
}