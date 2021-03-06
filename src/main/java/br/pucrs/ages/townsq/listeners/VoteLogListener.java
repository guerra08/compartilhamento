package br.pucrs.ages.townsq.listeners;

import br.pucrs.ages.townsq.model.VoteLog;
import br.pucrs.ages.townsq.service.AnswerService;
import br.pucrs.ages.townsq.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
public class VoteLogListener {

    public VoteLogListener() { }

    @PersistenceContext
    private EntityManager entityManager;

    private AnswerService answerService;
    private QuestionService questionService;

    @Autowired
    public VoteLogListener(AnswerService answerService, QuestionService questionService) {
        this.answerService = answerService;
        this.questionService = questionService;
    }

    @PostPersist
    public void updateEntityScoreWhenCreated(VoteLog log){
        if(log.getAnswer() != null){
            answerService.updateAnswerScore(log.getAnswer(), log.getScore());
        }
        if(log.getQuestion() != null){
            questionService.updateQuestionScore(log.getQuestion(), log.getScore());
        }
    }

    @PreRemove
    public void updateEntityScoreWhenDeleted(VoteLog log){
        if(log.getAnswer() != null){
            answerService.updateAnswerScore(log.getAnswer(), -log.getScore());
        }
        if(log.getQuestion() != null){
            questionService.updateQuestionScore(log.getQuestion(), -log.getScore());
        }
    }
}
