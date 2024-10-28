package jwp.controller;

import core.mvc.Controller;
import jwp.dao.QuestionDao;
import jwp.model.Question;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;

public class UpdateQuestionController implements Controller {

    QuestionDao questionDao = new QuestionDao();

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Question modifiedQuestion = new Question(
                Long.parseLong(req.getParameter("questionId")),
                req.getParameter("writer"),
                req.getParameter("title"),
                req.getParameter("contents"),
                new Timestamp(System.currentTimeMillis()),
                0);

        questionDao.update(modifiedQuestion);
        return "redirect:/";
    }
}
