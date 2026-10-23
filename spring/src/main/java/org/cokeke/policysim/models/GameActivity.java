package org.cokeke.policysim.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity 
@Table(name = "activity")
public class GameActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mainContent;

    @Column(nullable = false)
    private String supportingInfo;

    @Column(nullable = true)
    private String type;

    @Column(nullable = false)
    private LocalDateTime activiAtyTime;

    // @ManyToOne()
    // @JoinColumn(name = "user_id" nullable = false)
    // private User user;
    
}
