package io.vertx.ext.mail;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;

/*
 * really dumb mock test server that just replays a number of lines
 * as response. this doesn't check any conditions at all.  
 */
public class TestServer {

  private static final Logger log = LoggerFactory.getLogger(TestServer.class);

  private NetServer netServer;

  public void startServer(Vertx vertx, Handler<Void> finished) {
    NetServerOptions nsOptions = new NetServerOptions();
    nsOptions.setPort(1234);
    netServer = vertx.createNetServer(nsOptions);

    netServer.connectHandler(socket -> {
      socket.handler(buffer -> {
        log.info("buffer: "+buffer);
      });
    });
    netServer.listen(v -> finished.handle(null));
  }

  public void stop() {
    if (netServer != null) {
      netServer.close();
      netServer = null;
    }
  }

}
