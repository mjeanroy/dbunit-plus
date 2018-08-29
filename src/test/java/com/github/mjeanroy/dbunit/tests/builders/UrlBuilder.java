/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2018 Mickael Jeanroy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.mjeanroy.dbunit.tests.builders;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Builder for {@link URL} instances.
 */
public class UrlBuilder {

	/**
	 * URL protocol.
	 */
	private String protocol;

	/**
	 * URL host.
	 */
	private String host;

	/**
	 * URL port.
	 */
	private int port;

	/**
	 * URL path.
	 */
	private String path;

	/**
	 * Create URL with default settings.
	 */
	public UrlBuilder() {
		this.protocol = "http";
		this.host = "localhost";
		this.port = -1;
		this.path = "/";
	}

	/**
	 * Set URL protocol.
	 *
	 * @param protocol The protocol.
	 * @return The builder.
	 */
	public UrlBuilder setProtocol(String protocol) {
		this.protocol = protocol;
		return this;
	}

	/**
	 * Set URL port.
	 *
	 * @param port The port.
	 * @return The builder.
	 */
	public UrlBuilder setPort(int port) {
		this.port = port;
		return this;
	}

	/**
	 * Set URL host.
	 *
	 * @param host The host.
	 * @return The builder.
	 */
	public UrlBuilder setHost(String host) {
		this.host = host;
		return this;
	}

	/**
	 * Set URL path.
	 *
	 * @param path The path.
	 * @return The builder.
	 */
	public UrlBuilder setPath(String path) {
		this.path = path;
		return this;
	}

	/**
	 * Create {@link URL} instance.
	 *
	 * @return {@link URL} instance.
	 */
	public URL build() {
		try {
			return new URL(protocol, host, port, path);
		}
		catch (MalformedURLException ex) {
			throw new AssertionError(ex);
		}
	}
}
