package jwp.controller.user;

import core.mvc.controller.ControllerV2;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.Map;

public class LogoutControllerV2 implements ControllerV2 {

    private HttpSession session;

    @Override
    public void setSession(HttpSession session) {
        this.session = session;
    }

    @Override
    public String execute(Map<String, String> params, Map<String, Object> model) throws SQLException {
        session.removeAttribute("user");
        return "redirect:/";
    }
}