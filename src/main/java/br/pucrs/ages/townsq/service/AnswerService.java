package br.pucrs.ages.townsq.service;

import br.pucrs.ages.townsq.model.Answer;
import br.pucrs.ages.townsq.model.Comment;
import br.pucrs.ages.townsq.model.Question;
import br.pucrs.ages.townsq.model.User;
import br.pucrs.ages.townsq.repository.AnswerRepository;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

@Service
public class AnswerService {

    private AnswerRepository answerRepository;

    @Autowired
    public AnswerService(AnswerRepository answerRepository) { this.answerRepository = answerRepository; }

    /**
     * Saves an Answer on the database
     * @param answer Answer
     * @return Answer
     */
    public Answer saveAnswer(Answer answer, User user, Question question) {
        if(StringUtils.isEmpty(answer.getText().trim()))
            throw new IllegalArgumentException("O texto da resposta não pode estar vazio");
        answer.setQuestion(question);
        answer.setUser(user);
        return this.answerRepository.save(answer);
    }


    public Answer editAnswer(String answer,
                               User user,
                               Long id){
        Answer databaseAnswer = answerRepository.findById(id).orElse(null);
        if(StringUtils.isEmpty(answer.trim()) || databaseAnswer == null || !databaseAnswer.getUser().getId().equals(user.getId())){
            throw new IllegalArgumentException("Não foi possível editar a resposta.");
        }
        databaseAnswer.setText(answer);
        return answerRepository.save(databaseAnswer);
    }




    /**
     * Performs a soft delete of a answer if the user is it's creator
     * @param userId
     * @param answerId
     * @return boolean
     */
    public boolean delete(long userId, long answerId) {
        Answer answer = answerRepository.findById(answerId).orElse(null);
        if(answer != null) {
            if(answer.getUser().getId() == userId && answer.getIsActive() == 1) {
                answer.setIsActive(0);
                answerRepository.save(answer);
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * Returns an answer if it exists, given it's id
     * @param id Answer id
     * @return Optional of Answer
     */
    public Optional<Answer> findById(long id){ return answerRepository.findById(id); }

    /**
     * Gets the answers to be displayed on Question screen
     * @return List of answers
     */
    public List<Answer> getQuestionAnswers(Question question) {
        return answerRepository.findByIsActiveAndQuestionEqualsOrderByCreatedAtDesc(1, question);
    }

    public Answer favoriteAnswer(User user,
                                 Long id,
                                 Question questionFrom) {

        Answer favoritedAnswer = questionFrom.getFavoriteAnswer().orElse(null);

        if (favoritedAnswer != null && favoritedAnswer.getId().equals(id)) {
            favoritedAnswer.setIsBest(0);
            return answerRepository.save(favoritedAnswer);
        }
        // se ja existe uma answer facoritada e não for a que estamos tentando alterar, tratar em outro if()

        if (questionFrom.getUser().getId().equals(user.getId())) {
            Answer databaseAnswer = answerRepository.findById(id).orElse(null);
            databaseAnswer.setIsBest(1);
            return answerRepository.save(databaseAnswer);
        } else {
            throw new SecurityException("Você não pode favoritar esta resposta.");
        }
    }
}
