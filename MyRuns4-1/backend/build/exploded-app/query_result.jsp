<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*"%>
<%@ page import="edu.dartmouth.Zixiao.Myruns.backend.data.*"%>
<!DOCTYPE html>

<html>
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>ZixiaoLi - MyRuns</title>
    </head>
    <body>
      <div class="container">
        <h1>Exercise Entries List for your device</h1>

        <%
            String retStr = (String) request.getAttribute("_retStr");
            if (retStr != null) {
        %>
            <p><%=retStr%></p>
        <%
            }
        %>

        <table class="table table-hover">
          <thead>
            <tr>
              <th>ID</th>
              <th>Input Type</th>
              <th>Activity Type</th>
              <th>Date Time</th>
              <th>Duration</th>
              <th>Distance</th>
              <th>AveSpeed</th>
              <th>Calories</th>
              <th>Climb</th>
              <th>Heart Rate</th>
              <th>Comment</th>
              <th>Delete?</th>
            </tr>
          </thead>
          <tbody>

            <%
                List<ExerciseEntry> resultEntries = (List<ExerciseEntry>) request
                    .getAttribute("AllEntries");
                if (resultEntries != null) {
                    for (ExerciseEntry entry : resultEntries) {
            %>
                <tr>
                    <td><%=entry.indexID%></td>
                    <td><%=entry.mInputType%></td>
                    <td><%=entry.mActivityType%></td>
                    <td><%=entry.formatDate%></td>
                    <td><%=entry.mDuration%></td>
                    <td><%=entry.mDistance%></td>
                    <td><%=entry.mAvgSpeed%></td>
                    <td><%=entry.mCalorie%></td>
                    <td><%=entry.mClimb%></td>
                    <td><%=entry.mHeartRate%></td>
                    <td><%=entry.mComment%></td>
                    <td>
                        <a href="/delete.do?indexID=<%=entry.indexID%>">Delete</a>
                    </td>
                </tr>
            <%
                    }
                }
            %>

          </tbody>
        </table>

      </div>
    </body>
</html>