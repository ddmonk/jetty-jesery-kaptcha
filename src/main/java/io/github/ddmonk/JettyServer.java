package io.github.ddmonk;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.ddmonk.resource.KaptchaResource;

/**
 * Created by ddmonk on 2017/2/21.
 */
public class JettyServer {

  public static Logger LOG = LoggerFactory.getLogger(JettyServer.class);

  public static void main(String[] args) throws Exception {
    long startTime = System.currentTimeMillis();

    Server server = new Server(8080);
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/api/");
    ResourceHandler resource_handler = new ResourceHandler();

    // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
    // In this example it is the current directory but it can be configured to anything that the jvm has access to.
    resource_handler.setDirectoriesListed(true);
    resource_handler.setWelcomeFiles(new String[]{ "index.html" });
    resource_handler.setResourceBase(Resource.newClassPathResource("index.html").toString());// 指定服务的资源根路径，配置文件的相对路径与服务根路径有关
    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[]{context,resource_handler});

    server.setHandler(handlers);
    ServletHolder sh = new ServletHolder(ServletContainer.class);
    sh.setInitParameter(
        "jersey.config.server.provider.classnames",
        KaptchaResource.class.getCanonicalName());
    context.addServlet(sh, "/*");

    server.start();
    server.join();
    LOG.info("start ..... ");
  }

}
