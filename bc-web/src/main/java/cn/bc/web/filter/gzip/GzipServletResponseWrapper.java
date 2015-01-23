package cn.bc.web.filter.gzip;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class GzipServletResponseWrapper extends HttpServletResponseWrapper {

    // ----------------------------------------------------- Constructor

    /**
     * Calls the parent constructor which creates a ServletResponse adaptor
     * wrapping the given response object.
     *
     * @param response the servlet response to wrap
     * @param request The associated request
     */
    public GzipServletResponseWrapper(HttpServletResponse response, HttpServletRequest request) {
        super(response);
        origResponse = response;
        origRequest = request;
    }

    // ----------------------------------------------------- Instance Variables

    /** Original response. */
    protected HttpServletResponse origResponse = null;

    /** The request with which this servlet is associated. */
    protected HttpServletRequest origRequest;

    /** Descriptive information about this Response implementation. */
    protected static final String INFO = "CompressionServletResponseWrapper";

    /**
     * The ServletOutputStream that has been returned by
     * <code>getOutputStream()</code>, if any.
     */
    protected ServletOutputStream stream = null;

    /**
     * The PrintWriter that has been returned by
     * <code>getWriter()</code>, if any.
     */
    protected PrintWriter writer = null;

    /** The threshold number to compress. */
    protected int threshold = 0;

    /** Debug level. */
    private int debug = 0;

    /** Content type. */
    protected String contentType = null;

    // --------------------------------------------------------- Public Methods

    /**
     * Set content type.
     *
     * @param contentType the response content type
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
        origResponse.setContentType(contentType);
    }

    /**
     * Set threshold the compression threshold in bytes.
     *
     * @param threshold the compression threshold in bytes
     */
    public void setCompressionThreshold(int threshold) {
        this.threshold = threshold;
    }

    /**
     * Create and return a ServletOutputStream to write the content
     * associated with this Response.
     *
     * @return a new compressed servlet output stream
     * @exception IOException if an input/output error occurs
     */
    public ServletOutputStream createOutputStream() throws IOException {

        GzipResponseStream stream =
            new GzipResponseStream(origResponse, origRequest);
        stream.setBuffer(threshold);

        return stream;
    }

    /**
     * Finish a response.
     */
    public void finishResponse() {
        try {
            if (writer != null) {
                writer.close();
            } else {
                if (stream != null) {
                    stream.close();
                }
            }
        } catch (IOException e) {
        }
    }

    // ------------------------------------------------ ServletResponse Methods

    /**
     * Flush the buffer and commit this response.
     *
     * @exception IOException if an input/output error occurs
     */
    public void flushBuffer() throws IOException {
        // CLK-323 fix NPE
        if (writer != null) {
            writer.flush();
        } else if (stream != null) {
            stream.flush();
        }
    }

    /**
     * Return the servlet output stream associated with this Response.
     *
     * @return the servlet output stream associated with this response
     * @exception IllegalStateException if <code>getWriter</code> has
     *  already been called for this response
     * @exception IOException if an input/output error occurs
     */
    public ServletOutputStream getOutputStream() throws IOException {

        if (writer != null) {
            String msg =
                "getWriter() has already been called for this response";
            throw new IllegalStateException(msg);
        }

        if (stream == null) {
            stream = createOutputStream();
        }

        return (stream);
    }

    /**
     * Return the writer associated with this Response.
     *
     * @return the servlet print writer
     * @exception IllegalStateException if <code>getOutputStream</code> has
     *  already been called for this response
     * @exception IOException if an input/output error occurs
     */
    public PrintWriter getWriter() throws IOException {

        if (writer != null) {
            return (writer);
        }

        if (stream != null) {
            String msg =
                "getOutputStream() has already been called for this response";
            throw new IllegalStateException(msg);
        }

        stream = createOutputStream();
        if (debug > 1) {
            System.out.println("stream is set to " + stream + " in getWriter");
        }
        //String charset = getCharsetFromContentType(contentType);
        String charEnc = origResponse.getCharacterEncoding();

        // HttpServletResponse.getCharacterEncoding() shouldn't return null
        // according the spec, so feel free to remove that "if"
        if (charEnc != null) {
            writer = new PrintWriter(new OutputStreamWriter(stream, charEnc));
        } else {
            writer = new PrintWriter(stream);
        }

        return (writer);
    }

    /**
     * Set the content length. This method does nothing.
     *
     * @param length the content length
     */
    public void setContentLength(int length) {
    }

    /**
     * Set the int value in the header.
     *
     * @param header the response header
     * @param value the int value
     */
    public void setIntHeader(String header, int value) {
        if (!"Content-Length".equals(header)) {
            super.setIntHeader(header, value);
        }
    }

    /**
     * Set the string value in the header.
     *
     * @param header the response header
     * @param value the string value
     */
    public void setHeader(String header, String value) {
        if (!"Content-Length".equals(header)) {
            super.setHeader(header, value);
        }
    }

}