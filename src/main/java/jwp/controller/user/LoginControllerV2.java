package jwp.controller.user;

import core.mvc.controller.ControllerV2;
import jwp.dao.UserDao;
import jwp.model.User;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.Map;

public class LoginControllerV2 implements ControllerV2 {

    private final UserDao userDao = new UserDao();
    private HttpSession session;

    @Override
    public void setSession(HttpSession session) {
        this.session = session;
    }


    @Override
    public String execute(Map<String, String> params, Map<String, Object> model) throws SQLException {
        String userId = params.get("userId");
        String password = params.get("password");
        User loginUser = new User(userId, password);
        User user = userDao.findByUserId(userId);

        if (user != null && user.isSameUser(loginUser)) {
            session.setAttribute("user", user);
            return "redirect:/";
        }
        return "redirect:/user/loginFailed";
    }
}