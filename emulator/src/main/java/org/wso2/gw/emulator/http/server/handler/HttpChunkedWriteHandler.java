package org.wso2.gw.emulator.http.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.log4j.Logger;
import org.wso2.gw.emulator.http.server.contexts.HttpServerInformationContext;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * HttpChunkedWriteHandler
 */
public class HttpChunkedWriteHandler extends ChunkedWriteHandler {
    private static final Logger log = Logger.getLogger(HttpChunkedWriteHandler.class);
    static Callable callable = new Callable() {
        public Object call() throws Exception {
            return "Writing";
        }
    };
    private final HttpServerInformationContext serverInformationContext;
    private final ScheduledExecutorService scheduledWritingExecutorService;
    private final int corePoolSize = 10;

    public HttpChunkedWriteHandler(HttpServerInformationContext serverInformationContext) {
        this.serverInformationContext = serverInformationContext;
        scheduledWritingExecutorService = Executors.newScheduledThreadPool(corePoolSize);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        Boolean writingConnectionDrop = serverInformationContext.getServerConfigBuilderContext()
                .getWritingConnectionDrop();
        if (writingConnectionDrop != null && writingConnectionDrop == true) {
            ctx.channel().close();
            log.info("101505--Connection dropped/closed while writing data to channel");
        }

        int writeTimeOut = serverInformationContext.getServerConfigBuilderContext().getWriteTimeOut();

        if (writeTimeOut == 0) {
            writeTimeOut = Integer.MAX_VALUE;
        }

        Thread thread = new Thread(() -> {
            try {
                if (ctx.channel().isWritable()) {
                    HttpChunkedWriteHandler.super.write(ctx, msg, promise);
                } else {
                    log.info("101000--Receiver input/output error sending");
                }

            } catch (Exception e) {
                log.error(e);
            }
            waitingDelay(serverInformationContext.getServerConfigBuilderContext().getWritingDelay());
        });
        thread.start();
        long endTimeMillis = System.currentTimeMillis() + writeTimeOut;
        while (thread.isAlive()) {
            if (System.currentTimeMillis() > endTimeMillis) {
                ctx.channel().close();
                log.info("101504--Connection timeout occurred while writing data to the Channel");
                break;
            }
        }
    }

    private void waitingDelay(int delay) {
        if (delay != 0) {

            ScheduledFuture scheduledWaitingFuture = scheduledWritingExecutorService
                    .schedule(callable, delay, TimeUnit.MILLISECONDS);
            try {
                scheduledWaitingFuture.get();
            } catch (InterruptedException e) {
                log.error(e);
            } catch (ExecutionException e) {
                log.error(e);
            }
            //scheduledWritingExecutorService.shutdown();
        }
    }
}
