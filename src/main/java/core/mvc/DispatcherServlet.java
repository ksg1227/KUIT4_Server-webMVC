package core.mvc;

import core.mvc.adapter.HandlerAdapter;
import core.mvc.adapter.HandlerAdapterV1;
import core.mvc.adapter.HandlerAdapterV2;
import core.mvc.controller.Controller;
import core.mvc.view.ModelAndView;
import core.mvc.view.View;
import jwp.model.User;
import jwp.util.UserSessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "dispatcher", urlPatterns = "/", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {
    private RequestMapping requestMapping;
    private ViewResolver viewResolver;
    private final List<HandlerAdapter> handlerAdapters = new ArrayList<>();

    @Override
    public void init() {
        requestMapping = new RequestMapping();
        viewResolver = new ViewResolver();
        initHandlerAdapters();
    }

    private void initHandlerAdapters() {
        handlerAdapters.add(new HandlerAdapterV1());
        handlerAdapters.add(new HandlerAdapterV2());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. RequestMapping 을 통해 실행할 Handler(Controller)조회
        Controller controller = getHandler(req, resp);
        if (controller == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 2. HandlerAdapter 조회 및 실행
        HandlerAdapter adapter = getHandlerAdapter(controller);
        ModelAndView mav;
        try {
            mav = adapter.handle(req, resp, controller);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // 3. ViewResolver 를 통해 viewName 을 View 객체로 변환
        String viewName = mav.getViewName();
        if (viewName == null) return;

        View view = viewResolver.getView(viewName);
        view.render(mav.getModel(), req, resp);
    }


    private Controller getHandler(HttpServletRequest req, HttpServletResponse resp) {
        String url = req.getRequestURI();
        Controller controller = requestMapping.getController(url);
        if (controller == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        setControllerFields(req, controller);
        return controller;
    }


    private HandlerAdapter getHandlerAdapter(Controller controller) {
        for (HandlerAdapter adapter : handlerAdapters) {
            if (adapter.supports(controller)) {
                return adapter;
            }
        }
        throw new IllegalArgumentException("handler adapter를 찾을 수 없습니다. handler=" + controller);
    }

    private static void setControllerFields(HttpServletRequest request, Controller controller) {
        HttpSession session = request.getSession();
        controller.setSession(session);

        if (UserSessionUtils.isLogined(session)) {
            User userFromSession = UserSessionUtils.getUserFromSession(session);
            controller.setUserFromSession(userFromSession);
        }
    }
}
