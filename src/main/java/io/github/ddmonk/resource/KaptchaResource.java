package io.github.ddmonk.resource;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by ddmonk on 2017/2/21.
 */
@Path("hello")
public class KaptchaResource {

  private Properties props = new Properties();
  private Producer kaptchaProducer = null;
  private String sessionKeyValue = null;
  private String sessionKeyDateValue = null;

  public KaptchaResource() {
    /**
     * 配置参数
     */
    props.setProperty("kaptcha.textproducer.char.length", "4");
//    props.setProperty("kaptcha.textproducer.char.string", "我是顾刚");
//    props.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
    props.setProperty("kaptcha.image.width", "200");
    props.setProperty("kaptcha.image.height", "50");
//    props.setProperty("kaptcha.textproducer.font.size", "25");

    Config config1 = new Config(this.props);
    this.kaptchaProducer = config1.getProducerImpl();
    this.sessionKeyValue = config1.getSessionKey();
    this.sessionKeyDateValue = config1.getSessionDate();
  }



  @GET
  @Path("/captcha-image")
  @Produces("image/jpeg")
  public void captcha(@Context HttpServletRequest req, @Context HttpServletResponse resp) throws
                                                                                          ServletException,
                                                                                          IOException {
    resp.setHeader("Cache-Control", "no-store, no-cache");
    resp.setContentType("image/jpeg");
    String capText = this.kaptchaProducer.createText();
    req.getSession().setAttribute(this.sessionKeyValue, capText);
    req.getSession().setAttribute(this.sessionKeyDateValue, new Date());
    BufferedImage bi = this.kaptchaProducer.createImage(capText);
    ServletOutputStream out = resp.getOutputStream();
    ImageIO.write(bi, "jpg", out);
    req.getSession().setAttribute(this.sessionKeyValue, capText);
    req.getSession().setAttribute(this.sessionKeyDateValue, new Date());
  }

  @POST
  @Path("/test")
  @Consumes("application/x-www-form-urlencoded")
  public Response registerPost(@Context HttpServletRequest request,@FormParam("test") String pass) {
    if (!pass.equals(getGeneratedKey(request)))
      throw new RuntimeException("bad captcha");
    return Response.ok().build();
  }

  public String getGeneratedKey(HttpServletRequest req) {
    HttpSession session = req.getSession();
    return (String)session.getAttribute("KAPTCHA_SESSION_KEY");
  }

}
