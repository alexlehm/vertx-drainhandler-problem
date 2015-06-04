package io.vertx.ext.mail;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/*
 * smtp client test using vertx unit
 */
/**
 * @author <a href="http://oss.lehmann.cx/">Alexander Lehmann</a>
 *
 */
@RunWith(VertxUnitRunner.class)
public class DrainHandlerTest {

  private static final Logger log = LoggerFactory.getLogger(DrainHandlerTest.class);

  Vertx vertx = Vertx.vertx();

  @Test
  public void testServer(TestContext context) {
    log.info("starting");

    Async async = context.async();

    vertx.runOnContext(v -> {
      NetClientOptions netClientOptions = new NetClientOptions();
      NetClient netClient = vertx.createNetClient(netClientOptions);
      netClient.connect(1234, "localhost", result -> {
        if(result.succeeded()) {
          NetSocket socket = result.result();
          socket.drainHandler(v1 -> {
            log.info("drainHandler called");
          });
          writeLines(socket, 0, async);
//          vertx.setTimer(10000, v2 -> async.complete());
        }
      });
    });
  }

  /**
   * @param i
   */
  private void writeLines(NetSocket socket, int i, Async async) {
    while(i<2000) {
      if(socket.writeQueueFull()) {
        final int iFinal = i;
        log.info("setting drain handler, i=="+i);
        socket.drainHandler(v -> {
          log.info("drain handler called in writeLines, i=="+iFinal);
          socket.write(iFinal+"*****************************************************************************************\n");
          writeLines(socket, iFinal+1, async);
        });
        return;
      } else {
        socket.write(i+"*****************************************************************************************\n");
      }
      i++;
    }
//    socket.close();
    async.complete();
  }

  @Before
  public void before(TestContext context) {
    log.info("starting test server");
    Async async = context.async();
    testServer = new TestServer();
    testServer.startServer(vertx, v -> async.complete());
  }

  @After
  public void after(TestContext context) {
    log.info("stopping test server");
    testServer.stop();
  }

  private TestServer testServer;
}
