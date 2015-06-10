package dk.nodes.webservice.models;
/**
 * @author Thomas Nielsen 2012
 */

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class NProgressMultipartEntity extends MultipartEntity {
	
	private final NProgressListener listener;
	
	public NProgressMultipartEntity(final NProgressListener listener) {
		super();
		this.listener = listener;
	}
	
	public NProgressMultipartEntity(final HttpMultipartMode mode, final NProgressListener listener) {
		super(mode);
		this.listener = listener;
	}
	
	public NProgressMultipartEntity(HttpMultipartMode mode, final String boundary, final Charset charset, final NProgressListener listener) {
		super(mode, boundary, charset);
		this.listener = listener;
	}
	
	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream, this.listener));
	}
	
	public static interface NProgressListener {
		void transferred(long num);
	}
	
	public static class CountingOutputStream extends FilterOutputStream {
		
		private final NProgressListener listener;
		private long transferred;
		
		public CountingOutputStream(final OutputStream out, final NProgressListener listener) {
			super(out);
			this.listener = listener;
			this.transferred = 0;
		}
		
		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
			this.transferred += len;
			if (this.listener != null)
				this.listener.transferred(this.transferred);
		}
		
		public void write(int b) throws IOException {
			out.write(b);
			this.transferred++;
			if (this.listener != null)
				this.listener.transferred(this.transferred);
		}
	}
}