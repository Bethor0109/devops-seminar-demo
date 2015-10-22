package com.capside.enterpriseseminar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import lombok.Cleanup;
import lombok.extern.java.Log;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Component;

/**
 *
 * @author ciberado
 */
@Log
@Component
public class DBInit implements ApplicationListener<ContextRefreshedEvent> {

    public void execute() {
        try {
            log.info("Opening connection to the database.");
            @Cleanup Connection con = DriverManager.getConnection("jdbc:derby:memory:football;create=true", "ROOT", "");
            @Cleanup BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    DBInit.class.getClassLoader().getResourceAsStream("fbgames.sql")));
            @Cleanup Statement stmt = con.createStatement();
            log.info("Loading data into local database.");
            String line;
            while ((line = in.readLine()) != null) {
                log.log(Level.FINE, "Executing line {0}", line);
                stmt.executeUpdate(line);
            }
            log.info("Database initialized successfully.");
        } catch (SQLException | IOException exc) {
            throw new DataAccessResourceFailureException("Error initializing database.", exc);
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent e) {
        this.execute();
    }

}