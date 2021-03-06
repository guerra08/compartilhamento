package br.pucrs.ages.townsq.model;

import br.pucrs.ages.townsq.listeners.AnswerListener;
import br.pucrs.ages.townsq.utils.Chronos;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@EntityListeners(AnswerListener.class)
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
    private int isBest = 0;

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

    @OneToMany(targetEntity = Comment.class, cascade = CascadeType.ALL, mappedBy = "answer")
    private List<Comment> comments;

    @OneToMany(targetEntity = VoteLog.class, cascade = CascadeType.ALL, mappedBy = "answer")
    private List<VoteLog> votes;

    public String getCreatedAtString(){
        return Chronos.dateToPrettyTimeString(this.createdAt);
    }

    public List<Comment> getAllActiveComments(){
        return comments.stream().filter(e -> e.getIsActive() == 1).collect(Collectors.toList());
    }

    public String getVoted(User user, String type){
        VoteLog vote = votes.stream().filter(e -> e.getUser().getId().equals(user.getId())).findFirst().orElse(null);
        if(vote == null){
            return type + ".svg";
        }else{
            if(vote.getEventType().equals(type.toUpperCase()))
                return type + "d.svg";
            else
                return type + ".svg";
        }
    }

}
