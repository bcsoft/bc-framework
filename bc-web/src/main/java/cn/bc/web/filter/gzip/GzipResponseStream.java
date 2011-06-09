package cn.bc.web.filter.gzip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GzipResponseStream extends ServletOutputStream {

    // ----------------------------------------------------- Instance Variables

    /**
     * The threshold number which decides to compress or not.
     * Users can configure in web.xml to set it to fit their needs.
     */
    protected int compressionThreshold = 0;

    /** Debug level. */
    private int debug = 0;

    /**
     * The buffer through which all of our output bytes are passed.
     */
    protected byte[] buffer = null;

    /** The number of data bytes currently in the buffer. */
    protected int bufferCount = 0;

    /**
     * The underlying gzip output stream to which we should write data.
     */
    protected OutputStream gzipstream = null;

    /** Has this stream been closed? */
    protected boolean closed = false;

    /**
     * The content length past which we will not write, or -1 if there is
     * no defined content length.
     */
    protected int length = -1;

    /** The response with which this servlet output stream is associated. */
    protected HttpServletResponse response = null;

    /** The request with which this servlet is associated. */
    protected HttpServletRequest request;

    /**
     * The underlying output stream, either gzipped or servlet, to which we
     * should write data.
     */
    protected OutputStream output = null;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a servlet output stream associated with the specified Response.
     *
     * @param response The associated response
     * @param request The associated request
     * @throws IOException if an IO error occurs reading the response stream
     */
    public GzipResponseStream(HttpServletResponse response,
        HttpServletRequest request) throws IOException {

        super();
        this.closed = false;
        this.response = response;
        this.request = request;
        this.output = response.getOutputStream();
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Set the compressionThreshold number and create buffer for this size.
     *
     * @param threshold the compression threshold in bytes
     */
    protected void setBuffer(int threshold) {
        compressionThreshold = threshold;
        buffer = new byte[compressionThreshold];
    }

    /**
     * Close this output stream, causing any buffered data to be flushed.
     * Consecutive calls to this method will be ignored.
     *
     * @throws IOException if an error occurs closing the response
     */
    public void close() throws IOException {

        if (!closed) {

            // Don't close if this is a server side include
            if (request.getAttribute("javax.servlet.include.request_uri") != null) {
                flush();

            } else {
                if (gzipstream != null) {
                    flushToGZip();
                    gzipstream.close();
                    gzipstream = null;
                } else {
                    if (bufferCount > 0) {
                        if (debug > 2) {
                            System.out.print("output.write(");
                            System.out.write(buffer, 0, bufferCount);
                            System.out.println(")");
                        }
                        output.write(buffer, 0, bufferCount);
                        bufferCount = 0;
                    }
                }

                output.close();
                output = null;
                closed = true;
            }
        }
    }

    /**
     * Flush any buffered data for this output stream, which also causes the
     * response to be committed.
     *
     * @throws IOException if an error occurs flushing the gzip stream
     */
    public void flush() throws IOException {

        if (!closed) {

            if (gzipstream != null) {
                gzipstream.flush();
            }
        }
    }

    /**
     * Flush the buffer to the gzip stream.
     *
     * @throws IOException if an error occurs flushing the buffer
     */
    public void flushToGZip() throws IOException {

        if (bufferCount > 0) {
            writeToGZip(buffer, 0, bufferCount);
            bufferCount = 0;
        }
    }

    /**
     * Write the specified byte to our output stream.
     *
     * @param b The byte to be written
     *
     * @exception IOException if an input/output error occurs
     */
    public void write(int b) throws IOException {

        if (closed) {
            throw new IOException("Cannot write to a closed output stream");
        }

        if (bufferCount >= buffer.length) {
            flushToGZip();
        }

        buffer[bufferCount++] = (byte) b;
    }

    /**
     * Write <code>b.length</code> bytes from the specified byte array
     * to our output stream.
     *
     * @param b The byte array to be written
     * @exception IOException if an input/output error occurs
     */
    public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }

    /**
     * Write <code>len</code> bytes from the specified byte array, starting
     * at the specified offset, to our output stream.
     *
     * @param b The byte array containing the bytes to be written
     * @param off Zero-relative starting offset of the bytes to be written
     * @param len The number of bytes to be written
     * @exception IOException if an input/output error occurs
     */
    public void write(byte b[], int off, int len) throws IOException {

        if (closed) {
            throw new IOException("Cannot write to a closed output stream");
        }

        if (len == 0) {
            return;
        }

        // Can we write into buffer ?
        if (len <= (buffer.length - bufferCount)) {
            System.arraycopy(b, off, buffer, bufferCount, len);
            bufferCount += len;
            return;
        }

        // There is not enough space in buffer. Flush it ...
        flushToGZip();

        // ... and try again. Note, that bufferCount = 0 here !
        if (len <= (buffer.length - bufferCount)) {
            System.arraycopy(b, off, buffer, bufferCount, len);
            bufferCount += len;
            return;
        }

        // write direct to gzip
        writeToGZip(b, off, len);
    }

    /**
     * Writes array of bytes to the compressed output stream. This method
     * will block until all the bytes are written.
     *
     * @param b the data to be written
     * @param off the start offset of the data
     * @param len the length of the data
     * @throws IOException If an I/O error has occurred.
     */
    public void writeToGZip(byte b[], int off, int len) throws IOException {
        initializeGzip();
        gzipstream.write(b, off, len);
    }

    /**
     * Has this response stream been closed?
     *
     * @return true if the response stream has been closed
     */
    public boolean closed() {
        return (this.closed);
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Initialize the GZip output stream.
     * <p/>
     * This method delegates to {@link #setContentEncodingGZip()} to set the
     * GZip response Content-Encoding header.
     *
     * @throws IOException If an I/O error has occurred
     */
    protected void initializeGzip() throws IOException {

        if (gzipstream == null) {

            if (debug > 1) {
                System.out.println("new GZIPOutputStream");
            }

            if (response.isCommitted()) {
                if (debug > 1) {
                    System.out.print("Response already committed. Using original"
                        + " output stream");
                }
                gzipstream = output;

            } else if (setContentEncodingGZip()) {
                // If we can set the Content-Encoding header to gzip, create a
                // new gzip stream
                gzipstream = new GZIPOutputStream(response.getOutputStream());

            } else {
                // If we cannot set the Content-Encoding header, use original
                // output stream
                gzipstream = output;
            }
        }
    }

    /**
     * Set the "<tt>Content-Encoding</tt>" header of the response to
     * "<tt>gzip</tt>", returning true if the header was set, false otherwise.
     * <p/>
     * This method will return false when it is invoked from a server side
     * include (&lt;jsp:include&gt;), since its not possible to alter the headers
     * of an included response.
     *
     * @return true if the content encoding was set, false otherwise
     */
    protected boolean setContentEncodingGZip() {
        response.addHeader("Content-Encoding", "gzip");
        response.addHeader("Vary", "Accept-Encoding");
        return response.containsHeader("Content-Encoding");
    }
}

