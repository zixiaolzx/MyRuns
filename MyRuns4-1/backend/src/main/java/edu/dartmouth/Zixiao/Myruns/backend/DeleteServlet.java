package edu.dartmouth.Zixiao.Myruns.backend;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;
import edu.dartmouth.Zixiao.Myruns.backend.data.EntryDataStore;
/**
 * Created by Zizi on 2/22/2017.
 */

public class DeleteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        String id = request.getParameter("indexID");
        //do not proceed if null
        if(id == null){
            return;
        }
        EntryDataStore.delete(id);
        MessagingEndpoint messagingEndpoint = new MessagingEndpoint();
        messagingEndpoint.sendMessage(id);
        response.sendRedirect("/query.do");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException{
        doGet(request, response);
    }
}
