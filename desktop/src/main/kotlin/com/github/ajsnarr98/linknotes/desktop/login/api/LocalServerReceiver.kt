package com.github.ajsnarr98.linknotes.desktop.login.api

import com.github.ajsnarr98.linknotes.network.result.ResultStatus
import com.sun.net.httpserver.*
import kotlinx.coroutines.withTimeout
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.nio.charset.StandardCharsets
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * This class is based on [com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver].
 *
 * OAuth 2.0 verification code receiver that runs an HTTP server on a free port,
 * waiting for a redirect with the verification code.
 */
class LocalServerReceiver(
    private val host: String = "localhost",
    private var port: Int = -1,
    private val callbackPath: String = "/Callback",
) : OAuthVerificationCodeReceiver {

    private var server: HttpServer? = null
    private val callbackHandler = CallbackHandler(callbackPath = callbackPath)

    @Throws(IOException::class)
    override fun initialize() {
        val server = HttpServer.create(InetSocketAddress(if (port != -1) port else findOpenPort()), 0).apply {
            createContext(callbackPath, callbackHandler)
            executor = null
        }
        this.server = server

        server.start()

        port = server.address.port
    }

    override fun getRedirectUri(): String {
        return "http://" + this.host + ":" + port + callbackPath
    }

    override suspend fun waitForCode(timeoutMillis: Long): ResultStatus<String, String> {
        return if (timeoutMillis == -1L) {
            suspendCoroutine { continuation -> callbackHandler.waitForCode(continuation) }
        } else {
            withTimeout(timeoutMillis) {
                suspendCoroutine { continuation -> callbackHandler.waitForCode(continuation) }
            }
        }
    }

    @Throws(IOException::class)
    override fun close() {
        server?.stop(0)
        server = null
    }

    /**
     * Copied from Jetty findFreePort() as referenced by: https://gist.github.com/vorburger/3429822
     */
    private fun findOpenPort(): Int {
        try {
            ServerSocket(0).use { socket ->
                socket.setReuseAddress(true)
                return socket.getLocalPort()
            }
        } catch (e: IOException) {
            throw IllegalStateException("No free TCP/IP port to start embedded HTTP Server on")
        }
    }


    internal class CallbackHandler(
        private val callbackPath: String,
    ) : HttpHandler {

        private var code: String? = null
        private var error: String? = null
        private var exception: Throwable? = null

        private var coroutineContinuation: Continuation<ResultStatus<String, String>>? = null

        /**
         * Wait for the code, or an error. Call in suspendCoroutine block.
         */
        fun waitForCode(continuation: Continuation<ResultStatus<String, String>>) {
            this.coroutineContinuation = continuation
            tryResumeCoroutineContinuation()
        }

        /**
         * Try to resume using the success code or error if there is a continuation set.
         */
        private fun tryResumeCoroutineContinuation() {
            coroutineContinuation ?: return
            val code = code
            val error = error
            val exception = exception
            if (code != null) {
                coroutineContinuation?.resume(ResultStatus.Success(code))
            } else if (error != null) {
                coroutineContinuation?.resume(ResultStatus.Error(error))
            } else if (exception != null) {
                coroutineContinuation?.resumeWithException(exception)
            }
        }

        @Throws(IOException::class)
        override fun handle(httpExchange: HttpExchange) {
            if (callbackPath != httpExchange.requestURI.getPath()) {
                return
            }
            try {
                val parms = queryToMap(httpExchange.requestURI.getQuery())
                error = parms["error"]
                code = parms["code"]
                val respHeaders = httpExchange.responseHeaders
                writeLandingHtml(httpExchange, respHeaders)
                httpExchange.close()
            } catch (t: Throwable) {
                exception = t
            } finally {
                tryResumeCoroutineContinuation()
            }
        }

        private fun queryToMap(query: String?): Map<String, String> {
            val result: MutableMap<String, String> = HashMap()
            if (query != null) {
                for (param in query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    val pair = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (pair.size > 1) {
                        result[pair[0]] = pair[1]
                    } else {
                        result[pair[0]] = ""
                    }
                }
            }
            return result
        }

        @Throws(IOException::class)
        private fun writeLandingHtml(exchange: HttpExchange, headers: Headers) {
            exchange.responseBody.use { os ->
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0)
                headers.add("ContentType", "text/html")
                val doc =
                    OutputStreamWriter(os, StandardCharsets.UTF_8)
                doc.write("<html>")
                doc.write("<head><title>OAuth 2.0 Authentication Token Received</title></head>")
                doc.write("<body>")
                doc.write("Received verification code. You may now close this window.")
                // normally window.close() does not work (for security reasons?)
//                doc.write("<script>")
//                doc.write("window.open('', '_self', ''); window.close();")
//                doc.write("</script>")
                doc.write("</body>")
                doc.write("</html>\n")
                doc.flush()
            }
        }
    }

}