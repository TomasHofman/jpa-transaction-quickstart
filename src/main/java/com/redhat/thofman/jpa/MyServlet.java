package com.redhat.thofman.jpa;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

@WebServlet("/MyServlet")
public class MyServlet extends HttpServlet {

    private final static int ITERATIONS = 10;

    @Resource
    private DataSource dataSource;

    @Resource
    private UserTransaction userTransaction;

    @Inject
    private MyBean bean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");

        resp.getWriter().println("Executing SQL statement from bean.");
        bean.beanMethod(resp.getWriter());

        resp.getWriter().println("Executing SQL statement from servlet.");

        Connection connection = null;
        try {
            userTransaction.begin();

            connection = dataSource.getConnection();

            for (int i = 0; i < ITERATIONS; i++) {
                PreparedStatement preparedStatement = connection.prepareStatement("select 1");

                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                int intCol = resultSet.getInt(1);
                resp.getWriter().println("Result: " + intCol);
                Thread.sleep(200);
            }

            userTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace(resp.getWriter());
            try {
                userTransaction.rollback();
            } catch (SystemException e1) {
                e1.printStackTrace();
            }
//            throw new ServletException(e);
        }

        resp.getWriter().close();
    }
}
