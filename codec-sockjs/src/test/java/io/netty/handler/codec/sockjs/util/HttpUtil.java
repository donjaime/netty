/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package io.netty.handler.codec.sockjs.util;

import static io.netty.handler.codec.http.HttpHeaders.Values.WEBSOCKET;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.mockito.Mockito.mock;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

import java.util.ArrayList;
import java.util.List;

public final class HttpUtil {

    private HttpUtil() {
    }

    public static HttpResponse decode(final Object obj) throws Exception {
        if (!(obj instanceof ByteBuf)) {
            throw new IllegalStateException("obj must be of type ByteBuffer");
        }
        return new ResponseDecoder().toHttpResponse((ByteBuf) obj);
    }

    public static FullHttpRequest webSocketUpgradeRequest(final String path, final WebSocketVersion version) {
        final FullHttpRequest req = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, path);
        req.headers().set(Names.HOST, "server.test.com");
        req.headers().set(Names.UPGRADE, WEBSOCKET.toLowerCase());
        req.headers().set(Names.CONNECTION, "Upgrade");
        req.headers().set(Names.SEC_WEBSOCKET_KEY, "dGhlIHNhbXBsZSBub25jZQ==");
        req.headers().set(Names.SEC_WEBSOCKET_ORIGIN, "http://test.com");
        req.headers().set(Names.SEC_WEBSOCKET_VERSION, version.toHttpHeaderValue());
        req.retain();
        return req;
    }

    private static class ResponseDecoder extends HttpResponseDecoder {

        public HttpResponse toHttpResponse(final ByteBuf buf) throws Exception {
            final ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
            final List<Object> out = new ArrayList<Object>();
            callDecode(ctx, buf, out);
            return (HttpResponse) out.get(0);
        }
    }

}
