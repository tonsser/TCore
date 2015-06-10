package dk.nodes.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class NSerialBitmap implements Serializable {

	public Bitmap bitmap;
	@Deprecated
	/**
	 * Don't use this, use the LruCache instead
	 * @param bitmap
	 */
	public NSerialBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	// Converts the Bitmap into a byte array for serialization
	@Deprecated
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
		byte bitmapBytes[] = byteStream.toByteArray();
		out.write(bitmapBytes, 0, bitmapBytes.length);
	}

	// Deserializes a byte array representing the Bitmap and decodes it
	@Deprecated
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		int b;
		while((b = in.read()) != -1)
			byteStream.write(b);
		byte bitmapBytes[] = byteStream.toByteArray();
		bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
	}
}


