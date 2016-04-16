package com.dc.logoserver;

import java.io.IOException;

public interface InputReceiver {
	String receiveInput() throws IOException;

	void close();
}
