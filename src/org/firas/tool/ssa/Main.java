package org.firas.tool.ssa;

import java.io.PrintWriter;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * @author Wu Yuping
 */
public class Main extends HttpServlet {

    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
                 throws ServletException, java.io.IOException {
        final String command = req.getParameter("command");
        final PrintWriter writer = resp.getWriter();
        if ("match".equals(command) || "revertMatch".equals(command)) {
            final String big = "/data/" + req.getParameter("big");
            final String small = "/data/" + req.getParameter("small");
            final String x = req.getParameter("x");
            final String y = req.getParameter("y");
            final String x0 = req.getParameter("x0");
            final String y0 = req.getParameter("y0");
            if (x0 != null && !x0.isEmpty() && y0 != null && !y0.isEmpty()) {
                ImageMatcher.match(writer, big, small, Integer.valueOf(x), Integer.valueOf(y),
                        Integer.valueOf(x0), Integer.valueOf(y0), "revertMatch".equals(command));
            } else if (x != null && !x.isEmpty() && y != null && !y.isEmpty()) {
                ImageMatcher.match(writer, big, small, Integer.valueOf(x), Integer.valueOf(y),
                        9999, 9999, "revertMatch".equals(command));
            } else {
                ImageMatcher.match(writer, big, small, "revertMatch".equals(command));
            }
        } else if ("blackwhite".equals(command)) {
            final String src = "/data/" + req.getParameter("src");
            final String target = "/data/" + req.getParameter("target");
            BlackWhite.process(writer, src, target);
        }
    }
}
