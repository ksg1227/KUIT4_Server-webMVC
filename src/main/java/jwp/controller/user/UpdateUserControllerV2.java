package jwp.controller.user;

import core.mvc.controller.ControllerV2;
import jwp.dao.UserDao;
import jwp.model.User;

import java.sql.SQLException;
import java.util.Map;

public class UpdateUserControllerV2 implements ControllerV2 {

    private final UserDao userDao = new UserDao();

    @Override
    public String execute(Map<String, String> params, Map<String, Object> model) throws SQLException {
        User modifiedUser = new User(
                params.get("userId"),
                params.get("password"),
                params.get("name"),
                params.get("email"));
        userDao.update(modifiedUser);
        return "redirect:/user/list";
    }
}