package br.pucrs.ages.townsq.controller;

import br.pucrs.ages.townsq.model.Comment;
import br.pucrs.ages.townsq.model.Question;
import br.pucrs.ages.townsq.model.User;
import br.pucrs.ages.townsq.service.CommentService;
import br.pucrs.ages.townsq.service.EmailService;
import br.pucrs.ages.townsq.service.QuestionService;
import br.pucrs.ages.townsq.utils.Slugify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class CommentController {

    private final CommentService commentService;
    private final QuestionService questionService;
    private final EmailService emailService;


    @Autowired
    public CommentController(CommentService commentService, QuestionService questionService, EmailService emailService){
        this.commentService = commentService;
        this.questionService = questionService;
        this.emailService = emailService;
    }

    /**
     * Post route to create a comment
     * @param user User
     * @param comment Comment
     * @param question Question
     * @param creator String
     * @param creatorId long
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/comment/create/{creator}/{creatorId}")
    public String postCreateComment(@AuthenticationPrincipal User user,
                                    @ModelAttribute @Valid Comment comment,
                                    BindingResult bindingResult,
                                    @ModelAttribute Question question,
                                    @PathVariable String creator,
                                    @PathVariable long creatorId,
                                    final RedirectAttributes redirectAttributes) {
        Question questionOfComment = questionService.getQuestionById(question.getId()).orElse(null);
        if(questionOfComment == null){
            redirectAttributes.addFlashAttribute("error", "Operação inválida.");
            return "redirect:/";
        }
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("error", "Comentário inválido");
            return "redirect:/question/" + questionOfComment.getId() + "/" + Slugify.toSlug(questionOfComment.getTitle());
        }
        try{
            Comment createdComment = commentService.saveComment(comment, user, creator, creatorId);
            emailService.createEmail(createdComment);
            redirectAttributes.addFlashAttribute("success", "Comentário cadastrado com sucesso.");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error", "Não foi possível cadastrar o comentário.");
        }
        return "redirect:/question/" + questionOfComment.getId() + "/" + Slugify.toSlug(questionOfComment.getTitle());
    }

    /**
     * Edits a comment
     * @param user User
     * @param comment Comment
     * @param id long
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/comment/edit/{id}")
    public String postEditComment(
            @AuthenticationPrincipal User user,
            @ModelAttribute @Valid Comment comment,
            BindingResult bindingResult,
            @PathVariable long id,
            final RedirectAttributes redirectAttributes
    ){
        Question questionFrom = questionService.getQuestionById(comment.getQuestion().getId()).orElse(null);
        if(questionFrom == null){
            redirectAttributes.addFlashAttribute("error","Operação inválida.");
            return "redirect:/";
        }
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("error", "Comentário inválido.");
            return "redirect:/question/" + questionFrom.getId() +  "/" + Slugify.toSlug(questionFrom.getTitle());
        }
        try{
            commentService.editComment(comment.getText(),user,id);
            redirectAttributes.addFlashAttribute("success","Comentário editado com sucesso.");
        }catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("error",e.getMessage());
        }
        return "redirect:/question/" + questionFrom.getId() +  "/" + Slugify.toSlug(questionFrom.getTitle());
    }

    /**
     * Get route to delete comment
     * @param user User
     * @param questionId Long,
     * @param commentId Long
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @GetMapping("/comment/delete/{questionId}/{commentId}")
    public String getDeleteComment(@AuthenticationPrincipal User user,
                                   @PathVariable Long questionId,
                                   @PathVariable Long commentId,
                                   final RedirectAttributes redirectAttributes){
        Question question;
        if((question = questionService.getQuestionById(questionId).orElse(null)) == null){
            redirectAttributes.addFlashAttribute("error", "Operação inválida.");
            return "redirect:/";
        }
        if(commentService.deleteComment(commentId, user)){
            redirectAttributes.addFlashAttribute("success", "Comentário deletado com sucesso!");
        }
        else{
            redirectAttributes.addFlashAttribute("error", "Não foi possível deletar o comentário.");
        }
        return "redirect:/question/" + question.getId() + "/" + Slugify.toSlug(question.getTitle());
    }

    @ExceptionHandler({ BindException.class })
    public String handleException(final RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Comentário inválido.");
        return "redirect:/";
    }

}
