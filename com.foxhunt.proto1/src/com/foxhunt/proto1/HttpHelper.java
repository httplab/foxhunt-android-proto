package com.foxhunt.proto1;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 08.01.12
 * Time: 2:29
 * To change this template use File | Settings | File Templates.
 */
public class HttpHelper
{
	public static String _getResponseBody(final HttpEntity entity) throws IOException, ParseException
	{

		if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }

		InputStream instream = entity.getContent();

		if (instream == null) { return ""; }

		if (entity.getContentLength() > Integer.MAX_VALUE) { throw new IllegalArgumentException(

				"HTTP entity too large to be buffered in memory"); }

		String charset = getContentCharSet(entity);

		if (charset == null) {

			charset = HTTP.DEFAULT_CONTENT_CHARSET;

		}

		Reader reader = new InputStreamReader(instream, charset);

		StringBuilder buffer = new StringBuilder();

		try {

			char[] tmp = new char[1024];

			int l;

			while ((l = reader.read(tmp)) != -1) {

				buffer.append(tmp, 0, l);

			}

		} finally {

			reader.close();

		}

		return buffer.toString();

	}

	public static String getContentCharSet(final HttpEntity entity) throws ParseException {

		if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }

		String charset = null;

		if (entity.getContentType() != null) {

			HeaderElement values[] = entity.getContentType().getElements();

			if (values.length > 0) {

				NameValuePair param = values[0].getParameterByName("charset");

				if (param != null) {

					charset = param.getValue();

				}

			}

		}

		return charset;

	}
}
