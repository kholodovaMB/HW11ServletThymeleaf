package org.HMB;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private static Logger log = LoggerFactory.getLogger(TimeServlet.class);
    private String zoneIdUTC = "UTC";
    private String dateTime;
    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix(getClass().getClassLoader().getResource("templates").getPath());
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        String timeZone = parseTimezone(req, resp);

        dateTime = ZonedDateTime.now(ZoneId.of(timeZone)).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss"))
                + " " + timeZone;

        Context context = new Context(
                req.getLocale(),
                Map.of("dateTime", dateTime)
        );

        engine.process("time", context, resp.getWriter());
        resp.getWriter().close();
    }

    private String parseTimezone(HttpServletRequest request, HttpServletResponse resp) {

        if (request.getParameterMap().containsKey("timezone")) {
            String timeZone = request.getParameter("timezone").replace(" ", "+");
            resp.addCookie(new Cookie("lastTimezone", timeZone));
            return timeZone;
        }
        return isCookie(request);
    }

    private String isCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("lastTimezone")) {
                    return cookie.getValue();
                }
            }
        }
        return zoneIdUTC;
    }

    @Override
    public void destroy() {
        dateTime  = "";
    }

}