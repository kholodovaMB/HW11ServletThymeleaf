package org.HMB;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;
import java.util.TimeZone;

@WebFilter(urlPatterns = "/time")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
        String timezoneParameter = req.getParameter("timezone");
        if (timezoneParameter == null) {
            chain.doFilter(req, resp);
        } else if (isValidTimezone(timezoneParameter.replace(' ', '+'))) {
            chain.doFilter(req, resp);
        } else {
            resp.setContentType("text/html");
            resp.setStatus(400);
            PrintWriter out = resp.getWriter();
            out.write("Invalid timezone: " + timezoneParameter);
            out.close();
        }
    }
    private boolean isValidTimezone(String timezone) {
        return "UTC".equals(timezone) || Pattern.matches("UTC[+-](0?[0-9]|1[0-4])?", timezone)
                || TimeZone.getTimeZone(timezone).getID().equals(timezone);
    }
}
