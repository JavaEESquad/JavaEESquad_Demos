package be.c4j.ee.squad.demo;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 */
@WebServlet(urlPatterns = "/country")
public class CountryServlet extends HttpServlet {

    @Inject
    private CountryBoundary countryBoundary;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");

        Country country = null;
        if (code != null) {
            country = countryBoundary.findCountry(code);
        }

        ServletOutputStream outputStream = resp.getOutputStream();
        prepareResponse(code, country, outputStream);

        outputStream.flush();
    }

    private void prepareResponse(String code, Country country, ServletOutputStream outputStream) throws IOException {
        if (code == null) {
            outputStream.print("Please specify country code with code query parameter");
        } else {
            if (country == null) {
                outputStream.print("No country found for code, please have a look at http://www.nationsonline.org/oneworld/country_code_list.htm");
            } else {
                outputStream.print("Country = " + country.getDescription());
            }
        }
    }
}
