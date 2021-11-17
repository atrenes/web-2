import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.time.ZoneId;

public class AreaCheckServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long startTime = System.nanoTime();

        String xString = request.getParameter("x");
        String yString = request.getParameter("y");
        String rString = request.getParameter("r");

        if (validateX(xString) && validateY(yString) && validateR(rString)) {
            HttpSession session = request.getSession();
            if (session.getAttribute("rCount") == null) {
                session.setAttribute("rCount", "0");
            }

            String rCount = (String) session.getAttribute("rCount");

            double x = Double.parseDouble(xString);
            double y = Double.parseDouble(yString);
            double r = Double.parseDouble(rString);

            String runtime = String.valueOf(System.nanoTime() - startTime);

            String timestamp = LocalDateTime.now(ZoneId.of("Europe/Moscow")).format(DateTimeFormatter.ofPattern("d.MM.yyyy HH:mm"));

            session.setAttribute("x" + rCount, x);
            session.setAttribute("y" + rCount, y);
            session.setAttribute("r" + rCount, r);
            session.setAttribute("result" + rCount, checkHit(x, y, r));
            session.setAttribute("runtime" + rCount, runtime);
            session.setAttribute("timestamp" + rCount, timestamp);

            session.setAttribute("rCount", String.valueOf(Integer.parseInt(rCount)+1));

            PrintWriter pw = response.getWriter();

            if (request.getParameter("type") != null && request.getParameter("type").equals("ajax")) {
                response.setContentType("text/json; charset=UTF-8");
                pw.println("{\"x\": " + x + ", \"y\": " + y + ", \"r\": " + r + ", \"result\": \"" + checkHit(x, y, r) + "\", \"runtime\": " + runtime + ", \"timestamp\": \"" + timestamp + "\"}");
            }
        }

        //getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
    }

    private static boolean validateX(String xString) {
        try {
            Double[] xValues = {-2.0, -1.5, -1.0, -0.5, 0d, 0.5, 1.0, 1.5, 2.0};
            double x = Double.parseDouble(xString);
            return Arrays.asList(xValues).contains(x);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean validateY(String yString) {
        try {
            double y = Double.parseDouble(yString);
            return (y > -5 && y < 3 && yString.length() < 10);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean validateR(String rString) {
        try {
            double r = Double.parseDouble(rString);
            return (r > 2 && r < 5 && rString.length() < 10);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean checkHit(double x, double y, double r) {
        return (x <= 0 && y <= 0 && x >= -r && y >= -r/2) || // rectangle
                (x >= 0 && y >= 0 && x*x + y*y <= r*r/4) || // circle
                (x >= 0 && y <= 0 && y >= x-r/2); // triangle
    }
}
