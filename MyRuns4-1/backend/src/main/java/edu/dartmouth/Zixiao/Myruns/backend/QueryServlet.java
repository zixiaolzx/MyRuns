package edu.dartmouth.Zixiao.Myruns.backend;

import java.io.IOException;
import java.security.ProtectionDomain;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.dartmouth.Zixiao.Myruns.backend.data.EntryDataStore;
import edu.dartmouth.Zixiao.Myruns.backend.data.ExerciseEntry;

/**
 * Created by Zizi on 2/22/2017.
 */

public class QueryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public static final String ALL_ENTRIES_KEY = "AllEntries";

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        List<ExerciseEntry> allEntries = EntryDataStore.fetchAll();
        request.setAttribute(ALL_ENTRIES_KEY, allEntries);
        getServletContext().getRequestDispatcher("/query_result.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        doGet(request, response);
    }
}

