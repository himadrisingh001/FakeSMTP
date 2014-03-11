package com.nilhcem.fakesmtp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nilhcem.fakesmtp.mail.saver.InMemoryMailSaver;
import com.nilhcem.fakesmtp.server.SMTPInMemoryServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.get;
import static spark.Spark.setPort;

public class RestServer
{
  private static Logger log = LoggerFactory.getLogger(RestServer.class);

  private final SMTPInMemoryServerHandler serverHandler = SMTPInMemoryServerHandler.INSTANCE;
  private final ObjectMapper mapper = new ObjectMapper();

  public void start()
  {
    int port = Integer.parseInt(System.getProperty("web.port", "5000"));
    log.info(String.format("Starting %s at port %d", RestServer.class.getName(), port));
    log.info("IMPORTANT: You need sudo access to use ports below 1024.");
    setPort(port);
    get(
        new Route("/start")
        {
          @Override
          public Object handle(Request request, Response response)
          {
            int serverPort = 25;
            String port = request.queryParams("port");
            try {
              if (port == null || port.isEmpty()) {
                throw new RuntimeException("Port is null or empty: " + port);
              }
              serverPort = Integer.parseInt(port);
            }
            catch (Exception e) {
              log.error("Error in parsing port: " + port + ". Falling back to default: 25");
              response.status(406);
              return e.getMessage();
            }

            try {
              if (serverHandler.getSmtpServer().isRunning()) {
                response.status(409); // Conflict
                return "Already running at port : " + serverHandler.getSmtpServer().getPort();
              }

              serverHandler.startServer(serverPort);
            }
            catch (Exception e) {
              log.error("Unable to start the mail server.", e);
              return false;
            }
            return "true";
          }
        }
    );

    get(
        new Route("/stop")
        {
          @Override
          public Object handle(Request request, Response response)
          {
            serverHandler.stopServer();
            return true;
          }
        }
    );

    get(
        new Route("/getAll")
        {
          @Override
          public Object handle(Request request, Response response)
          {
            try {
              return mapper.writeValueAsString(((InMemoryMailSaver) serverHandler.getMailSaver()).getMails());
            }
            catch (JsonProcessingException e) {
              log.error("Unable to get all mails: " + e.getMessage());
              return "{}";
            }
          }
        }
    );

    get(
        new Route("/clear")
        {
          @Override
          public Object handle(Request request, Response response)
          {
            log.info("Removing all mails.");
            serverHandler.getMailSaver().deleteEmails();
            return true;
          }
        }
    );

    get(
        new Route("/kill")
        {
          @Override
          public Object handle(Request request, Response response)
          {
            new Thread(
                new Runnable()
                {
                  @Override
                  public void run()
                  {
                    try {
                      Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                      e.printStackTrace();
                    }
                    log.info("Got kill signal. Bye Bye!");
                    System.exit(0);
                  }
                }
            ).start();
            return true;
          }
        }
    );

    get(
        new Route("/status")
        {
          @Override
          public Object handle(Request request, Response response)
          {
            return serverHandler.getSmtpServer().isRunning();
          }
        }
    );

  }

  public static void main(String[] args)
  {
    new RestServer().start();
  }
}
