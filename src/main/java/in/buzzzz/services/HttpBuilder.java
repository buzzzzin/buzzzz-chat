package in.buzzzz.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static in.buzzzz.utils.ObjectUtils.isNotEmptyMap;
import static in.buzzzz.utils.ObjectUtils.isNotEmptyObject;
import static java.util.Map.Entry;

/**
 * @author jitendra on 26/9/15.
 */
@Service
public class HttpBuilder {
    @Autowired
    CloseableHttpAsyncClient httpAsyncClient;
    @Autowired
    ObjectMapper objectMapper;

    /**
     * This method will make HTTP GET request for given uri, with request headers
     * @param uri
     * @param headers
     * @return
     */
    public String httpGet(String uri, Map<String, String> headers) {
        HttpGet get = new HttpGet(uri);
        if (isNotEmptyMap(headers)) {
            addHeaders(get, headers);
        }
        HttpEntity entity = execute(get);
        return consumeEntityAsString(entity);
    }

    /**
     * This method will make HTTP GET request without any request headers
     * @param uri
     * @return
     */
    public String httpGet(String uri) {
        return httpGet(uri, null);
    }

    public String httpPostWithJsonBody(String uri, Map<String, Object> parameters, Map<String, String> headers) {
        HttpPost post = new HttpPost(uri);
        if (isNotEmptyMap(headers)) {
            addHeaders(post, headers);
        }

        if (isNotEmptyMap(parameters)) {
            try {
                HttpEntity entity = new StringEntity(objectMapper.writeValueAsString(parameters));
                post.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return consumeEntityAsString(execute(post));
    }

    public String httpPost(String uri, Map<String, Object> parameters, Map<String, String> headers) {
        HttpPost post = new HttpPost(uri);
        if (isNotEmptyMap(headers)) {
            addHeaders(post, headers);
        }

        if (isNotEmptyMap(parameters)) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            Iterator<Entry<String, Object>> iterator = parameters.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, Object> entry = iterator.next();
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
            }
            try {
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return consumeEntityAsString(execute(post));
    }

    public String httpPost(String uri, Map<String, Object> parameters) {
        return httpPost(uri, parameters, null);
    }

    public String httpPost(String uri) {
        return httpPost(uri, null, null);
    }

    public String httpPostWithHeaders(String uri, Map<String, String> headers) {
        return httpPost(uri, null, headers);
    }

    private void addHeaders(AbstractHttpMessage message, Map<String, String> headers) {
        Iterator<Entry<String, String>> iterator = headers.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> entry = iterator.next();
            message.addHeader(entry.getKey(), entry.getValue());
        }
    }

    private String consumeEntityAsString(HttpEntity responseEntity) {
        if (isNotEmptyObject(responseEntity)) {
            try {
                return EntityUtils.toString(responseEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private HttpEntity execute(HttpUriRequest method) {
        httpAsyncClient.start();
        Future<HttpResponse> responseFuture = httpAsyncClient.execute(method, null);
        try {
            return responseFuture.get().getEntity();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
