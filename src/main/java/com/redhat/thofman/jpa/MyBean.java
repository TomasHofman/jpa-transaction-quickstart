package com.redhat.thofman.jpa;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import javax.transaction.Transactional;

@Stateless
@Transactional
public class MyBean {

    private final static Logger LOG = Logger.getLogger(MyBean.class.getName());

    private final static int ITERATIONS = 10;

    @Resource
    private DataSource dataSource;

    public void beanMethod(PrintWriter writer) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();

            for (int i = 0; i < ITERATIONS; i++) {
                PreparedStatement preparedStatement = connection.prepareStatement("select 1");

                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                int intCol = resultSet.getInt(1);
                LOG.info("Result: " + intCol);
                writer.println("Result: " + intCol);
                Thread.sleep(200);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Statement failed", e);
            e.printStackTrace(writer);
        }
    }

}
