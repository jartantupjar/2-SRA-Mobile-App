/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Farmer;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;
import db.ForumDB;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig
public class SubmitPost extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String title = request.getParameter("title");
        String message = request.getParameter("message");
        String phase = request.getParameter("phase");
        int fieldID = Integer.parseInt(request.getParameter("fieldID"));
        ArrayList<String> imagePath = new ArrayList<>();
        Date date = new Date(Long.parseLong(request.getParameter("date")));
        Iterable<Part> parts = request.getParts();
        OutputStream out = null;
        InputStream filecontent = null;
        final PrintWriter writer = response.getWriter();
        for (Part part : parts) {
            if (!(part.getName().equalsIgnoreCase("title")|| 
                    part.getName().equalsIgnoreCase("message")||
                    part.getName().equalsIgnoreCase("fieldID") ||
                    part.getName().equalsIgnoreCase("date")||
                    part.getName().equalsIgnoreCase("phase")))
            try {
                //C:\\Users\\Bryll Joey Delfin\\Documents\\NetBeansProjects\\Reality\\web\\dist\\img
                //C:\\Users\\micha\\Desktop\\Thesis\\Images
                out = new FileOutputStream(new File("C:\\\\Users\\\\michaelggoch\\\\Documents\\\\NetBeansProjects\\\\Reality\\\\web\\\\dist\\\\img" + File.separator
                        + part.getSubmittedFileName()));
                filecontent = part.getInputStream();
                int read = 0;
                final byte[] bytes = new byte[1024];

                while ((read = filecontent.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                imagePath.add("dist\\img\\"
                        + part.getSubmittedFileName());
            } catch (FileNotFoundException fne) {
                writer.println("You either did not specify a file to upload or are "
                        + "trying to upload a file to a protected or nonexistent "
                        + "location.");
                writer.println("<br/> ERROR: " + fne.getMessage());

                LOGGER.log(Level.SEVERE, "Problems during file upload. Error: {0}",
                        new Object[]{fne.getMessage()});
            } finally {
                if (out != null) {
                    out.close();
                }
                if (filecontent != null) {
                    filecontent.close();
                }
                if (writer != null) {
                    writer.close();
                }
            }
        }
        new ForumDB().submitPost(title, message, fieldID, imagePath, date, phase);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
