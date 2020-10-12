package br.pucrs.ages.townsq.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "answers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class Answer {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Texto não pode ser nulo.")
    @NotEmpty(message = "Texto não pode ser vazio.")
    @Column(name = "text", columnDefinition = "VARCHAR(512)", nullable =  false)
    private String text;

    @Column(name = "isActive")
    private int isActive = 1;

    @Column(name = "isBest")
    private int isBest = 1;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private java.sql.Timestamp updatedAt;

    @CreationTimestamp
    @Column(name = "createdAt")
    private java.sql.Timestamp createdAt;

    @Column(name = "score")
    private int score = 0;

    @ManyToOne(optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(optional = true)
    @JoinColumn(name = "questionId", nullable = false)
    private Question question;

}
